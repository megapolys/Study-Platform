package rdt.client.progressBar;

import javafx.concurrent.Task;
import rdt.client.fileSystem.FileSystem;

import java.io.*;
import java.util.ArrayList;

public class PackFile extends Task<Byte> {

    private String inputPath;
    private String outputPath;

    private void packFilesRec(String currentPath, String relativePath, ArrayList<String> pathes, ArrayList<File> files) throws IOException {

        File currentFile = new File(currentPath);
        if (currentFile.isDirectory()) {

            String[] fileNames = currentFile.list();
            for (int i = 0; i < fileNames.length; i++)
                packFilesRec(currentPath + "/" + fileNames[i], relativePath + "/" + currentFile.getName(), pathes, files);

        } else {

            pathes.add(relativePath + "/" + currentFile.getName());
            files.add(currentFile);

        }

    }

    public PackFile(String inputPath, String outputPath){
        this.inputPath = inputPath;
        this.outputPath = outputPath;
    }

    @Override
    protected Byte call() {

        try {
            FileSystem.saveConfig();

            File result = new File(outputPath);
            if (!result.exists())
                result.createNewFile();

            ArrayList<String> pathes = new ArrayList<String>();
            ArrayList<File> files = new ArrayList<File>();

            packFilesRec(inputPath, "", pathes, files);

            DataOutputStream output = new DataOutputStream(new FileOutputStream(result));
            output.writeInt(0xCAFEFACE);
            output.writeInt(pathes.size());

            for (int i = 0; i < pathes.size(); i++) {

                byte[] pathBytes = pathes.get(i).getBytes();

                output.writeInt(pathBytes.length);
                output.write(pathBytes);

            }

            output.flush();

            for (int i = 0; i < files.size(); i++) {
//                Logger.log("Exporting" + i + "  " + files.get(i).getPath());
                this.updateMessage("Копирование : " + files.get(i).getPath());
                output.writeInt((int) files.get(i).length());

                FileInputStream input = new FileInputStream(files.get(i));
                byte[] buffer = new byte[512];

                int read;
                while ((read = input.read(buffer)) != -1)
                    output.write(buffer, 0, read);

                input.close();
                output.flush();

                this.updateProgress(i + 1, files.size());
            }

            output.close();


        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        this.updateMessage("Операция выполнена успешно!");

        return new Byte((byte)0);
    }
}
