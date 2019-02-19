package rdt.client.progressBar;

import javafx.concurrent.Task;
import rdt.client.fileSystem.FileSystem;

import java.io.*;

public class UnpackFile extends Task<Byte> {

    private String inputPath;
    private String outputPath;

    public UnpackFile(String inputPath, String outputPath) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;
    }

    @Override

    protected Byte call() throws Exception {

        this.updateValue(new Byte((byte) 1));

        File dir = new File(outputPath);
        dir.mkdir();

        this.updateMessage("Распаковка файлов");
        unpack();

        this.updateMessage("Слияние систем");
        FileSystem.merger(dir + "\\data");

        this.updateMessage("Удаление излишек");
        FileSystem.deleteFolder(dir);

        FileSystem.saveConfig();
        this.updateMessage("Операция успешно завершена!");
        this.updateProgress(1, 1);

        return new Byte((byte) 1);
    }

    private void unpack(){

        try {

            File inputFile = new File(inputPath);
            DataInputStream input = new DataInputStream(new FileInputStream(inputFile));

            int magic = input.readInt();
            if (magic != 0xCAFEFACE) {
                input.close();
            }

            int filesNum = input.readInt();
            String[] pathes = new String[filesNum];

            for (int i = 0; i < filesNum; i++) {

                int pathLength = input.readInt();
                byte[] pathBytes = new byte[pathLength];

                input.read(pathBytes);

                pathes[i] = new String(pathBytes);

            }

            for (int i = 0; i < filesNum; i++) {

                int fileLength = input.readInt();
                byte[] fileBytes = new byte[fileLength];

                input.read(fileBytes);

                File outputFile = new File(outputPath + pathes[i]);
                if (!outputFile.exists()) {
                    new File(outputFile.getPath().substring(0, outputFile.getPath().lastIndexOf('\\'))).mkdirs();
                    outputFile.createNewFile();
                }
//                this.updateMessage(outputFile.getName());

                FileOutputStream output = new FileOutputStream(outputFile);
                output.write(fileBytes);
                output.close();
                this.updateProgress(i, filesNum);

            }

            input.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }
}
