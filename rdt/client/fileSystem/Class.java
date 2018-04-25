package rdt.client.fileSystem;

import java.util.ArrayList;

public class Class {

    private String nameOfClass;
    private int[] classPath;
    private Subject parentSubject;

    public void setParentSubject(Subject parentSubject) {
        this.parentSubject = parentSubject;
    }

    public Subject getParentSubject() {
        return parentSubject;
    }

    public String getName() {
        return nameOfClass;
    }

    public int[] getClassPath() {
        return classPath;
    }

    public Class(String nameOfClass, int[] classPath){
        this.nameOfClass = nameOfClass;
        this.classPath = classPath;
    }

    public boolean equals(int[] classPath) {
        for (int i = 0; i < classPath.length; i++) {
            if(this.classPath[i] == classPath[i])
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        int lvl = classPath.length - 1;
        if (lvl == -1) {
            return "Предмет.  " + nameOfClass;
        }
        else {
            String nameOfChapter = parentSubject.getNameOfChapter()[lvl];
            int num = classPath[lvl] + 1;
            return nameOfChapter + "№" + num + " " + nameOfClass;
        }
    }
}
