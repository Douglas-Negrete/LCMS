package List;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Lawn {

<<<<<<< HEAD
	private String address, lawnName, genLocation, notes;
=======
	private String client, address, lawnName, genLocation;
>>>>>>> Douglas
	private Date nextMow, lastMow;
	private int interval;
	private double price;
	private Calendar cal = Calendar.getInstance();
	
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
		
	}//end constructor

	public String getClient() {
		
		return client;
		
	}//end getclient

	public void setClient(String client) {
		
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
	
	public void setNextMow(Date nextMow) {
		
		this.nextMow = nextMow;
		
	}//end setnextmow
	
	public Date getLastMow() {
		
		return lastMow;
		
	}//end getlastmow
	
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
	
	public void skipLawn() {
		
		
		
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
	 String s = this.lawnName + ", " + this.address + " (" + this.genLocation + 
			  ") \nPrice: $" + df.format(this.price) +  "\nLast Mowed: " 
			 + sf.format(this.lastMow) + " Next Mow: " + sf.format(this.nextMow);
	  if (!notes.isEmpty())
		  s += "\nNotes: " + this.notes;
	  s += "\n";
	 return s;
	}
	
}//end Lawn
