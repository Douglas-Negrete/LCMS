package Main;

import GUI.GUI;
import javafx.application.Application;

public class Main {

	public static void main(String[] args) {
		
		System.setProperty("glass.accessible.force", "false");

		Application.launch(GUI.class);

	}//end main

}//end class Main
