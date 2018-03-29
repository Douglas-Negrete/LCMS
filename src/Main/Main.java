package Main;

import GUI.GUI;
import Web.WebServer;
import javafx.application.Application;

public class Main {

	static String ip;

	public static void main(String[] args) throws InterruptedException {

		System.setProperty("glass.accessible.force", "false");

		GUI gui = new GUI();

		new Thread() {//creates anonymous thread object

			@Override
			public void run() {

				//Application.launch(GUI.class);
				Application.launch(gui.getClass());

			}//end run method

		}.start();//end thread object

		if(gui.io.readServerFromFile()) {
			
			ip = WebServer.startServer();

		}

	}//end main

}//end class Main
