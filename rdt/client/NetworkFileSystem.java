package rdt.client;

import rdt.net.DataByteBuffer;
import rdt.net.DataPacket;
import rdt.net.NetworkClient;
import rdt.util.Logger;

import java.io.IOException;
import java.net.Socket;

public class NetworkFileSystem {

    public static void init() {
        try {
            NetworkClient networkClient = new NetworkClient(new Socket("10.11.162.146", 13197));
            fillFileSystem(networkClient);
        } catch (IOException e) {
            Logger.logError(NetworkFileSystem.class, e);
        }
    }

    public static void fillFileSystem(NetworkClient networkClient){

        networkClient.sendPacket(DataPacket.requestSubjectsPacket());
        networkClient.waitForPackets();

        DataPacket packet = networkClient.getPacket();
        DataByteBuffer dataByteBuffer = packet.getData();
        String[] subjectName = dataByteBuffer.getStringArray();

        for (int i = 0; i < subjectName.length; i++) {
            System.out.println(subjectName);
        }



    }




}
