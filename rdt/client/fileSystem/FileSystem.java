package rdt.client.fileSystem;

import rdt.util.Logger;

import java.util.ArrayList;

public class FileSystem {

    private static ArrayList<String> typeList = new ArrayList<>();

    public static void init(){
        typeList.add("Презентация");
        typeList.add("Видео");
        typeList.add("Файл");
    }

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

    public static String alterType(int type) {
        return typeList.get(type);
    }

}
