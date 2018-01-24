package List;

import java.util.LinkedList;

public class Client {

	private String name, billAddress;
	private double owed;
	private LinkedList<Lawn> lawnList;
	int lawnNumber;
	
	public Client(String name, String billAddress) {
		super();
		this.name = name;
		this.billAddress = billAddress;
	}

	public String getName() {
		
		return this.name;
		
	}//end getname
	
	public void setName(String name) {
		
		this.name = name;
		
	}//end setname
	
	public String getBillAddress() {
		
		return this.billAddress;
		
	}//end getbilladdress
	
	public void setBillAddress(String billAddress) {
		
		this.billAddress = billAddress;
		
	}//end setbilladdress
	
	public double getOwed() {
		
		return this.owed;
		
	}//end getowed
	
	public void setOwed(double owed) {
		
		this.owed = owed;
		
	}//end setowed
	
	public void addLawn(Lawn l) {
		
		lawnList.add(l);
		lawnNumber++;
		
	}//end addLawn
	
	public void removeLawn(Lawn l) {
		
		lawnList.remove(l);
		lawnNumber--;
		
	}//end removeLawn

	
}//end class
