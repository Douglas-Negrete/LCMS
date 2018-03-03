package List;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;

public class FileIO {

	private File backupFile, backupFileLocation = new File("backupFileLocation.txt");
	private int numClients;
	private LinkedList<Client> clientList;
	public LinkedList<String> emailList;
	private String backupEmail = "";
	public LinkedList<Lawn> lawnList;
	public String companyName;

	public FileIO() {

		readBackupFileLocation();

		clientList = new LinkedList<>();
		emailList = new LinkedList<>();
		lawnList = new LinkedList<>();

		//readInBackupFile();//clientList

		//generateBackupFile();

	}//end default constructor
	
	public Lawn getLawn(String s) {
		
		for(int i = 0; i < lawnList.size(); i++) {
			
			if(lawnList.get(i).getAddress().equals(s))
				return lawnList.get(i);
			
		}
		
		return null;
		
	}//end getLawn
	
	public void setBackupEmail(String s) {
		
		this.backupEmail = s;
		
	}//end setbackupemail
	
	public String getBackupEmail() {
		
		return backupEmail;
		
	}//end getbackupemail
	
	public void removeFiles() {
		
		backupFile.delete();
		backupFileLocation.delete();
		
	}//end removeFiles()

	public void addLawn(int i, Lawn l) {

		clientList.get(i).addLawn(l);

	}//end addlawn

	public void addClient(Client c) {

		clientList.add(c);
		this.numClients++;

	}//end addClient
	
	public void removeClient(int i) {
		
		this.clientList.remove(i);
		
	}//end remove client

	public void generateBackupFile()
	{
		FileWriter writer;
		PrintWriter outFile;

		try
		{
			writer = new FileWriter(backupFile);
			outFile = new PrintWriter(writer);

			outFile.println("#FILESTART");
			for (int i = 0; i < clientList.size(); i++)
			{
				outFile.println(clientList.get(i).toFile());
			}
			outFile.println("#ENDCLIENT");
			outFile.println(backupEmail);
			for (int i = 0; i < emailList.size(); i++)
				outFile.println(emailList.get(i));
			outFile.println("#ENDEMAILS");
			outFile.println(companyName);
			outFile.println("#ENDALL");

			outFile.close();
			writer.close();
		} catch (IOException e) { e.printStackTrace(); }

	}//end generatebackuplist

	public void readInBackupFile()//LinkedList<Client> list) 
	{
		FileReader reader;
		Scanner inFile;

		//backupFile = new File("C:\\Users\\ama82\\workspace\\LCMS\\src\\backupOutputTest.txt");

		try
		{
			reader = new FileReader(backupFile);
			inFile = new Scanner(reader);
			int i = 0;
			String delims = ";";

			while(inFile.hasNext())
			{
				String temp = inFile.nextLine();
				while (!temp.equals("#ENDCLIENT"))
				{
					temp = inFile.nextLine();
					if (temp.equals("#ENDCLIENT"))
						break;
					String[] line = temp.split(delims);
					System.out.println(Arrays.toString(line));
					Client tempClient = new Client(line[0], line[1]);
					tempClient.setOwed(Double.parseDouble(line[2]));
					clientList.add(tempClient);
					i++;

					while (!temp.equals("#ENDLAWN"))
					{
						temp = inFile.nextLine();
						if (temp.equals("#ENDLAWN") || temp.equals("#ENDCLIENT"))
							break;
						line = temp.split(delims);
						//System.out.println(Arrays.toString(line));
						Lawn tempLawn = new Lawn(tempClient,line[0],line[1],line[2],
								Integer.parseInt(line[3]), Double.parseDouble(line[4]));
						tempLawn.setLastMow(line[5]);
						tempLawn.setNextMow(line[6]);
						tempLawn.setNotes(inFile.nextLine());
						lawnList.add(tempLawn);
						tempClient.addLawn(tempLawn);

					}
				}
				while (!temp.equals("#ENDEMAILS"))
				{
					temp = inFile.nextLine();
					if (temp.equals("#ENDEMAILS") || temp.equals(""))
						break;
					emailList.add(temp);
					System.out.println(temp);
				}
				companyName = inFile.nextLine();
				inFile.nextLine();
			}
			System.out.println("All info read in for " + companyName);
			System.out.println("Contains " + i + " Clients");
			setNumClients(i);
			
			sortLawns();

			inFile.close();
			reader.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (java.lang.ArrayIndexOutOfBoundsException e) 
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Input Dialog");
			alert.setHeaderText("System Error");
			alert.setContentText("An error has occured reading in the client information.");

			alert.showAndWait();
			e.printStackTrace();
		} catch (ParseException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Input Dialog");
			alert.setHeaderText("System Error");
			alert.setContentText("An error has occured reading in the client information.");

			alert.showAndWait();
			e.printStackTrace();
		}


	}//end readinbackupfile
	
