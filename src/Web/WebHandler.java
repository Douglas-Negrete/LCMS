package Web;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class WebHandler extends AbstractHandler {

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		response.getWriter().println(readTheFile());

	}//end handle


	public String readTheFile() {
		
		FileReader reader;
		Scanner inFile;
		File file = new File("lawns.txt");
		String website = "";
		String temp = "";

		website += "<!DOCTYPE html>";
		website += "<html>";
		//website += "<body>";
		
		try
		{
		//read in base css
		File cssFile = new File("cssTest.txt");
		FileReader cssReader = new FileReader(cssFile);
		inFile = new Scanner(cssReader);
				
		while(inFile.hasNext())
		{
			temp = inFile.nextLine();
			//System.out.println(temp);
			website += temp;
		}
		
		//website += "<style> body{background-image: url(\"forestbridge.jpg\");} <\\style>";
		//website += "<meta charset=\"UTF-8\">";
		//website += "<h1>LCMS</h1>";
		//website += "<h2>Lawn Care Made Simple</h2>";

		//website += "<b2> Lawn List: <br/> </b2>";

		website += "<form action=\"/FileWrite.jsp\" method=\"POST\">";
		
		
//**************************************************************************************************************
		
		//populate with lawns

		reader = new FileReader(file);
		inFile = new Scanner(reader);
		int i = 1;
		
		while(inFile.hasNext()){
				
			temp = inFile.nextLine();
			website += "<input type=\"checkbox\" name=\"lawn\" value=\"mowed\">" + temp + "<br>";
			website += "<input type=\"textarea\" rows=\"1\" cols=\"15\" id = c " + i + " name=\"comments\" placeholder=\"Enter Comments Here\"></textarea><br><br>";
			temp = inFile.nextLine();
			temp = inFile.nextLine();
			//website += temp + "<br>";
			i++;

		}
		
		inFile.close();
		reader.close();	
			
		}
		catch (IOException e){
			e.printStackTrace();
		}
		
		//website += "<button type=\"button\" value=\"Submit Lawns\" onclick=\"submitLawns()\">Submit Lawns</button>";
		
		//website += "<button type=\"button\" onclick=\"document.getElementById('demo').innerHTML = Date()\">Click me to display Date and Time.</button>";
		website += "<input type=\"submit\" value=\"Submit Lawns\"></form>";
		
//**************************************************************************************************************

		//end tags
		//website += "<script>";

		//website += "function submitLawns()"; 
		//website += "{";
			//website += ""
		   	//website += "var x = document.getElementById(\"c1\").value;";
		   	//website += "document.getElementById(\"test\").innerHTML = x;";
		//website += "}";
		    
		//website += "</script>";
		
		website += "</body>";
		website += "</html>";

		//try {

		//	reader = new FileReader(file);
		//	inFile = new Scanner(reader);

		//	while(inFile.hasNext()) {

		//		String temp = inFile.nextLine();
		//		website += temp;

		//	}

		//	inFile.close();
		//	reader.close();

		//}
		//catch (IOException e) {
		//	e.printStackTrace();
		//}

		return website;

	}//end readTheFile

}//end webHandler

