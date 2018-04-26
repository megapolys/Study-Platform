package rdt.main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import rdt.net.ClientMessage;
import rdt.net.DataByteBuffer;
import rdt.net.DataPacket;
import rdt.net.NetworkServer;
import rdt.platform.backend.DataFile;
import rdt.platform.backend.Subject;
import rdt.util.Logger;

public class ServerMain {
	
	private NetworkServer server;
	
	private ArrayList<Subject> subjects;

	public ServerMain() {
		
		setup();
		
		while (!Thread.interrupted()) {
			
			loop();
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				Logger.logError(e);
				System.exit(-1);
			}
			
		}
		
	}
	
	public void setup() {
		
		this.server = new NetworkServer(13197);
		
		this.subjects = new ArrayList<Subject>();
		
		/*Subject subjectTactic = new Subject("Общая тактика");
		Subject subjectBrExp = new Subject("Взрыв мозга");
		
		subjectTactic.addLevel("Тема");
		subjectTactic.addLevel("Занятие");
		
		subjectBrExp.addLevel("Хуема");
		subjectBrExp.addLevel("Хуенятие");
		
		subjectBrExp.addHeadElement(new int[] {0}, "Занятие бла бла");
		subjectBrExp.addHeadElement(new int[] {0, 1}, "Занятие бла ну ещё");
		
		subjects.add(subjectTactic);
		subjects.add(subjectBrExp);
		
		save();*/
		
		load();
		
		//Logger.log(subjects.get(1).getHeadElement(new int[] {0}));
		
	}
	
	public void loop() {
		
		while (server.hasMessages()) {
			
			ClientMessage message = server.getMessage();
			DataByteBuffer data = message.getPacket().getData();
			
			switch (message.getPacket().getType()) {
			
			case 100: { //получение описания премета
					
					String subjectName = data.getString();
					for (int i = 0; i < subjects.size(); i++) {
						if (subjects.get(i).getName().equals(subjectName)) {
							
							DataPacket packet = DataPacket.responseSubjectPacket(subjects.get(i));
							message.getClient().sendPacket(packet);
							
							break;
						}
					}
					
					break;
				
				}
			
			case 101: { //получение файлов
				
					String subjectName = data.getString();
					String hash = data.getString();
					for (int i = 0; i < subjects.size(); i++) {
						if (subjects.get(i).getName().equals(subjectName)) {
							
							DataFile[] files = subjects.get(i).getDataFiles(hash);
							
							DataPacket packet = DataPacket.responseFilesPacket(files);
							message.getClient().sendPacket(packet);
							
							break;
						}
					}
					
					break;
				
				}
				
			}
			
		}
		
		server.update();
		
	}
	
	public void save() {
		
		try {
			
			File subjectsFile = new File(GlobalConstants.RESOURCES_FOLDER + GlobalConstants.SUBJECTS_FILE_NAME);
			
			if (!subjectsFile.exists())
				subjectsFile.createNewFile();
			
			DataOutputStream out = new DataOutputStream(new FileOutputStream(subjectsFile));
			
			out.writeInt(subjects.size());
			for (int i = 0; i < subjects.size(); i++) {
				
				byte[] nameBytes = subjects.get(i).getName().getBytes();
				out.writeInt(nameBytes.length);
				
				out.write(nameBytes);
				
			}
			
			out.close();
			
			for (int i = 0; i < subjects.size(); i++) {
				
				File subjectFolder = new File(GlobalConstants.RESOURCES_FOLDER + subjects.get(i).getName() + "/");
				if (!subjectFolder.exists())
					subjectFolder.mkdirs();
				
				File subjectFile = new File(GlobalConstants.RESOURCES_FOLDER + subjects.get(i).getName() + "/" + subjects.get(i).getName());
				if (!subjectFile.exists())
					subjectFile.createNewFile();
				
				byte[] subjectBytes = subjects.get(i).getBytes();
				out = new DataOutputStream(new FileOutputStream(subjectFile));
				
				out.writeInt(subjectBytes.length);
				out.write(subjectBytes);
				
				out.flush();
				out.close();
				
			}
		
		} catch (IOException e) {
			Logger.logError(e);
			System.exit(-1);
		}
		
	}
	
	public void load() {
		
		try {

			File subjectsFile = new File(GlobalConstants.RESOURCES_FOLDER + GlobalConstants.SUBJECTS_FILE_NAME);
			
			if (!subjectsFile.exists())
				subjectsFile.createNewFile();
			
			DataInputStream in = new DataInputStream(new FileInputStream(subjectsFile));
			
			int subjectsLength = in.readInt();
			String[] subjectNames = new String[subjectsLength];
			
			Logger.log(subjectsLength);
			
			for (int i = 0; i < subjectsLength; i++) {
				
				byte[] subjectBytes = new byte[in.readInt()];
				in.read(subjectBytes);
				
				subjectNames[i] = new String(subjectBytes);
				
			}
			
			in.close();
			
			for (int i = 0; i < subjectNames.length; i++) {
				
				File subjectFolder = new File(GlobalConstants.RESOURCES_FOLDER + subjectNames[i] + "/");
				if (!subjectFolder.exists())
					subjectFolder.mkdirs();
				
				File subjectFile = new File(GlobalConstants.RESOURCES_FOLDER + subjectNames[i] + "/" + subjectNames[i]);
				
				in = new DataInputStream(new FileInputStream(subjectFile));
				
				int subjectLength = in.readInt();
				byte[] subjectBytes = new byte[subjectLength];
				
				in.read(subjectBytes);
				
				Subject subject = Subject.fromBytes(subjectBytes);
				subjects.add(subject);
				
				in.close();
				
			}
		
		} catch (IOException e) {
			Logger.logError(e);
			System.exit(-1);
		}
		
	}
	
	public static void main(String[] args) {
		new ServerMain();
	}
	
}
