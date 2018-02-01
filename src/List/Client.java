package List;

import java.util.LinkedList;

public class Client {

	private String name, billAddress;
	private double owed;
	private LinkedList<Lawn> lawnList;
	int lawnNumber;

	public Client(String name, String billAddress) {

		super();
		this.lawnList = new LinkedList<>();
		this.name = name;
		this.billAddress = billAddress;
		this.owed = 0;

	}//end constructor

	public int lawnListSize() {

		if(this.lawnList.size() != 0)
			return this.lawnList.size();
		else
			return 0;

	}//end lawnListSize

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

	public String getLawnName(int i) {

		return this.lawnList.get(i).getLawnName();

	}//end getLawnName

	public void addLawn(Lawn l) {

		lawnList.add(l);
		lawnNumber++;

	}//end addLawn

	public void removeLawn(Lawn l) 
	{

		lawnList.remove(l);
		lawnNumber--;
		
	}//end removeLawn

}//end class
