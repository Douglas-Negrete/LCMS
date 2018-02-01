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
		File file = new File("Web/Website.txt");
		String website = "";

		try {

			reader = new FileReader(file);
			inFile = new Scanner(reader);

			while(inFile.hasNext()) {

				String temp = inFile.nextLine();
				website += temp;

			}

			inFile.close();
			reader.close();

		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return website;

	}//end readTheFile

}//end webHandler

