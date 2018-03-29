package List;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Lawn {

	private Client client;
	private String address, lawnName, genLocation, notes;
	private Date nextMow, lastMow, tempN, tempL;
	private int interval, numMows;
	private double price;
	private Calendar cal = Calendar.getInstance();
	private boolean active = true;

	public DecimalFormat df = new DecimalFormat("0.00");
	public SimpleDateFormat sf = new SimpleDateFormat("MM-dd-yyyy");

	public Lawn(Client client, String address, String lawnName, String genLocation, int interval, double price) {

		super();
		setAddress(address);
		setLawnName(lawnName);
		setGenLocation(genLocation);
		setInterval(interval);
		setClient(client);
		setPrice(price);
		//		setLastMow(this.cal.getTime());
		//		setNextMow(this.cal.getTime());
		setNotes("");

	}//end constructor

	public void setActive(boolean b) {

		this.active = b;

	}//end setActive

	public void setNumMows(int num) {

		this.numMows = num;

	}

	public int setNumMows() {

		return this.numMows;

	}

	public Client getClient() {

		return client;

	}//end getclient

	public void setClient(Client client) {

		this.client = client;

	}//end setclient

	public String getAddress() {

		return address;

	}//end getAddress

	public void setAddress(String address) {

		this.address = address;

	}//end setaddress

	public String getLawnName() {

		return lawnName;

	}//end getlawnname

	public void setLawnName(String lawnName) {

		this.lawnName = lawnName;

	}//end setlawnname

	public String getGenLocation() {

		return genLocation;

	}//end getgenlocation

	public void setGenLocation(String genLocation) {

		this.genLocation = genLocation;

	}//end setgenlocation

	public String getStringNextMow() {

		return sf.format(nextMow);

	}//end getnextmow

	public Date getNextMow() {

		return nextMow;

	}//end getnextmow

	public String getStringLastMow() {

		return sf.format(lastMow);

	}//end getlastmow

	public Date getLastMow() {

		return lastMow;

	}//end getlastmow

	public void setNextMow(Date nextMow) {

		this.nextMow = nextMow;

	}//end setnextmow

	public void setNextMow(String nextMow) throws ParseException {

		this.nextMow = sf.parse(nextMow);

	}//end setnextmow

	public void setLastMow(Date lastMow) {

		this.lastMow = lastMow;

	}//end setlastmow

	public void setLastMow(String lastMow) throws ParseException {

		this.lastMow = sf.parse(lastMow);

	}//end setlastmow

	public int getInterval() {

		return interval;

	}//end getinterval

	public void setInterval(int interval) {

		this.interval = interval;

	}//end setinterval

	public double getPrice() {

		return price;

	}//end getprice

	public void setPrice(double price) {

		this.price = price;

	}//end setprice

	public void setNotes(String str) {

		this.notes = str;
	}

	public String getNotes() {

		return this.notes;

	}//end getprice

	public void skipLawn() 
	{
		this.tempL = this.lastMow;
		this.tempN = this.nextMow;

		this.lastMow = Calendar.getInstance().getTime();
		cal.setTime(lastMow);
		cal.add(Calendar.DATE, interval);
		this.nextMow = cal.getTime();

	}//end checklawnoff

	public void unSkipLawn() {

		if(tempL != null && tempN != null) {

			this.lastMow = this.tempL;
			this.nextMow = this.tempN;

		}
		else {
			
			cal.setTime(lastMow);
			cal.add(Calendar.DATE, -interval);
			this.lastMow = cal.getTime();
			
			cal.setTime(nextMow);
			cal.add(Calendar.DATE, -interval);
			this.nextMow = cal.getTime();
			
		}

	}//end unCheckLawnOff

	public void checkLawnOff() 
	{
		this.tempL = this.lastMow;
		this.tempN = this.nextMow;

		this.lastMow = Calendar.getInstance().getTime();
		cal.setTime(lastMow);
		cal.add(Calendar.DATE, interval);
		this.nextMow = cal.getTime();

		this.client.setOwed(this.client.getOwed() + this.price);

	}//end checklawnoff

	public void unCheckLawnOff() {

		if(tempL != null && tempN != null) {

			this.lastMow = this.tempL;
			this.nextMow = this.tempN;

		}
		else {
			
			cal.setTime(lastMow);
			cal.add(Calendar.DATE, -interval);
			this.lastMow = cal.getTime();
			
			cal.setTime(nextMow);
			cal.add(Calendar.DATE, -interval);
			this.nextMow = cal.getTime();
			
		}
			
		this.client.setOwed(this.client.getOwed() - this.price);

	}//end unCheckLawnOff

	public String toString()
	{
		String s = this.getLawnName() + ", " + getAddress() + " (" + getGenLocation() + 
				") \nPrice: $" + df.format(getPrice()) +  "\nLast Mowed: " 
				+ sf.format(getLastMow()) + " Next Mow: " + sf.format(getNextMow());
		if (!notes.isEmpty())
			s += "\nNotes: " + getNotes();
		s += "\n";
		return s;
	}

	public String toFile()
	{
		String s;
		s = getAddress() + ";" + getLawnName() +";"+ getGenLocation() +";"+getInterval()+";";
		s += df.format(getPrice()) +";"+ sf.format(getLastMow()) +";"+ sf.format(getNextMow());
		s += "\n" + getNotes();
		return s;
	}

}//end Lawn