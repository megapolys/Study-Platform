package rdt.client.fileSystem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import rdt.client.MainFrame;
import rdt.client.progressBar.PackFile;
import rdt.client.progressBar.ProgressDemo;
import rdt.client.progressBar.UnpackFile;
import rdt.util.FileUtils;
import rdt.util.Logger;
import java.io.*;
import java.util.ArrayList;

public class FileSystem {

    private static ArrayList<Subject> subjects = new ArrayList<>();

    final static String rootDir = "data";




    public static void init(){

        File root = new File(rootDir);
        root.mkdir();
        File config = new File(root + "\\config");
        try {
            if (!config.exists())
            config.createNewFile();
        } catch (IOException e) {
            Logger.logError(e);
        }

    }

    public static int getCountOfSubject(){
        return subjects.size();
    }

    public static void addSubject(Subject subject) {
        subjects.add(subject);
    }

    public static ArrayList<Subject> getSubjects() {
        return subjects;
    }

    public static boolean isSubjNameExists(String text){
        for (int i = 0; i < subjects.size(); i++)
            if (subjects.get(i).getNameOfSubject().equals(text))
                return true;
        return false;
    }

    public static int getSubjectIndex(String name){
        for (int i = 0; i < subjects.size(); i++) {
            if (subjects.get(i).getNameOfSubject().equals(name))
                return i;
        }
        return 0;
    }

    public static int getSubjectIndex(Subject subject){
        return subjects.indexOf(subject);
    }

    public static boolean deleteSubject(int index){
        Logger.log("Delete subject");
        Subject subject = subjects.get(index);
        File root = new File(subject.getPath());
        if (deleteFolder(root)){
            subjects.remove(subject);
            return true;
        }
        return false;
    }

