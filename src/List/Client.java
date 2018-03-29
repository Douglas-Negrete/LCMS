package List;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;

public class Client {

<<<<<<< HEAD
	private String name, billAddress, phoneNum;
=======
	private String name, billAddress, phoneNumber;
>>>>>>> a66b5abfffd69d7bb83d06bc60777ad8d02d1f99
	private double owed;
	private LinkedList<Lawn> lawnList;
	int lawnNumber;

	public DecimalFormat df = new DecimalFormat("0.00");

<<<<<<< HEAD
	public Client(String name, String billAddress, String phone) {
=======
	public Client(String name, String billAddress, String phoneNumber) {
>>>>>>> a66b5abfffd69d7bb83d06bc60777ad8d02d1f99
		super();
		this.lawnList = new LinkedList<>();
		this.name = name;
		this.billAddress = billAddress;
		this.phoneNum = phone;
		this.owed = 0;
		this.phoneNumber = phoneNumber;
	}//end constructor

	public int lawnListSize() {

		if(this.lawnList.size() != 0)
			return this.lawnList.size();
		else
			return 0;

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
	
	public String getPhoneNum() {

		return phoneNum;

	}//end getname

	public void setPhoneNum(String phone) {

		this.phoneNum = phone;

	}//end setname

	public double getOwed() {

		return owed;

	}//end getowed

	public void setOwed(double owed) {

		this.owed = owed;

	}//end setowed
	
	public String[] getLawnNames() {
		
		ArrayList<String> temp = new ArrayList<>();

		for(int i = 0; i < lawnList.size(); i++)
				temp.add(lawnList.get(i).getAddress());

		//return s = temp.toArray(new String[temp.size()]);
		return temp.toArray(new String[temp.size()]);
		
	}

	public String getSingleLawnName(int i) {

		return this.getName() + ", " + this.lawnList.get(i).getAddress() + ", " + this.lawnList.get(i).getLawnName();

	}//end getLawnName
	
	public Lawn getLawnFromAddress(String s) {
		
		for(int i = 0; i < this.lawnList.size(); i++)
			if(this.lawnList.get(i).getAddress().equals(s))
				return this.lawnList.get(i);
		
		return null;
		
	}//end getlawnfromaddress
	
	public Lawn getSingleLawn(int i) {
		
		return this.lawnList.get(i);
		
	}//end getlawn

	public void addLawn(Lawn l) {

		lawnList.add(l);

	}//end addLawn

	public void removeLawn(Lawn l) {

		lawnList.remove(l);

	}//end removeLawn

	public String toString()
	{
<<<<<<< HEAD
		String s = "Name: " + name + " Billing Address: " +  billAddress + " Phone Number: " + phoneNum + " Owed: " + owed + "\n";
=======
		String s = "Name: " + name + " Billing Address: " +  billAddress + " PhoneNumber: " +
				   phoneNumber + " " + owed + "\n";
>>>>>>> a66b5abfffd69d7bb83d06bc60777ad8d02d1f99
		for (int i = 0; i < lawnList.size(); i++)
			s += lawnList.get(i).toString();
		return s;
	}

	public String toFile()
	{
		String s;
<<<<<<< HEAD
		s = name+";"+billAddress+";"+phoneNum+";"+df.format(owed)+"\n";
=======
		s = name+";"+billAddress+";"+phoneNumber+";"+df.format(owed)+"\n";
>>>>>>> a66b5abfffd69d7bb83d06bc60777ad8d02d1f99
		for (int i = 0; i < lawnList.size(); i++)
			s += lawnList.get(i).toFile() + "\n";
		s += "#ENDLAWN";
		return s;
	}

}//end class
