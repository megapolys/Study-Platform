package rdt.client.fileSystem;

public class FileSystem {

    private static Subject[] subjects;

    public static int getCountOfSubject(){
        return subjects.length;
    }

    public static void setSubjects(Subject[] subjects) {
        FileSystem.subjects = subjects;
    }

    public static Subject[] getSubjects() {
        return subjects;
    }
}
