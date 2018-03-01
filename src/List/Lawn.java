package List;

import java.text.DecimalFormat;
<<<<<<< HEAD
=======
import java.text.ParseException;
>>>>>>> Douglas
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Lawn {

<<<<<<< HEAD
<<<<<<< HEAD
	private String address, lawnName, genLocation, notes;
=======
	private String client, address, lawnName, genLocation;
>>>>>>> Douglas
=======
	private Client client;
	private String address, lawnName, genLocation, notes;
>>>>>>> Douglas
	private Date nextMow, lastMow;
	private int interval;
	private double price;
	private Calendar cal = Calendar.getInstance();
<<<<<<< HEAD
	
	public DecimalFormat df = new DecimalFormat("0.00");
	public SimpleDateFormat sf = new SimpleDateFormat("MM-dd-yyyy");
	
	
	public Lawn(String client, String address, String lawnName, String genLocation, int interval, double price) {
		
		super();
		this.client = client;
		this.address = address;
		this.lawnName = lawnName;
		this.genLocation = genLocation;
		this.interval = interval;
		this.price = price;
		this.nextMow = this.cal.getTime();
		this.notes = "";
		
=======

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
		setLastMow(this.cal.getTime());
		setNextMow(this.cal.getTime());
		setNotes("");

>>>>>>> Douglas
	}//end constructor

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

	public Date getNextMow() {

		return nextMow;

	}//end getnextmow

	public void setNextMow(String nextMow) throws ParseException {

		this.lastMow = sf.parse(nextMow);

	}//end setnextmow

	public Date getLastMow() {

		return lastMow;

	}//end getlastmow

	public void setLastMow(String lastMow) throws ParseException {

		this.lastMow = sf.parse(lastMow);

	}//end setlastmow
	
	public void setNextMow(Date nextMow)
	{
		
	  this.nextMow = nextMow;
	  
	}
	
	public void setLastMow(Date lastMow) {
		
		this.lastMow = lastMow;
		
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

	public void skipLawn() {

		cal.add(Calendar.DATE, interval);
		nextMow = cal.getTime();

	}//end skiplawn
<<<<<<< HEAD
	
	public void checkLawnOff() 
	{
	 this.lastMow = Calendar.getInstance().getTime(); 
				
	 lastMow = cal.getTime();
	 cal.add(Calendar.DATE, interval);
	 nextMow = cal.getTime();
	}//end checklawnoff
	
	public String toString()
	{
	 String s = this.lawnName + ", " + this.address + " (" + this.genLocation + 
			  ") \nPrice: $" + df.format(this.price) +  "\nLast Mowed: " 
			 + sf.format(this.lastMow) + " Next Mow: " + sf.format(this.nextMow);
	  if (!notes.isEmpty())
		  s += "\nNotes: " + this.notes;
	  s += "\n";
	 return s;
	}
	
=======

	public void checkLawnOff() 
	{
		this.lastMow = Calendar.getInstance().getTime(); 

		lastMow = cal.getTime();
		cal.add(Calendar.DATE, interval);
		nextMow = cal.getTime();
	}//end checklawnoff

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

>>>>>>> Douglas
}//end Lawn
