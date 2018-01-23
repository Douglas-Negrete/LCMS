package List;

import java.util.Date;

public class Lawn {

	private String address, lawnName, genLocation;
	private Date nextMow, lastMow;
	private int clientID, interval;
	private double price;
	
	public Lawn(String address, String lawnName, String genLocation, int clientID, int interval, double price) {
		
		super();
		this.address = address;
		this.lawnName = lawnName;
		this.genLocation = genLocation;
		this.clientID = clientID;
		this.interval = interval;
		this.price = price;
		
	}//end constructor

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
	
	public int getClientID() {
		
		return clientID;
		
	}//end getclientid
	
	public void setClientID(int clientID) {
		
		this.clientID = clientID;
		
	}//end setclientid
	
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
	
	public void checkLawnOff() {
		
		
		
	}//end checklawnoff
	
	public String toString() {
		
		return "";
		
	}//end toString
	
}//end Lawn
