package rdt.client.fileSystem;

public class HeadClass {

    private String name;
    private int[] path;
    private Subject parentSubject;

    public HeadClass(String name, int[] path, Subject parentSubject) {
        this.name = name;
        this.path = path;
        this.parentSubject = parentSubject;
    }

    public Subject getParentSubject() {
        return parentSubject;
    }

    public String getName() {
        return name;
    }

    public int[] getPath() {
        return path;
    }

    public boolean equals(int[] classPath) {
        for (int i = 0; i < classPath.length; i++) {
            if(this.path[i] == classPath[i])
                return true;
        }
        return false;
    }

    public void rename(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        int lvl = path.length - 1;
        if (lvl == -1) {
            return name;
        }
        else {
            String nameOfChapter = parentSubject.getNameOfChapter()[lvl];
            int num = path[lvl] + 1;
            return nameOfChapter + " №" + num + " " + name;
        }
    }
}
