package Web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.util.Scanner;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class WebMain {

	public static Server server;

	public static String startServer() throws Exception {

		//Determine IP Address of machine to email
		String ipAddress = "";
//		
//		URL whatismyip = new URL("http://checkip.amazonaws.com");
//		BufferedReader in = new BufferedReader(new InputStreamReader(
//		                whatismyip.openStream()));
//
//		ipAddress = in.readLine();
		
		try {
			
			InetAddress inet = InetAddress.getLocalHost();
			InetAddress[] ips = InetAddress.getAllByName(inet.getCanonicalHostName());
			
			if(ips!=null) {
				
				for(int i = 0; i < ips.length; i++) {
					
					ipAddress = ips[i].toString();
					System.out.println(ips[i]);
					
				}
				
			}
			
			if(ips.length>1)
				ipAddress = ips[1].toString();
			
		}
		catch(UnknownHostException e) {
			
			
		}
		
		System.out.println(ipAddress + ":8080");
		
		createHTML();
		configureServer();
		
		return ipAddress;

	}

	public static void configureServer()
	{
		try
		{
			// 1. Creating the server on port 8080
			server = new Server(8080);

			// 2. Creating the WebAppContext for the created content
			WebAppContext ctx = new WebAppContext();
			ctx.setResourceBase("src/Web/webapp");
			ctx.setContextPath("/");

			// 3. Including the JSTL jars for the webapp.
			ctx.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",".*/[^/]*jstl.*\\.jar$");

			// 4. Enabling the Annotation based configuration
			org.eclipse.jetty.webapp.Configuration.ClassList classlist = org.eclipse.jetty.webapp.Configuration.ClassList.setServerDefault(server);
			classlist.addAfter("org.eclipse.jetty.webapp.FragmentConfiguration", "org.eclipse.jetty.plus.webapp.EnvConfiguration", "org.eclipse.jetty.plus.webapp.PlusConfiguration");
			classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration", "org.eclipse.jetty.annotations.AnnotationConfiguration");

			// 5. Setting the handler and starting the Server
			server.setHandler(ctx);
			//server.setHandler(new WebHandler());
			server.start();
			//server.join();
		}
		catch(Exception e) {e.printStackTrace();}
	}

	public static void createHTML()
	{

		//Variable Declarations
		FileReader reader;
		Scanner inFile;
		File readFile = new File("lawns.txt");
		File writeFile = new File("src/Web/webapp/lawns.html");
		//File writeFile = new File("lawns.html");
		//String website = "";
		String temp = "";
		String mowVal = "";
		String CompanyName = "";
		
		try
		{
		
		if( writeFile.exists() && !writeFile.isDirectory() )
		{
			writeFile.delete();
		}
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(writeFile), true);
	
//---------------Begin writing lawns.html--------------------------------------------------------
		
		pw.println("<html>");
		
		pw.println("<link rel=\"stylesheet\" href=\"w3.css\">");
		pw.println("<meta charset=\"UTF-8\">\r\n" + 
				"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
		
		pw.println("<body style=\"background-color: #aef2a9;\">");
		
		reader = new FileReader(readFile);
		inFile = new Scanner(reader);
		int i = 1;
				
		CompanyName = inFile.nextLine();
		
		pw.println("<div class=\"bgimg w3-display-container w3-animate-opacity\">");
		pw.println("<div class=\"w3-display-top w3-center\">");
		
		//if-else: prints different header depending on if a company name is available
		if(CompanyName.equals(""))
		{
			pw.println("<h1 class=\"w3-jumbo w3-animate-top\">Lawn Care Made Simple</h1>");
		}
		else
		{
			pw.println("<h1 class=\"w3-jumbo w3-animate-top\">" + CompanyName + "</h1>");
		}
		
		pw.println("<hr class=\"w3-border-grey\" style=\"margin:auto;width:100%\">");
	    pw.println("<p class=\"w3-large w3-center\">Lawn List</p>");
		
		pw.println("<form action=\"FileWrite.jsp\" method=\"POST\">");
		
		//This loop: Writing lawns from lawns.txt w/ checkboxes and text fields
		while(inFile.hasNext())
		{
						
			temp = inFile.nextLine();
			mowVal = inFile.nextLine();
			
			//if-else: pre-checks boxes if lawns are already listed as mowed
			if(mowVal.equals("unmowed"))
			{
				pw.println("<input type=\"checkbox\" name=\"" + i + "\" value=\"mowed\">" + "<font size=\"4\">" + temp + "</font>" + "<br>");
			}
			else
			{
				pw.println("<input type=\"checkbox\" name=\"" + i + "\" value=\"mowed\" checked>" + "<font size=\"4\">" + temp + "</font>" + "<br>");
			}
			
			//ensures NULL is not returned by a checkbox: if NULL is returned, this returns 0 instead
			pw.println("<input type=\"hidden\" name=\"" + i + "\" value=\"0\">");

			temp = inFile.nextLine();
			pw.println("&nbsp" + temp + "<br>");
			
			pw.println("<input type=\"textarea\" rows=\"1\" cols=\"15\" autocomplete = \"off\" name=\"" + (i+1) + "\" placeholder=\"Enter Comments Here\"></textarea><br><br>");
			
			i++;
			i++;
		}
				
		inFile.close();
		reader.close();	
		
		//Submit Lawns button
		pw.println("<input type=\"submit\" value=\"Submit Lawns\"></form>");
		
		pw.println("</div>");
		pw.println("</div>");
		
		pw.println("</body>");
		pw.println("</html>");
		
		pw.close();
		
//---------------End writing lawns.html--------------------------------------------------------
		
		}
		catch(FileNotFoundException e) {e.printStackTrace();}
		catch(IOException e) {e.printStackTrace();}
	}

	//serverRestart to be called when changes made to lawns.txt by Desktop Application
	public static void serverRestart() {

		try {

			server.stop();
			createHTML();
			server.start();

		}
		catch(Exception e) {



		}

	}//end serverRestart

}
