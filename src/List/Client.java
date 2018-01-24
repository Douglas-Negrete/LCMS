package List;

import java.util.LinkedList;

public class Client {

	private String name, billAddress;
	private int clientID;
	private double owed;
	private LinkedList<Lawn> lawnList;
	int lawnNumber;
	
	public Client(String name, String billAddress, double owed) {
		super();
		this.name = name;
		this.billAddress = billAddress;
		this.owed = owed;
	}//end constructor
	
	public int lawnListSize() {
		
		return this.lawnList.size();
		
	}//end lawnListSize

	public String getName() {
		
		return name;
		
	}//end getname
	
	public void setName(String name) {
		
		this.name = name;
		
	}//end setname
	
	public String getBillAddress() {
		
		return billAddress;
		
	}//end getbilladdress
	
	public void setBillAddress(String billAddress) {
		
		this.billAddress = billAddress;
		
	}//end setbilladdress
	
	public double getOwed() {
		
		return owed;
		
	}//end getowed
	
	public void setOwed(double owed) {
		
		this.owed = owed;
		
	}//end setowed
	
	public void addLawn(Lawn l) {
		
		lawnList.add(l);
		
	}//end addLawn
	
	public void removeLawn(Lawn l) {
		
		lawnList.remove(l);
		
	}//end removeLawn

	public int getClientID() {
		
		return clientID;
		
	}//end getClientID

	public void setClientID(int clientID) {
		
		this.clientID = clientID;
		
	}//end setClientID
	
}//end class
