package rdt.client;

import rdt.client.fileSystem.FileSystem;
import rdt.client.fileSystem.Subject;

public class Main {
    public static void main(String[] args) {

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

        FileSystem.setSubjects(subjects);

        MainFrame.launch();
    }
    
}
