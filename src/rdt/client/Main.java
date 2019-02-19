package rdt.client;

import rdt.client.fileSystem.FileSystem;
import rdt.client.fileSystem.Subject;
import rdt.client.progressBar.ProgressDemo;
import rdt.util.FileUtils;

import java.io.File;

public class Main {
    public static void main(String[] args) {

//        NetworkFileSystem.init();

//        fillFileSystem();

//        long one = System.currentTimeMillis() / 1000;
//        FileUtils.ppt2img("pres.pptx", "test");
//        System.out.println("time : " + (System.currentTimeMillis() / 1000 - one));

        MainFrame.launch();

//        File file = new File("test");
//        file.renameTo(new File("test_copy"));
//        System.out.println(FileSystem.deleteFolder(file));

    }

    private static void fillFileSystem(){

        String[] nameOfSubject = {
                "Тактика",
                "ВСП",
                "ОВП"
        };

        String[] nameOfChapter = {
                "Глава",
                "Раздел",
                "Занятие"
        };

        Subject[] subjects = {new Subject(nameOfSubject[0], nameOfChapter),new Subject(nameOfSubject[1], nameOfChapter), new Subject(nameOfSubject[2], nameOfChapter)};

        int[][] classPath = {
                {0},
                {1},
                {0, 0},
                {0, 1},
                {1, 0},
                {0, 1, 0}
        };

        subjects[0].addClass("Глава 1", classPath[0]);
        subjects[0].addClass("Глава 2", classPath[1]);
        subjects[0].addClass("Раздел 1.1", classPath[2]);
        subjects[0].addClass("Раздел 1.2", classPath[3]);
        subjects[0].addClass("Раздел 2.1", classPath[4]);
        subjects[0].addClass("Занятие 1.2.1", classPath[5]);

        int[][] filePath = {
                {0},
                {1},
                {0, 0},
                {0, 1},
                {1, 0},
                {0, 1, 0},
                {}
        };

//        subjects[0].addFile(filePath[0], "file1");
//        subjects[0].addFile(filePath[1], "file21");
//        subjects[0].addFile(filePath[2], "file1.1");
//        subjects[0].addFile(filePath[3], "file1.2");
//        subjects[0].addFile(filePath[4], "file2.1");
//        subjects[0].addFile(filePath[5], "file1.2.11");
//        subjects[0].addFile(filePath[1], "file22");
//        subjects[0].addFile(filePath[5], "file1.2.12");
//        subjects[0].addFile(filePath[6], "azazazazazazazazazazaza");

        for (int i = 0; i < subjects.length; i++) {
            FileSystem.addSubject(subjects[i]);
        }

    }
    
}