	public void printBackupFileTA(TextArea ta)//LinkedList<Client> list) 
	{
		FileReader reader;
		Scanner inFile;

		try
		{
			reader = new FileReader(backupFile);
			inFile = new Scanner(reader);
			int i = 0;
			String delims = ";";

			while(inFile.hasNext())
			{
				String temp = inFile.nextLine();
				ta.appendText(temp+"\n");
				while (!temp.equals("#ENDCLIENT"))
				{
					temp = inFile.nextLine();
					ta.appendText(temp+"\n");
					if (temp.equals("#ENDCLIENT"))
						break;
					String[] line = temp.split(delims);
					System.out.println(Arrays.toString(line));
					i++;

					while (!temp.equals("#ENDLAWN"))
					{
						temp = inFile.nextLine();
						ta.appendText(temp+"\n");
						if (temp.equals("#ENDLAWN") || temp.equals("#ENDCLIENT"))
							break;
						line = temp.split(delims);
					}
				}
				while (!temp.equals("#ENDEMAILS"))
				{
					temp = inFile.nextLine();
					ta.appendText(temp+"\n");
					if (temp.equals("#ENDEMAILS") || temp.equals(""))
						break;
					//emailList.add(temp);
					System.out.println(temp);
				}
				//companyName = inFile.nextLine();
				ta.appendText(inFile.nextLine()+"\n");
				inFile.nextLine();
			}
			System.out.println("All info read in for " + companyName);
			System.out.println("Contains " + i + " Clients");
			setNumClients(i);

			inFile.close();
			reader.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (java.lang.ArrayIndexOutOfBoundsException e) 
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Input Dialog");
			alert.setHeaderText("System Error");
			alert.setContentText("An error has occured reading in the client information.");

			alert.showAndWait();
			e.printStackTrace();
		}

	}//end readinbackupfile
	
	public void readBackupFileLocation() {
		
		FileReader reader;
		Scanner inFile;

		try
		{
			reader = new FileReader(backupFileLocation);
			inFile = new Scanner(reader);

			while(inFile.hasNext())
			{
				String temp = inFile.nextLine();
				backupFile = new File(temp);
				System.out.println("Does the file exist - " + backupFile.exists());
			}

			inFile.close();
			reader.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void setBackupFileLocation(String s) {
		
		FileWriter writer;
		PrintWriter outFile;

		try
		{
			writer = new FileWriter(backupFileLocation);
			outFile = new PrintWriter(writer);

			outFile.println(s);

			outFile.close();
			writer.close();
		} catch (IOException e) { e.printStackTrace(); }
		
	}

	public void setBackupFile(File f) {

		this.backupFile = f;

	}//end setbackupfile

	public boolean isNew() {

		if(backupFile == null)
			return true;

		return false;

	}//end isnew

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

		for(int i = 0; i < numClients; i++) {
			
			for(int j = 0; j < getClient(i).lawnNumber; j++)
				lawnList.add(getClient(i).getSingleLawn(j));
			
		}
		
		sortLawns();

	}//end populateLawns
	
	public void sortLawns() {
		
		Collections.sort(lawnList, new Comparator<Lawn>() {

			@Override
			public int compare(Lawn c1, Lawn c2) {
				
				if(c1.sf.format(c1.getNextMow()).equals(c1.sf.format(c2.getNextMow()))) {
					
					if(c1.getGenLocation().compareTo(c2.getGenLocation()) == 0)
						return c1.getLawnName().compareTo(c2.getLawnName());
					return c1.getGenLocation().compareTo(c2.getGenLocation());
					
				}
				else
					return c1.getNextMow().compareTo(c2.getNextMow());
				
			}//end compare
			
		});//end sort
		
	}//end sortLawns

	public int getNumClients() {
		
		return numClients;
		
	}

	public void setNumClients(int numClients) {
		
		this.numClients = numClients;
		
	}

	public String printClients() {
		
		String s = "";
		for (int i = 0; i < getNumClients(); i++)
			s += clientList.get(i);
		return s;
		
	}//end printclients

	public Client getClient(int i) {

		return clientList.get(i);

	}//end getClient

	public int getClientIndex(String name) {

		for(int i = 0; i < clientList.size(); i++) {

			if(clientList.get(i).getName().equals(name))
				return i;

		}

		return -1;

	}//end getclientindex

	public String[] getClientNames() {

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

	public String[] getLawnNames() {

		String[] names = new String[lawnList.size()];
		
		for(int i = 0; i < lawnList.size(); i++) {
			
			names[i] = lawnList.get(i).getClient().getName() + ", " + lawnList.get(i).getAddress() + ", " + lawnList.get(i).getLawnName();
			
		}

		return names;

	}//end getLawnName
	
	public String[] getCheckedLawns() {
		
		ArrayList<String> cLawns = new ArrayList<>();

		for(int i = 0; i < this.clientList.size(); i++) {

			for(int j = 0; j < clientList.get(i).lawnListSize(); j++) {

				if(clientList.get(i).getSingleLawn(j).getLastMow().equals(new Date())) {
					
					cLawns.add(clientList.get(i).getSingleLawnName(j));
					
				}

			}

		}

		return cLawns.toArray(new String[cLawns.size()]);
		
	}//end getcheckedLawns
	
	public String getBackupLocation() {
		
		return this.backupFile.getAbsolutePath();
		
	}//end getBackupLocation
	
	public void setEmailData() {
		
		this.backupEmail = emailList.get(0);
		emailList.removeFirst();
		
	}//end setemaildata

}//end class
