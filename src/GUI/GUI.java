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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GUI extends Application {

	ArrayList<Client> clients = new ArrayList<>();

	@Override
	public void start(Stage primaryStage) throws Exception {

		clients.add(new Client("doug", "6375", 20));
		clients.add(new Client("halina", "6311", 21));
		clients.add(new Client("coach wilson", "1234", 22));
		clients.add(new Client("matt", "5678", 23));
		clients.add(new Client("emily", "9101", 22));
		clients.add(new Client("sheree", "5343", 25));
		clients.add(new Client("ryan", "5235", 50));
		clients.add(new Client("mia", "5656", 23));
		clients.add(new Client("maddie", "7335", 26));
		clients.add(new Client("oso", "7653", 40));

		primaryStage.setTitle("Lawn Care Made Simple");
        Scene scene = new Scene(new VBox(), 1100, 600);

		//GridPane grid = new GridPane();
		//grid.setPadding(new Insets(0, 15, 15, 0));

		MenuBar menuBar = new MenuBar();//The menu for the topPane
		// --- Menu File
		Menu menuFile = new Menu("File");//file submenu for the menu
		// --- Menu Edit
		Menu menuEdit = new Menu("Edit");//edit submenu
		// --- Menu View
		Menu menuView = new Menu("View");//temp place holder
		ObservableList<String> clientList;//the actual string list that will go in the list pane
		ListView<String> list;//the list pane in the right pane
		Label searchLabel;
		TextField searchTextField;
		Button addClient, addLawn;
		HBox topPane = new HBox();//what goes in the top section of the layout
		HBox searchBox = new HBox();//contains the search label, and the search box
		HBox add = new HBox();//temp for what goes in the center section
		VBox rightPane = new VBox();//what goes in the right section of the layout
		BorderPane border = new BorderPane();//the layout for the scene, this layout has five sections: top, left, center, right, bottom

		menuBar.getMenus().addAll(menuFile, menuEdit, menuView);
		menuBar.setMinWidth(primaryStage.getMaxWidth());

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

		addClient = new Button("Client");

		addLawn = new Button("Lawn");

		topPane.getChildren().add(menuBar);

		searchBox.setSpacing(10);
		searchBox.setPadding(new Insets(0, 20, 5, 20));
		searchBox.getChildren().addAll(searchLabel, searchTextField);

		add.setSpacing(10);
		add.setPadding(new Insets(20, 20, 20, 20));

		add.getChildren().addAll(addClient, addLawn);

		rightPane.setSpacing(10);
		rightPane.setPadding(new Insets(20, 20, 20, 20));
		rightPane.getChildren().addAll(searchBox, list);

		border.setTop(topPane);
		border.setCenter(add);
		border.setRight(rightPane);

		//		grid.add(searchLabel, 0, 0);
		//		grid.add(searchTextField, 1, 0);

		//grid.add(searchBox, 1, 0);
		//grid.add(list, 1, 2);

		//primaryStage.setScene(new Scene(border, 1100, 600));
		((VBox) scene.getRoot()).getChildren().addAll(menuBar, border);
		primaryStage.setScene(scene);
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