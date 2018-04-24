package rdt.client.fileSystem;

import java.util.ArrayList;

public class Class {

    private String nameOfClass;
    private int[] classPath;

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
        return nameOfClass;
    }
}
