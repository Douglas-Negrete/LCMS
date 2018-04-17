package Web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class WebMain {

	public static Server server;
	
	public static String startServer() throws Exception {

		//Find ipAddress
		String ipAddress = "";
		try {
			InetAddress inet = InetAddress.getLocalHost();
			InetAddress[] ips = InetAddress.getAllByName(inet.getCanonicalHostName());
			if (ips  != null ) {
				for (int i = 0; i < ips.length; i++) {
					ipAddress = ips[i].toString();
					System.out.println(ips[i]);
				}
			}
			
		//ipAddress = ips[1].toString();
			
		} catch (UnknownHostException e) {System.out.println("ERROR1");}
		
		//return ipAddress;
		
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
		
		FileReader reader;
		Scanner inFile;
		File readFile = new File("lawns.txt");
		File writeFile = new File("src/Web/webapp/lawns.html");
		String website = "";
		String temp = "";
		
		try
		{
		
		if( writeFile.exists() && !writeFile.isDirectory() )
		{
			writeFile.delete();
		}
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(writeFile), true);
	
		pw.println("<html>");
		
		pw.println("<meta charset=\"UTF-8\">\r\n" + 
				"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
		
		pw.println("<body>");
		
		//read in base css
		//File cssFile = new File("cssTest.txt");
		//FileReader cssReader = new FileReader(cssFile);
		//inFile = new Scanner(cssReader);
						
		//while(inFile.hasNext())
		//{
			//temp = inFile.nextLine();
			//System.out.println(temp);
			//pw.println(temp);
		//}
		
		pw.println("<div class=\"w3-display-top w3-center\">");
	    pw.println("<h1 class=\"w3-jumbo w3-animate-top\">Lawn Care Made Simple</h1>");
	    pw.println("<hr class=\"w3-border-grey\" style=\"margin:auto;width:100%\">");
	    pw.println("<p class=\"w3-large w3-center\">Lawn List</p>");
	    pw.println("</div>");
		
		
		//populate with lawns
		reader = new FileReader(readFile);
		inFile = new Scanner(reader);
		int i = 1;
		
		pw.println("<form action=\"FileWrite.jsp\" method=\"POST\">");
		
		while(inFile.hasNext()){
						
			temp = inFile.nextLine();
			//pw.println("<input type=\"checkbox\" name=\"" + i + "_lawn\" value=\"mowed\">" + temp + "<br>");
			//pw.println("<input type=\"textarea\" rows=\"1\" cols=\"15\" name=\"" + (i+1) + "_comments\" placeholder=\"Enter Comments Here\"></textarea><br><br>");
			
			pw.println("<input type=\"checkbox\" name=\"" + i + "\" value=\"mowed\">" + temp + "<br>");
			pw.println("<input type=\"hidden\" name=\"" + i + "\" value=\"0\">");
			
			pw.println("<input type=\"textarea\" rows=\"1\" cols=\"15\" name=\"" + (i+1) + "\" placeholder=\"Enter Comments Here\"></textarea><br><br>");
			
			temp = inFile.nextLine();
			temp = inFile.nextLine();
			//website += temp + "<br>";
			i++;
			i++;

		}
				
		inFile.close();
		reader.close();	
		
		pw.println("<input type=\"submit\" value=\"Submit Lawns\"></form>");
		
		pw.println("</body>");
		pw.println("</html>");
		
		}
		catch(FileNotFoundException e) {e.printStackTrace();}
		catch(IOException e) {e.printStackTrace();}
		
		//When change made to lawns.txt, triggers separate class with this code to update the html
	}
	
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
