package GUI;
import java.util.ArrayList;

import List.Client;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GUI extends Application {

	ArrayList<Client> clients = new ArrayList<>();

	@Override
	public void start(Stage primaryStage) throws Exception {

		clients.add(new Client("Doug", "6375"));
		clients.add(new Client("Halina", "6311"));
		clients.add(new Client("Coach Wilson", "1234"));
		clients.add(new Client("Matt", "5678"));
		clients.add(new Client("Emily", "9101"));
		clients.add(new Client("Sheree", "5343"));
		clients.add(new Client("Ryan", "5235"));
		clients.add(new Client("Mia", "5656"));
		clients.add(new Client("Maddie", "7335"));
		clients.add(new Client("Oso", "7653"));
		clients.add(new Client("Caleb", "7831"));

		primaryStage.setTitle("Lawn Care Made Simple");

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(15, 15, 15, 15));

		HBox searchBox = new HBox();
		searchBox.setSpacing(10);
		searchBox.setPadding(new Insets(5, 20, 5, 20));

		HBox add = new HBox();
		add.setSpacing(10);
		add.setPadding(new Insets(20, 20, 20, 0));

		ListView<String> list;
		ObservableList<String> clientList;
		TextField searchTextField;
		Label searchLabel;
		Button addClient, addLawn;

		addClient = new Button("Client");
		addLawn = new Button("Lawn");

		add.getChildren().addAll(addClient, addLawn);

		clientList = FXCollections.<String>observableArrayList(getItem(clients, 0));
		list = new ListView<>(clientList);

		searchLabel = new Label("Search");
		searchTextField = new TextField();
		searchTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {

				if(event.getCode().equals(KeyCode.ENTER)) {

					search(searchTextField, list);

				}

			}//end handle

		});//end setonkeypressed

		searchBox.getChildren().addAll(searchLabel, searchTextField);

		//		grid.add(searchLabel, 0, 0);
		//		grid.add(searchTextField, 1, 0);
		grid.add(add, 0, 1);
		grid.add(searchBox, 1, 0);
		grid.add(list, 1, 2);

		primaryStage.setScene(new Scene(grid, 1100, 600));

		primaryStage.show();

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			public void handle(WindowEvent we) {

				System.out.println("window is closing");

			}//end handle

		});//end setoncloserequest

	}//end start

	public void search(TextField search, ListView<String> list) {

		if(list.getItems().contains(search.getText())) {

			System.out.println("Is contained in the list.");

		}

	}//end search

	public String[] getItem(ArrayList<Client> list, int index) {

		String[] items = new String[list.size()];  

		for(int i = 0; i < list.size(); i++) {

			if(index == 0)
				items[i] = list.get(i).getName();
			else if(index == 1)
				items[i] = list.get(i).getBillAddress();
			else if(index == 2)
				items[i] = "" + list.get(i).getOwed();

		}

		return items;

	}//end getItem

}//end class GUI
