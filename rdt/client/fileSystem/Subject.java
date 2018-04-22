package rdt.client.fileSystem;

import java.util.ArrayList;

public class Subject {

    private String nameOfSubject;
    private String[] nameOfChapter;
    private ArrayList<Class> classes = new ArrayList<>();
    private File[] files;

    public ArrayList<Class> getClasses(){
        return classes;
    }

    public Subject(String nameOfSubject, String[] nameOfChapter) {
        this.nameOfSubject = nameOfSubject;
        this.nameOfChapter = nameOfChapter;
    }

    public Subject(String nameOfSubject) {
        this.nameOfSubject = nameOfSubject;
    }

    public String getNameOfSubject() {
        return nameOfSubject;
    }

    public String[] getNameOfChapter() {
        return nameOfChapter;
    }

    public void addClass(String nameOfClass, int[] classPath){
        classes.add(new Class(nameOfClass, classPath));
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
}
