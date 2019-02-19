package rdt.client.fileSystem;

import javafx.scene.control.Alert;
import rdt.client.MainFrame;
import rdt.platform.backend.FileDescription;
import rdt.util.Logger;
import rdt.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class Subject {

    private String nameOfSubject;
    private String[] nameOfChapter;
    private ArrayList<HeadClass> classes = new ArrayList<>();
    private ArrayList<StudyFile> files = new ArrayList<>();
    private int hashCode;

//    public Subject(rdt.platform.backend.Subject subject){
//
//        this.nameOfSubject = subject.getName();
//        this.nameOfChapter = subject.getLevels();
//        int[][] headPathes = subject.getHeadPathes();
//        for (int i = 0; i < headPathes.length; i++) {
//            int[] headPathe = headPathes[i];
//            HeadClass aClass = new HeadClass(subject.getHeadElement(headPathe), headPathe, this);
//            classes.add(aClass);
//        }
//
//        FileDescription[] fileDescriptions = subject.getFileDescriptions();
//
//        for (int i = 0; i < fileDescriptions.length; i++) {
//            FileDescription fileDescription = fileDescriptions[i];
//            files.add(new StudyFile(fileDescription.getPath(), fileDescription.getName()));
//        }
//
//    }

    public ArrayList<HeadClass> getClasses(){
        return classes;
    }

    public Subject(String nameOfSubject, String[] nameOfChapter) {
        this.nameOfSubject = nameOfSubject;
        this.nameOfChapter = nameOfChapter;
        this.hashCode = this.hashCode();
        File root = new File(getPath());
        root.mkdir();
        Logger.log("Create new subject");
    }

    public Subject(String nameOfSubject, String[] nameOfChapter, int hashCode) {
        this.nameOfSubject = nameOfSubject;
        this.nameOfChapter = nameOfChapter;
        this.hashCode = hashCode;
    }

    public String getPath(){
        return FileSystem.rootDir + "\\" + hashCode;
    }

    public String getNameOfSubject() {
        return nameOfSubject;
    }

    public String[] getNameOfChapter() {
        return nameOfChapter;
    }

    public void addClass(String nameOfClass, int[] classPath){
        HeadClass aClass = new HeadClass(nameOfClass, classPath, this);
        classes.add(aClass);
    }

    public void addClass(String nameOfClass, int[] classPath, Subject subject){
        HeadClass aClass = new HeadClass(nameOfClass, classPath, subject);
        classes.add(aClass);
    }

    public void addClass(HeadClass headClass){
        classes.add(headClass);
    }

    public ArrayList<HeadClass> getUnderClasses(int[] chapter){
        ArrayList<HeadClass> res = new ArrayList<>();
        boolean flag = true;
        for (HeadClass cl :
                classes) {
            int[] classPath = cl.getPath();
            if (classPath.length == chapter.length + 1)
                for (int i = 0; i < chapter.length; i++) {
                    if (chapter[i] != classPath[i]) flag = false;
                }
            else flag = false;
            if (flag) res.add(cl);
            else flag = true;
        }
        return res;
    }

    public ArrayList<StudyFile> getFiles(HeadClass headClass){

        int[] path = headClass.getPath();

        ArrayList<StudyFile> arrayList = new ArrayList<>();

        boolean flag = true;

        for (int i = 0; i < files.size(); i++) {
            int[] filePath = files.get(i).getPath();
            if (path.length > filePath.length)
                flag = false;
            else for (int j = 0; j < path.length; j++) {
                if (path[j] != filePath[j])
                    flag = false;
            }
            if (flag) arrayList.add(files.get(i));
            flag = true;
        }

        return arrayList;
    }

    public ArrayList<StudyFile> getFiles(){
        return files;
    }

    public void setFiles(ArrayList<StudyFile> files) {
        this.files = files;
    }

    public void rename(String newName){
        nameOfSubject = newName;
    }

    public void removeClass(HeadClass headClass){

        int[] path = headClass.getPath();

        ArrayList<HeadClass> arrayList = new ArrayList<>();

        boolean flag = true;

        for (int i = 0; i < classes.size(); i++) {
            int[] filePath = classes.get(i).getPath();
            if (path.length > filePath.length)
                flag = false;
            else for (int j = 0; j < path.length; j++) {
                if (path[j] != filePath[j])
                    flag = false;
            }
            if (flag) arrayList.add(classes.get(i));
            flag = true;
        }

        for (int i = 0; i < arrayList.size(); i++) {
            classes.remove(arrayList.get(i));
        }

        ArrayList<StudyFile> arrayListF = this.getFiles(headClass);

        for (int i = 0; i < arrayListF.size(); i++) {
            deleteFile(arrayListF.get(i).getHexCode());
        }

        for (int i = 0; i < classes.size(); i++) {
            int[] filePath = classes.get(i).getPath();
            if (path.length > filePath.length)
                flag = false;
            else {
                for (int j = 0; j < path.length - 1; j++) {
                    if (path[j] != filePath[j])
                        flag = false;
                }
                if (path[path.length - 1] > filePath[path.length - 1]) flag = false;
            }
            if (flag) filePath[path.length - 1]--;
            flag = true;
        }

        for (int i = 0; i < files.size(); i++) {
            int[] filePath = files.get(i).getPath();
            if (path.length > filePath.length)
                flag = false;
            else {
                for (int j = 0; j < path.length - 1; j++) {
                    if (path[j] != filePath[j])
                        flag = false;
                }
                if (path[path.length - 1] >= filePath[path.length - 1]) flag = false;
            }
            if (flag) filePath[path.length - 1]--;
            flag = true;
        }

    }

    public void setNameOfChapter(String[] chapter){
        nameOfChapter = chapter;
    }

    public int getHashCode() {
        return hashCode;
    }

    public StudyFile addFile(int[] path, File file){
        StudyFile studyFile = new StudyFile(path, file.getName());
        String hexCode = Utils.getHash(file.getPath());
        if (isFileExists(hexCode) == 0) {
            try (FileInputStream fin = new FileInputStream(file)) {
                File fileOut = new File(getPath() + "\\buffer");
                FileOutputStream fout = new FileOutputStream(fileOut);
                byte[] buffer = new byte[512];
                int read;
                while ((read = fin.read(buffer)) != -1)
                    fout.write(buffer, 0, read);
                fout.close();
            } catch (IOException e) {
                Logger.logError(e);
            }
            studyFile.init(getPath());
        }
        else {
            StudyFile newStudyFile = new StudyFile(path, studyFile.getName(), hexCode, studyFile.getType(), getPath());
            studyFile = newStudyFile;
        }
        files.add(studyFile);
        return studyFile;
    }

    void addFile(StudyFile studyFile){
        files.add(studyFile);
    }

    public boolean deleteFile(String hexCode){
        StudyFile studyFile = getFile(hexCode);
        File file = new File(studyFile.getGlobalPath());
        if (isFileExists(hexCode) == 1) {
            if (file.delete()) {
                files.remove(studyFile);
            }
            else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Внимание!");
                alert.setHeaderText("Файл невозможно удалить");
                alert.setContentText("Проверьте, открыт ли данный файл в приложении и попробуейте снова");
                alert.show();
                return false;
            }
        }
        else{
            files.remove(studyFile);
        }
        return true;
    }

    public void renameFile(String hexCode, String name){
        getFile(hexCode).rename(name);
    }

    int isFileExists(String hexCode){
        int res = 0;
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).getHexCode().equals(hexCode)) {
                res++;
            }
        }
        return res;
    }

    public StudyFile getFile(String hexCode){
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).getHexCode().equals(hexCode))
                return files.get(i);
        }
        return null;
    }

    @Override
    public String toString() {
        return nameOfSubject;
    }
}
