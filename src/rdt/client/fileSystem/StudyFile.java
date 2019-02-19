package rdt.client.fileSystem;

import rdt.util.Utils;
import java.io.File;

public class StudyFile {

    private String type;
    private int[] path;
    private String name;
    private String hexCode;
    private String rootDir;

    public String getHexCode() {
        return hexCode;
    }

    public String getRootDir(){return rootDir;}

    public String getGlobalPath(){
        return rootDir + "\\" + hexCode + "." + type;
    }

    StudyFile(int[] path, String name, String hexCode, String type, String rootDir) {
        this.type = type;
        this.path = path;
        this.name = name;
        this.hexCode = hexCode;
        this.rootDir = rootDir;
    }

    StudyFile(int[] path, String text) {

//        this.hexCode = Long.toHexString(this.hashCode());

        this.path = path;
        String[] arrStr = text.split("\\.");
        if (arrStr.length == 1) {
            this.type = "Файл";
            this.name = text;
            return;
        }
        this.type = arrStr[arrStr.length - 1];
        String name = "";
        for (int i = 0; i < arrStr.length - 1; i++) {
            name += arrStr[i] + (i < arrStr.length - 2 ? "." : "");
        }
        this.name = name;
    }

    void init(String path){
        this.hexCode = Utils.getHash(path + "\\buffer");
        this.rootDir = path;
        File file = new File(path + "\\buffer");
        if (!file.renameTo(new File(path + "\\" + hexCode + "." + type))) {
            file.delete();
        }

    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int[] getPath() {
        return path;
    }

    void rename(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