    public static void saveConfig(){

        byte[] allBuffer = FileSystem.packSubjects();
        File config = new File(rootDir + "\\config");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(config);
            fileOutputStream.write(allBuffer, 0, allBuffer.length);
            fileOutputStream.close();
        } catch (IOException e) {
            Logger.logError(e);
        }

    }

    private static byte[] packSubjects(){
        MyByteBuffer myByteBuffer = new MyByteBuffer();
        int size = subjects.size();
        myByteBuffer.addInt(size);
        for (int i = 0; i < size; i++) {

            Subject subject = subjects.get(i);
            myByteBuffer.addString(subject.getNameOfSubject());
            myByteBuffer.addStringArray(subject.getNameOfChapter());
            myByteBuffer.addInt(subject.getHashCode());

            ArrayList<HeadClass> headClasses = subject.getClasses();
            int sizeClasses = headClasses.size();
            myByteBuffer.addInt(sizeClasses);
            for (int j = 0; j < sizeClasses; j++) {
                HeadClass headClass = headClasses.get(j);
                myByteBuffer.addIntArray(headClass.getPath());
                myByteBuffer.addString(headClass.getName());
            }

            ArrayList<StudyFile> subjectFiles = subject.getFiles();
            int sizeFiles = subjectFiles.size();
            myByteBuffer.addInt(sizeFiles);
            for (int j = 0; j < sizeFiles; j++) {
                StudyFile studyFile = subjectFiles.get(j);
                myByteBuffer.addIntArray(studyFile.getPath());
                myByteBuffer.addString(studyFile.getName());
                myByteBuffer.addString(studyFile.getHexCode());
                myByteBuffer.addString(studyFile.getType());
            }

        }
        return myByteBuffer.getBytes();
    }

    public static void loadConfig(){
        File dataDir = new File(rootDir);
        if (!dataDir.exists())
            return;
        File allConfig = new File(rootDir + "\\config");
        try(FileInputStream fin = new FileInputStream(allConfig)) {
            int len = fin.available();
            byte[] allBuffer = new byte[len];
            fin.read(allBuffer, 0, len);
            ArrayList<Subject> subjects = FileSystem.unpackSubjects(allBuffer);
            FileSystem.subjects.addAll(subjects);
        } catch (IOException e) {
            Logger.logError(e);
        }

    }

    private static ArrayList<Subject> unpackSubjects(byte[] bytes)   {
        MyByteBuffer myByteBuffer = new MyByteBuffer(bytes);
        int size = myByteBuffer.getInt();
        ArrayList<Subject> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {

            String nameOfSub = myByteBuffer.getString();
            String[] nameOfChapter = myByteBuffer.getStringArray();
            int hashCode = myByteBuffer.getInt();
            Subject subject = new Subject(
                    nameOfSub,
                    nameOfChapter,
                    hashCode
            );
            
            int sizeClasses = myByteBuffer.getInt();
            for (int j = 0; j < sizeClasses; j++) {
                int[] path = myByteBuffer.getIntArray();
                String name = myByteBuffer.getString();
                subject.addClass(new HeadClass(name, path, subject));
            }
            
            int sizeFiles = myByteBuffer.getInt();
            for (int j = 0; j < sizeFiles; j++) {
                int[] path = myByteBuffer.getIntArray();
                String name = myByteBuffer.getString();
                String hexCode = myByteBuffer.getString();
                String type = myByteBuffer.getString();
                subject.addFile(new StudyFile(path, name, hexCode, type, subject.getPath()));
            }
            
            result.add(subject);
        }
        return result;
    }

    public static void importing(String path){
//        long secStart = System.currentTimeMillis() / 1000;
//        FileUtils.unpackFiles(path, "import");
        Logger.log("Import");
        ProgressDemo progressDemo = new ProgressDemo(ProgressDemo.BarType.BAR_TYPE);
        UnpackFile task = new UnpackFile(path, "data\\import");
        progressDemo.start(task, MainFrame.getStage());

//        long secEnd = System.currentTimeMillis() / 1000;
//        Logger.log("time = " + (secEnd - secStart));
    }

    public static void exporting(String path){

        Logger.log("Export");
//        long secStart = System.currentTimeMillis() / 1000;
        ProgressDemo progressDemo = new ProgressDemo(ProgressDemo.BarType.BAR_TYPE);
        progressDemo.start(new PackFile(rootDir, path), MainFrame.getStage());
//        long secEnd = System.currentTimeMillis() / 1000;
//        Logger.log("time = " + (secEnd - secStart));


//        FileUtils.packFiles(rootDir, path);

    }

    public static boolean deleteFolder(File rootDir){

        if (!rootDir.exists()) {
            return false;
        }
        File[] files = rootDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                if (!deleteFolder(file)) {
                    return false;
                }
            }
            else{
                if (!file.delete()) {
                    return false;
                }
            }
        }

        return rootDir.delete();
    }

    public static boolean merger(String dataPath){

        File config = new File(dataPath + "\\config");
        ArrayList<Subject> subjects;
        if (!config.exists())
            return false;
        try(FileInputStream fin = new FileInputStream(config)) {
            int len = fin.available();
            byte[] allBuffer = new byte[len];
            fin.read(allBuffer, 0, len);
            subjects = FileSystem.unpackSubjects(allBuffer);
        } catch (IOException e) {
            Logger.logError(e);
            return false;
        }

        for (int i = 0; i < subjects.size(); i++) {
            Subject subject = subjects.get(i);
            Subject oldSubject;
            int hashCode = subject.getHashCode();
            File oldDir = new File(rootDir + "\\" + hashCode);
            File newDir = new File(dataPath + "\\" + hashCode);
            if ((oldSubject = isSubjectExists(hashCode)) == null){
                FileSystem.subjects.add(subject);
            }
            else {
                int index = FileSystem.subjects.indexOf(oldSubject);
                FileSystem.deleteSubject(index);
                FileSystem.subjects.add(index, subject);
            }
            if (!newDir.exists()){
                oldDir.mkdir();
            }
            else{
                newDir.renameTo(oldDir);
            }
        }

        return true;

    }

    private static Subject isSubjectExists(int hashCode){
        for (int i = 0; i < subjects.size(); i++) {
            Subject subject = subjects.get(i);
            if (subject.getHashCode() == hashCode)
                return subject;
        }
        return null;
    }

}
