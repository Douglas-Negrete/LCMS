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

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(0, 15, 15, 0));

		MenuBar menuBar = new MenuBar();
		// --- Menu File
		Menu menuFile = new Menu("File");
		// --- Menu Edit
		Menu menuEdit = new Menu("Edit");
		// --- Menu View
		Menu menuView = new Menu("View");
		menuBar.getMenus().addAll(menuFile, menuEdit, menuView);
		
		HBox topPane = new HBox();

		HBox searchBox = new HBox();
		searchBox.setSpacing(10);
		searchBox.setPadding(new Insets(5, 20, 5, 20));

		VBox rightPane = new VBox();
		rightPane.setSpacing(10);
		rightPane.setPadding(new Insets(20, 20, 20, 20));

		HBox add = new HBox();
		add.setSpacing(10);
		add.setPadding(new Insets(20, 20, 20, 20));

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

		topPane.getChildren().add(menuBar);
		
		rightPane.getChildren().addAll(searchBox, list);

		//		grid.add(searchLabel, 0, 0);
		//		grid.add(searchTextField, 1, 0);
		grid.add(topPane, 0, 0);
		grid.add(add, 0, 1);
		grid.add(rightPane, 1, 0);
		//grid.add(searchBox, 1, 0);
		//grid.add(list, 1, 2);

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
