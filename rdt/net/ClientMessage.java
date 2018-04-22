package rdt.net;

public class ClientMessage {
	
	private NetworkClient client;
	private DataPacket packet;

	public ClientMessage(NetworkClient client, DataPacket packet) {
		this.packet = packet;
		this.client = client;
	}
	
	public void send() {
		client.sendPacket(packet);
	}
	
	public NetworkClient getClient() {
		return this.client;
	}
	
	public DataPacket getPacket() {
		return this.packet;
	}
	
}
