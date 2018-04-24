package rdt.client.fileSystem;

public class File {

    private int typeOfFile;
    private int[] filePath;
    private String nameOfFile;
    private String hashCode;
    private String filePathString;

    public File(int typeOfFile, int[] filePath, String nameOfFile) {
        this.typeOfFile = typeOfFile;
        this.filePath = filePath;
        this.nameOfFile = nameOfFile;
    }

    public String getNameOfFile() {
        return nameOfFile;
    }

    public int getTypeOfFile() {
        return typeOfFile;
    }

    public int[] getFilePath() {
        return filePath;
    }
}
