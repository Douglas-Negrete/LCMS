package Main;

import GUI.GUI;
import Web.WebServer;
import javafx.application.Application;

public class Main {

	static String ip;

	public static void main(String[] args) {

		System.setProperty("glass.accessible.force", "false");

		new Thread() {//creates anonymous thread object

			@Override
			public void run() {

				Application.launch(GUI.class);

			}//end run method

		}.start();//end thread object

		ip = WebServer.startServer();

	}//end main

}//end class Main
