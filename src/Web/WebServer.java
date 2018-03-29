
//public class WebServer {
package Web;

//package application;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

import GUI.GUI;

public class WebServer {

	public static String startServer() {

		Server server = new Server(1025);

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
			//ipAddress = inet.getHostName();
			//System.out.println("This is the new address "+ipAddress);
			ipAddress = ips[0].toString();
			//ipAddress = inet.getHostAddress();
			//System.out.println("This is the new address "+ipAddress);
		} catch (UnknownHostException e) {

			System.out.println("ERROR1");

		}

		//			ServerConnector http = new ServerConnector(server);
		//			http.setHost(ipAddress);
		//			http.setHost("localhost");
		//			http.setIdleTimeout(30000);
		//			http.setPort(80);
		//			server.addConnector(http);
		server.setHandler(new WebHandler());

		try {
			//server.stop();
			server.start();
			//server.join();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("ERROR2");
		}

		return ipAddress;

	}//end startServer

	//		public static void main(String[] args) {startServer();}

}//end class webServer

//}