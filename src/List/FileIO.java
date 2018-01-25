package List;

import java.io.File;
import java.util.LinkedList;

public class FileIO {

	private File backupFile;
	private LinkedList<Client> clientList;

	public FileIO() {

		clientList = new LinkedList<>();

		clientList.add(new Client("doug", "6375", 20));
		clientList.add(new Client("halina", "6311", 21));
		clientList.add(new Client("coach wilson", "1234", 22));
		clientList.add(new Client("matt", "5678", 23));
		clientList.add(new Client("emily", "9101", 22));
		clientList.add(new Client("sheree", "5343", 25));
		clientList.add(new Client("ryan", "5235", 50));
		clientList.add(new Client("mia", "5656", 23));
		clientList.add(new Client("maddie", "7335", 26));
		clientList.add(new Client("oso", "7653", 40));

	}//end default constructor
	
	public void addClient(Client c) {
		
		clientList.add(c);
		
	}//end addClient

	public void generateBackupFile(Lawn[] list){



	}//end generatebackuplist

	public void readInBackupFile(File file) {



	}//end readinbackupfile

	public String encryptInformation() {

		return "";

	}//end encryptinformation

	public void findBackupFile() {



	}//end findbackupfile

	public void search(String s) {



	}//end search

	public void createClientList() {



	}//end createclientlist

	public void populateLawns() {



	}//end populateLawns

	public String[] getClientName() {

		String[] names;
		if(clientList.size() != 0)
			names = new String[this.clientList.size()];
		else
			return null;

		for(int i = 0; i < this.clientList.size(); i++) {

			names[i] = clientList.get(i).getName();

		}

		return names;

	}//end getclientname
	
	public String[] getLawnName() {

		int lawns = 0;
		
		for(int i = 0; i < clientList.size(); i++) {
			
			lawns += clientList.get(i).lawnListSize();
			
		}
		
		String[] names;
		if(clientList.size() != 0)
			names = new String[lawns];
		else
			return null;

		int t = 0;
		
		for(int i = 0; i < this.clientList.size(); i++) {

			for(int j = 0; j < clientList.get(i).lawnListSize(); j++) {
				
				names[t] = clientList.get(i).getLawnName(j);
				t++;
				
			}

		}

		return names;
		
	}//end getLawnName

}//end class
