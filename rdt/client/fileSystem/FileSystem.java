package rdt.client.fileSystem;

import rdt.util.Logger;

import java.util.ArrayList;

public class FileSystem {

    private static ArrayList<String> typeList = new ArrayList<>();

    private static ArrayList<Subject> subjects = new ArrayList<>();

    public static void init(){
        typeList.add("Презентация");
        typeList.add("Видео");
        typeList.add("Файл");
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

    public static String alterType(int type) {
        return typeList.get(type);
    }

    public static int getIntType(String value){
        for (int i = 0; i < typeList.size(); i++) {
            if (value.equals(typeList.get(i)))
                return i;
        }
        return -1;
    }

}
