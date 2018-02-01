package List;

import java.util.LinkedList;

public class Client {

	private String name, billAddress;
	private double owed;
	private LinkedList<Lawn> lawnList;
	int lawnNumber;
<<<<<<< HEAD

	public Client(String name, String billAddress, double owed) {
=======
	
	public Client(String name, String billAddress) {
>>>>>>> Caleb
		super();
		this.lawnList = new LinkedList<>();
		this.name = name;
		this.billAddress = billAddress;
<<<<<<< HEAD
		this.owed = owed;
	}//end constructor

	public int lawnListSize() {

		if(this.lawnList.size() != 0)
			return this.lawnList.size();
		else
			return 0;

	}//end lawnListSize

	public String getName() {

		return name;

=======
	}

	public String getName() {
		
		return this.name;
		
>>>>>>> Caleb
	}//end getname

	public void setName(String name) {

		this.name = name;

	}//end setname

	public String getBillAddress() {
<<<<<<< HEAD

		return billAddress;

=======
		
		return this.billAddress;
		
>>>>>>> Caleb
	}//end getbilladdress

	public void setBillAddress(String billAddress) {

		this.billAddress = billAddress;

	}//end setbilladdress

	public double getOwed() {
<<<<<<< HEAD

		return owed;

=======
		
		return this.owed;
		
>>>>>>> Caleb
	}//end getowed

	public void setOwed(double owed) {

		this.owed = owed;

	}//end setowed

	public String getLawnName(int i) {

		return this.lawnList.get(i).getLawnName();

	}//end getLawnName

	public void addLawn(Lawn l) {

		lawnList.add(l);
<<<<<<< HEAD

=======
		lawnNumber++;
		
>>>>>>> Caleb
	}//end addLawn

	public void removeLawn(Lawn l) {

		lawnList.remove(l);
<<<<<<< HEAD

	}//end removeLawn

=======
		lawnNumber--;
		
	}//end removeLawn

	
>>>>>>> Caleb
}//end class
