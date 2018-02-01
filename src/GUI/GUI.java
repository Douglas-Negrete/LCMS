package GUI;

import java.util.ArrayList;

import List.Client;
import List.FileIO;
import List.Lawn;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/*
 * This class contains all elements for the GUI. The idea is to have all elements of the graphical portion contained here, while
 * not including any of the logic needed to populate each field 
 * 
 */

public class GUI extends Application {

	FileIO io = new FileIO();

	@Override
	public void start(Stage primaryStage) throws Exception {

		primaryStage.setTitle("Lawn Care Made Simple");//title
		Scene scene = new Scene(new VBox(), 1100, 600);//window size
		//primaryStage.getIcons().add(new Image("/src/lawnMower.png"));

		MenuBar menuBar = new MenuBar();//The menu for the topPane
		Menu menuFile = new Menu("File");//file submenu for the menu
		Menu menuEdit = new Menu("Edit");//edit submenu
		Menu menuView = new Menu("View");//view what is displayed in the right pane list
		MenuItem client = new MenuItem("Clients"),//menu items for the view option: current clients,
				lawn = new MenuItem("Lawns"),//     current lawns, which lawns have been taken care of
				chkdLwn = new MenuItem("Checked Lawns");

		ObservableList<String> list = FXCollections.<String>observableArrayList(io.getClientName());//the actual string list that
		//will go in the list pane

		ListView<String> listView = new ListView<>(list);//the list pane in the right pane

		Label searchLabel = new Label("Search");//makes a label for the search bar
		Label cNameLbl  = new Label("              Name:"), 
				cBiAdLbl  = new Label("Billing Address:"),
				cOwedLbl = new Label("Amount Owed:");
		Label lClientLbl = new Label("       Client Name:"),
				lAddressLbl = new Label("              Address:"),
				lLawnNameLbl = new Label("        Lawn Name:"),
				lGenLocationLbl = new Label("General Location:"),
				lIntervalLbl = new Label("     Interval(Days):"),
				lPriceLbl = new Label("                   Price:");

		TextField searchTextField = new TextField();//makes a search bar to search the list in the right pane
		TextField cNameTF  = new TextField(),
				cBiAdTF = new TextField(),
				cOwedTF = new TextField();
		TextField lClientTF = new TextField(),
				lAddressTF = new TextField(),
				lLawnNameTF = new TextField(),
				lGenLocationTF = new TextField(),
				lIntervalTF = new TextField(),
				lPriceTF = new TextField();

		Button clntPageBtn = new Button("Client"),
				lwnPageBtn = new Button("Lawn"),
				addClntBtn = new Button("Add Client"),
				addLwnBtn = new Button("Add Lawn");
		Button cnclAddBtn = new Button("Cancel");

		HBox topPane = new HBox();//what goes in the top section of the layout
		HBox searchBox = new HBox();//contains the search label, and the search box
		HBox centerPane = new HBox();//temp for what goes in the center section
		HBox btnPane = new HBox();//pane for the buttons to populate
		VBox rightPane = new VBox();//what goes in the right section of the layout
		VBox addClntLwnLbl = new VBox(),
				addClntLwnTF = new VBox();

		BorderPane border = new BorderPane();//the layout for the scene, this layout has five sections: top, left, center, right, bottom

		menuBar.getMenus().addAll(menuFile, menuEdit, menuView);

		lawn.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent t) {

				//list = FXCollections.<String>observableArrayList(io.getLawnName());
				populateList(listView, list, io.getLawnName());
				listView.setVisible(true);

			}//end handle

		});//end setonaction

		menuView.getItems().addAll(client, lawn, chkdLwn);

		searchTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {//creates a keylistener on the searchbox

			@Override
			public void handle(KeyEvent event) {

				if(event.getCode().equals(KeyCode.ENTER)) {//when the enter key is pressed

					search(searchTextField, listView);//search the list for the name entered in the searchbox

				}

			}//end handle

		});//end setonkeypressed

		clntPageBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				centerPane.getChildren().clear();
				addClntLwnLbl.setSpacing(20);
				addClntLwnLbl.setPadding(new Insets(20,2,20,20));
				addClntLwnLbl.getChildren().addAll(cNameLbl, cBiAdLbl, cOwedLbl, addClntBtn);
				addClntLwnTF.setSpacing(10);
				addClntLwnTF.setPadding(new Insets(20,20,20,2));
				addClntLwnTF.getChildren().addAll(cNameTF, cBiAdTF, cOwedTF, cnclAddBtn);
				centerPane.getChildren().addAll(addClntLwnLbl, addClntLwnTF);
				border.setCenter(centerPane);

			}//end handle

		});//end setOnAction

		lwnPageBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				centerPane.getChildren().clear();
				addClntLwnLbl.setSpacing(20);
				addClntLwnLbl.setPadding(new Insets(20,2,20,20));
				addClntLwnLbl.getChildren().addAll(lClientLbl, lAddressLbl, lLawnNameLbl, lGenLocationLbl, lIntervalLbl, lPriceLbl, addLwnBtn);
				addClntLwnTF.setSpacing(10);
				addClntLwnTF.setPadding(new Insets(20,20,20,2));
				addClntLwnTF.getChildren().addAll(lClientTF, lAddressTF, lLawnNameTF, lGenLocationTF, lIntervalTF, lPriceTF, cnclAddBtn);
				centerPane.getChildren().addAll(addClntLwnLbl, addClntLwnTF);
				border.setCenter(centerPane);

			}//end handle

		});//end setonaction

		addClntBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				io.addClient(new Client(cNameTF.getText(), cBiAdTF.getText(), Double.parseDouble(cOwedTF.getText())));
				rightPane.getChildren().remove(1);
				rightPane.getChildren().add(1, populateList(listView, list, io.getClientName()));
				cNameTF.setText("");
				cBiAdTF.setText("");
				cOwedTF.setText("");
				addClntLwnLbl.getChildren().clear();
				addClntLwnTF.getChildren().clear();
				centerPane.getChildren().addAll(clntPageBtn, lwnPageBtn);

			}//end handle

		});//end setonaction

		addLwnBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				int i = io.getClientIndex(lClientTF.getText());//checks to see if the client is in the list
				if(i != -1) {//if the client exists
					
					io.addLawn(i, new Lawn(lClientTF.getText(), lAddressTF.getText(), lLawnNameTF.getText(),
							lGenLocationTF.getText(), Integer.parseInt(lIntervalTF.getText()), Double.parseDouble(lPriceTF.getText())));
					lClientTF.setText("");
					lAddressTF.setText("");
					lLawnNameTF.setText("");
					lGenLocationTF.setText("");
					lIntervalTF.setText("");
					lPriceTF.setText("");
					addClntLwnLbl.getChildren().clear();
					addClntLwnTF.getChildren().clear();
					centerPane.getChildren().addAll(clntPageBtn, lwnPageBtn);
					
				}
				else {//the client does not exist
					
					lClientTF.setText("");//clears the client name area
					
					Alert alert = new Alert(AlertType.INFORMATION);//creates a dialog box warning the user that the client does not exist
					alert.setTitle("Lawn Creation Error");
					alert.setHeaderText(null);
					alert.setContentText("The client entered does not exist!");
					alert.showAndWait();
					
				}

			}//end handle

		});//end setonaction
		
		cnclAddBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				addClntLwnLbl.getChildren().clear();
				addClntLwnTF.getChildren().clear();
				centerPane.getChildren().addAll(clntPageBtn, lwnPageBtn);
				
			}//end handle
			
		});//end setonaction

		topPane.getChildren().add(menuBar);

		searchBox.setSpacing(10);
		searchBox.setPadding(new Insets(0, 20, 5, 20));
		searchBox.getChildren().addAll(searchLabel, searchTextField);

		centerPane.setSpacing(10);
		centerPane.setPadding(new Insets(20, 20, 20, 20));

		centerPane.getChildren().addAll(clntPageBtn, lwnPageBtn);

		rightPane.setSpacing(10);
		rightPane.setPadding(new Insets(20, 20, 20, 20));
		rightPane.getChildren().addAll(searchBox, listView);

		border.setTop(topPane);
		border.setCenter(centerPane);
		border.setRight(rightPane);

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

	public ListView<String> populateList(ListView<String> lv, ObservableList<String> list, String[] s) {

		list = FXCollections.<String>observableArrayList(s);
		return new ListView<>(list);
		//System.out.println(list.toString());

	}//end populateList

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