package List;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Lawn {

	private String address, lawnName, genLocation, notes;
	private Date nextMow, lastMow;
	private int interval;
	private double price;
	private Client client;
	private Calendar cal = Calendar.getInstance();
	
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
		setNextMow(this.cal.getTime());
		setNotes("");
		
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
		
		return this.lawnName;
		
	}//end getlawnname
	
	public void setLawnName(String lawnName) {
		
		this.lawnName = lawnName;
		
	}//end setlawnname
	
	public String getGenLocation() {
		
		return this.genLocation;
		
	}//end getgenlocation
	
	public void setGenLocation(String genLocation) {
		
		this.genLocation = genLocation;
		
	}//end setgenlocation
	
	public Date getNextMow() {
		
		return this.nextMow;
		
	}//end getnextmow
	
	public void setNextMow(Date nextMow) {
		
		this.nextMow = nextMow;
		
	}//end setnextmow
	
	public Date getLastMow() {
		
		return this.lastMow;
		
	}//end getlastmow
	
	public void setLastMow(Date lastMow) {
		
		this.lastMow = lastMow;
		
	}//end setlastmow
	
	public int getInterval() {
		
		return this.interval;
		
	}//end getinterval
	
	public void setInterval(int interval) {
		
		this.interval = interval;
		
	}//end setinterval
	
	public double getPrice() {
		
		return this.price;
		
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
	
	public void checkLawnOff() 
	{
	 this.lastMow = Calendar.getInstance().getTime(); 
				
	 lastMow = cal.getTime();
	 cal.add(Calendar.DATE, interval);
	 nextMow = cal.getTime();
	}//end checklawnoff
	
	public String toString()
	{
	 String s = getLawnName() + ", " + getAddress() + " (" + getGenLocation() + 
			 ") \nPrice: $" + df.format(getPrice()) +  "\nLast Mowed: " 
			 + sf.format(getLastMow()) + " Next Mow: " + sf.format(getNextMow());
	  if (!notes.isEmpty())
		  s += "\nNotes: " + getNotes();
	  s += "\n";
	 return s;
	}
	
}//end Lawn
