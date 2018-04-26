package rdt.client.fileSystem;

import rdt.platform.backend.FileDescription;

import java.util.ArrayList;

public class Subject {

    private String nameOfSubject;
    private String[] nameOfChapter;
    private ArrayList<Class> classes = new ArrayList<>();
    private ArrayList<File> files = new ArrayList<>();

    public Subject(rdt.platform.backend.Subject subject){

        this.nameOfSubject = subject.getName();
        this.nameOfChapter = subject.getLevels();
        int[][] headPathes = subject.getHeadPathes();
        for (int i = 0; i < headPathes.length; i++) {
            int[] headPathe = headPathes[i];
            classes.add(new Class(subject.getHeadElement(headPathe), headPathe));
        }

        FileDescription[] fileDescriptions = subject.getFileDescriptions();

        for (int i = 0; i < fileDescriptions.length; i++) {
            FileDescription fileDescription = fileDescriptions[i];
            files.add(new File(fileDescription.getType(), fileDescription.getPath(), fileDescription.getName()));
        }

    }

    public ArrayList<Class> getClasses(){
        return classes;
    }

    public Subject(String nameOfSubject, String[] nameOfChapter) {
        this.nameOfSubject = nameOfSubject;
        this.nameOfChapter = nameOfChapter;
    }

    public String getNameOfSubject() {
        return nameOfSubject;
    }

    public String[] getNameOfChapter() {
        return nameOfChapter;
    }

    public void addClass(String nameOfClass, int[] classPath){
        Class aClass = new Class(nameOfClass, classPath);
        aClass.setParentSubject(this);
        classes.add(aClass);
    }

    public ArrayList<Class> getUnderClasses(int[] chapter){
        ArrayList<Class> res = new ArrayList<>();
        boolean flag = true;
        for (Class cl :
                classes) {
            int[] classPath = cl.getClassPath();
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

    public ArrayList<File> getFiles(Class cl){

        int[] path = cl.getClassPath();

        ArrayList<File> arrayList = new ArrayList<>();

        boolean flag = true;

        for (int i = 0; i < files.size(); i++) {
            int[] filePath = files.get(i).getFilePath();
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

    public void addFile(int typeOfFile, int[] filePath, String nameOfFile){
        files.add(new File(typeOfFile, filePath, nameOfFile));
    }

    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }
}
