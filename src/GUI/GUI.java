package GUI;
import java.util.ArrayList;

import List.Client;
import List.FileIO;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
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
		TextField searchTextField = new TextField();//makes a search bar to search the list in the right pane
		TextField cNameTF  = new TextField(),
				cBiAdTF = new TextField(),
				cOwedTF = new TextField();
		
		Button clntPageBtn = new Button("Client"),
				lwnPageBtn = new Button("Lawn"),
				addClntBtn = new Button("Add Client");
		
		HBox topPane = new HBox();//what goes in the top section of the layout
		HBox searchBox = new HBox();//contains the search label, and the search box
		HBox centerPane = new HBox();//temp for what goes in the center section
		VBox rightPane = new VBox();//what goes in the right section of the layout
		VBox addClntLbl = new VBox(),
				addClntTF = new VBox();
		
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
				
				//add.setVisible(false);
				centerPane.getChildren().removeAll(clntPageBtn, lwnPageBtn);
				centerPane.getChildren().addAll(addClntLbl, addClntTF);
				border.setCenter(centerPane);
				
			}//end handle
			
		});//end setOnAction
		
		addClntBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				io.addClient(new Client(cNameTF.getText(), cBiAdTF.getText(), Double.parseDouble(cOwedTF.getText())));
				rightPane.getChildren().remove(1);
				rightPane.getChildren().add(1, populateList(listView, list, io.getClientName()));
				cNameTF.setText("");
				cBiAdTF.setText("");
				cOwedTF.setText("");
				centerPane.getChildren().removeAll(addClntLbl, addClntTF);
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
		
		addClntLbl.setSpacing(20);
		addClntLbl.setPadding(new Insets(20,2,20,20));
		addClntLbl.getChildren().addAll(cNameLbl, cBiAdLbl, cOwedLbl);
		addClntTF.setSpacing(10);
		addClntTF.setPadding(new Insets(20,20,20,2));
		addClntTF.getChildren().addAll(cNameTF, cBiAdTF, cOwedTF, addClntBtn);

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