package rdt.client;

import rdt.client.fileSystem.FileSystem;
import rdt.net.DataByteBuffer;
import rdt.net.DataPacket;
import rdt.net.NetworkClient;
import rdt.platform.backend.DataFile;
import rdt.platform.backend.HeadPath;
import rdt.platform.backend.Subject;
import rdt.util.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class NetworkFileSystem {
//
//    public static void init() {
//        try {
//            NetworkClient networkClient = new NetworkClient(new Socket("10.11.162.146", 13197));
//            fillFileSystem(networkClient);
//        } catch (IOException e) {
//            Logger.logError(e);
//        }
//    }

//    public static void fillFileSystem(NetworkClient networkClient){
//
//        networkClient.sendPacket(DataPacket.requestSubjectsPacket());
//        networkClient.waitForPackets();
//
//        DataPacket packet = networkClient.getPacket();
//        DataByteBuffer dataByteBuffer = packet.getData();
//        String[] subjectName = dataByteBuffer.getStringArray();
//
//        for (int i = 0; i < subjectName.length; i++) {
//
//            networkClient.sendPacket(DataPacket.requestSubjectPacket(subjectName[i]));
//            networkClient.waitForPackets();
//
//            DataPacket packet1 = networkClient.getPacket();
//            DataByteBuffer dataByteBuffer1 = packet1.getData();
//            FileSystem.addSubject(new rdt.client.fileSystem.Subject(dataByteBuffer1.getSubject()));
//
//        }
//
//        networkClient.sendPacket(DataPacket.requestFilesPacket("Взрыв мозга", "QWE123"));
//        networkClient.waitForPackets();
//
//        DataPacket dataPacket = networkClient.getPacket();
//        DataFile data = dataPacket.getData().getDataFileArray(new String[]{"C:\\Users\\RealityShift22\\Videos"})[0];
//        data.writeToHDD();
//
//
//
//        try {
//            networkClient.close();
//        } catch (IOException e) {
//            Logger.logError(e);
//        }
//
//    }

}

