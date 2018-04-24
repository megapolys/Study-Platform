package rdt.client;

import rdt.net.DataPacket;
import rdt.net.NetworkClient;
import rdt.util.Logger;

import java.io.IOException;
import java.net.Socket;

public class NetworkFileSystem {

    public static void init() {
        try {
            NetworkClient networkClient = new NetworkClient(new Socket("10.11.162.146", 13197));
            networkClient.sendPacket(DataPacket.requestAddSubjectPacket("ТактикаTaktika"));
        } catch (IOException e) {
            Logger.logError(NetworkFileSystem.class, e);
        }
    }


}
