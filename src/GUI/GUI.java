package GUI;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
import List.Client;
import List.FileIO;
import List.Lawn;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/*
 * This class contains all elements for the GUI. The idea is to have all elements of the graphical portion contained here, while
 * not including any of the logic needed to populate each field 
 * 
 */

public class GUI extends Application {

	FileIO io = new FileIO();
	int shown = 0;// 0 == client, 1 == lawn, 2 == checkedLawn
	Client tempClnt;
	Lawn tempLwn;

	@Override
	public void start(Stage primaryStage) throws Exception {

		if(io.isNew()) {

			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("First launch of program");
			alert.setHeaderText("Do you have a backup file, or are you starting for the first time?");
			alert.setContentText("Choose your option.");
			alert.initStyle(StageStyle.UNDECORATED);

			ButtonType buttonTypeOne = new ButtonType("Backup from file");
			ButtonType buttonTypeTwo = new ButtonType("New program");

			alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeOne){
				// ... user chose first option

				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Choose Backup File");
				fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
				File selectedFile = fileChooser.showOpenDialog(primaryStage);
				if (selectedFile != null) {
					io.setBackupFile(selectedFile);
					io.setBackupFileLocation(selectedFile.getAbsolutePath());
				}
				else {
					selectedFile = new File("BackupFile.txt");
					io.setBackupFile(selectedFile);
					io.setBackupFileLocation(selectedFile.getAbsolutePath());
				}

				io.readInBackupFile();

			} else {
				// ... user chose second option
				File temp;
				temp = new File("BackupFile.txt");
				io.setBackupFile(temp);
				io.setBackupFileLocation(temp.getAbsolutePath());
				io.addClient(new Client("Example Client", "123 Example Billing Address Ave"));

				TextInputDialog dialog = new TextInputDialog();
				dialog.setTitle("Backup Email");
				dialog.setHeaderText("LCMS will backup data to an email of your choice");
				dialog.setContentText("Please enter the desired email to use:");
				dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setVisible(false);
				dialog.initStyle(StageStyle.UNDECORATED);

				Alert confirmEmail = new Alert(AlertType.CONFIRMATION);
				confirmEmail.setTitle("Confirm Email Address");
				confirmEmail.setHeaderText("Is this email correct?");

				ButtonType correct = new ButtonType("Correct");
				ButtonType notCorrect = new ButtonType("Incorrect");

				confirmEmail.getButtonTypes().setAll(correct, notCorrect);

				boolean flag = true;
				while(flag) {

					// Traditional way to get the response value.
					Optional<String> resultEmail = dialog.showAndWait();
					if (resultEmail.isPresent()){

						System.out.println("Your email: " + resultEmail.get());

						confirmEmail.setContentText(resultEmail.get());

						Optional<ButtonType> confirmEmailBtn = confirmEmail.showAndWait();
						if (confirmEmailBtn.get() == correct){

							io.emailList.add(resultEmail.get());
							System.out.println(io.emailList.toString());
							flag = false;

						} else if (confirmEmailBtn.get() == notCorrect) {

							//resultEmail = dialog.showAndWait();

						}
					}
				}//end while
			}
		}
		else
			io.readInBackupFile();

		primaryStage.setTitle("Lawn Care Made Simple");//title
		Scene scene = new Scene(new VBox(), 1100, 600);//window size
		//primaryStage.getIcons().add(new Image("/src/lawnMower.png"));

		MenuBar menuBar = new MenuBar();//The menu for the topPane
		Menu menuFile = new Menu("File");//file submenu for the menu
		Menu menuEdit = new Menu("Edit");//edit submenu
		Menu menuView = new Menu("View");//view what is displayed in the right pane list
		MenuItem importList = new MenuItem("Import"),
				backUp = new MenuItem("Backup Information");
		MenuItem addItems = new MenuItem("Add Items"),
				addBackupMail = new MenuItem("Edit Backup Settings");
		MenuItem client = new MenuItem("Clients"),//menu items for the view option: current clients,
				lawn = new MenuItem("Lawns"),//     current lawns, which lawns have been taken care of
				chkdLwn = new MenuItem("Checked Lawns");

		ObservableList<String> list = FXCollections.<String>observableArrayList(io.getClientNames());//the actual string list that
		//will go in the list pane

		ListView<String> listView = new ListView<>(list);//the list pane in the right pane

		Label searchLabel = new Label("Search");//makes a label for the search bar
		Label cNameLbl  = new Label("              Name:"), 
				cBiAdLbl  = new Label("Billing Address:"),
				cName = new Label(),
				cAddr = new Label();
		Label lClientLbl = new Label("       Client Name:"),
				lAddressLbl = new Label("              Address:"),
				lLawnNameLbl = new Label("        Lawn Name:"),
				lGenLocationLbl = new Label("General Location:"),
				lIntervalLbl = new Label("     Interval(Days):"),
				lPriceLbl = new Label("                   Price:");

		TextField searchTextField = new TextField();//makes a search bar to search the list in the right pane
		TextField cNameTF  = new TextField(),
				cBiAdTF = new TextField();
		TextField lClientTF = new TextField(),
				lAddressTF = new TextField(),
				lLawnNameTF = new TextField(),
				lGenLocationTF = new TextField(),
				lIntervalTF = new TextField(),
				lPriceTF = new TextField();

		TextArea lawnTA = new TextArea();

		Button clntPageBtn = new Button("Client"),
				lwnPageBtn = new Button("Lawn"),
				addClntBtn = new Button("Add Client"),
				addLwnBtn = new Button("Add Lawn");
		Button cnclAddBtn = new Button("Cancel");
		Button editClntBtn = new Button("Edit Client"),
				editLwnBtn = new Button("Edit Lawn"),
				delClntBtn = new Button("Delete Client"),
				delLwnBtn = new Button("Delete Lawn");

		HBox topPane = new HBox();//what goes in the top section of the layout
		HBox searchBox = new HBox();//contains the search label, and the search box
		HBox centerPane = new HBox();//temp for what goes in the center section
		HBox btnPane = new HBox();//pane for the buttons to populate
		VBox rightPane = new VBox();//what goes in the right section of the layout
		VBox addClntLwnLbl = new VBox(),
				addClntLwnTF = new VBox();//for add clients or lawns
		VBox displayInfo = new VBox();//to display misc information
		VBox sidePanelBtn = new VBox();

		BorderPane border = new BorderPane();//the layout for the scene, this layout has five sections: top, left, center, right, bottom

		menuBar.getMenus().addAll(menuFile, menuEdit, menuView);

		menuFile.getItems().addAll(importList, backUp);

		menuEdit.getItems().addAll(addItems, addBackupMail);

		menuView.getItems().addAll(client, lawn, chkdLwn);

		importList.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				sidePanelBtn.getChildren().clear();

				File selectedFile = null;
				while(selectedFile == null) {
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("Choose Backup File");
					fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
					selectedFile = fileChooser.showOpenDialog(primaryStage);
					if (selectedFile != null) {
						io.setBackupFile(selectedFile);
						io.setBackupFileLocation(selectedFile.getAbsolutePath());
					}
					else
						break;
				}

			}

		});

		backUp.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				sidePanelBtn.getChildren().clear();

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Backup Confirmation");
				alert.setHeaderText("You have selected Backup");
				alert.setContentText("Do you want to create a backup file with the current information?");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK){
					// ... user chose OK
					//io.generateBackupFile(); this method should email the backup list to the user's email
				} else {
					// ... user chose CANCEL or closed the dialog
				}


			}//end handle

		});//end setonaction

		addItems.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				sidePanelBtn.getChildren().clear();
				addClntBtn.setText("Add Client");
				centerPane.getChildren().clear();
				centerPane.getChildren().addAll(clntPageBtn, lwnPageBtn);
				border.setCenter(centerPane);

			}//end handle

		});//end setonaction

		addBackupMail.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				TextInputDialog dialog = new TextInputDialog();
				dialog.setTitle("Email Input");
				dialog.setHeaderText("Enter an email address to update backup email address.");
				dialog.setContentText("Please enter email address:");

				// Traditional way to get the response value.
				Optional<String> result = dialog.showAndWait();
				if (result.isPresent()){
					System.out.println("Your name: " + result.get());
				}

			}//end handle

		});//end setonaction

		client.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent t) {

				sidePanelBtn.getChildren().clear();

				shown = 0;
				rightPane.getChildren().remove(1);
				rightPane.getChildren().add(1, populateList(listView, io.getClientNames()));

			}//end handle

		});//end setonaction

		lawn.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent t) {

				sidePanelBtn.getChildren().clear();

				shown = 1;
				rightPane.getChildren().remove(1);
				rightPane.getChildren().add(1, populateList(listView, io.getLawnNames()));

			}//end handle

		});//end setonaction

		chkdLwn.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent t) {

				sidePanelBtn.getChildren().clear();

				shown = 2;

			}//end handle

		});//end setonaction

		listView.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				tempClnt = io.getClient(io.getClientIndex(listView.getFocusModel().getFocusedItem()));
				cName.setText(tempClnt.getName());
				cAddr.setText(tempClnt.getBillAddress());
				lawnTA.clear();
				populateLawnTA(lawnTA, cName.getText());
				centerPane.getChildren().clear();
				addClntLwnLbl.getChildren().clear();
				addClntLwnLbl.setSpacing(20);
				addClntLwnLbl.setPadding(new Insets(20,2,20,20));
				addClntLwnLbl.getChildren().addAll(cNameLbl, cBiAdLbl);
				displayInfo.setSpacing(20);
				displayInfo.setPadding(new Insets(20,2,20,20));
				displayInfo.getChildren().clear();
				displayInfo.getChildren().addAll(cName, cAddr);
				centerPane.getChildren().addAll(addClntLwnLbl, displayInfo, lawnTA);
				border.setCenter(centerPane);
				sidePanelBtn.getChildren().addAll(editClntBtn, editLwnBtn, delClntBtn, delLwnBtn);
				border.setLeft(sidePanelBtn);

			}//end handle

		});//end setonmouseclicked

		searchTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {//creates a keylistener on the searchbox

			@Override
			public void handle(KeyEvent event) {

				//if(event.getCode().equals(KeyCode.ENTER)) {//when the enter key is pressed
				if(event.getCode().isLetterKey()) {

					rightPane.getChildren().remove(1);
					rightPane.getChildren().add(1, populateList(listView, search(searchTextField, listView)));
					//search the list for the name entered in the searchbox

				}

			}//end handle

		});//end setonkeypressed

		lawnTA.setEditable(false);
		lawnTA.setMinWidth(325);
		lawnTA.setMaxWidth(325);

		clntPageBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				centerPane.getChildren().clear();
				btnPane.getChildren().clear();
				cNameTF.setText("");
				cBiAdTF.setText("");
				addClntBtn.setText("Add Client");
				btnPane.getChildren().addAll(addClntBtn, cnclAddBtn);
				addClntLwnLbl.getChildren().clear();
				addClntLwnLbl.setSpacing(20);
				addClntLwnLbl.setPadding(new Insets(20,2,20,20));
				addClntLwnLbl.getChildren().addAll(cNameLbl, cBiAdLbl);
				addClntLwnTF.getChildren().clear();
				addClntLwnTF.setSpacing(11);
				addClntLwnTF.setPadding(new Insets(20,20,20,2));
				addClntLwnTF.getChildren().addAll(cNameTF, cBiAdTF, btnPane);
				centerPane.getChildren().addAll(addClntLwnLbl, addClntLwnTF);
				border.setCenter(centerPane);

			}//end handle

		});//end setOnAction

		lwnPageBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				centerPane.getChildren().clear();
				btnPane.getChildren().clear();
				addClntLwnLbl.getChildren().clear();
				lClientTF.setText("");
				lAddressTF.setText("");
				lLawnNameTF.setText("");
				lGenLocationTF.setText("");
				lIntervalTF.setText("");
				lPriceTF.setText("");
				btnPane.getChildren().addAll(addLwnBtn, cnclAddBtn);
				addClntLwnLbl.getChildren().clear();
				addClntLwnLbl.setSpacing(20);
				addClntLwnLbl.setPadding(new Insets(21,2,20,20));
				addClntLwnLbl.getChildren().addAll(lClientLbl, lAddressLbl, lLawnNameLbl, lGenLocationLbl, lIntervalLbl, lPriceLbl);
				addClntLwnTF.getChildren().clear();
				addClntLwnTF.setSpacing(11);
				addClntLwnTF.setPadding(new Insets(20,20,20,2));
				addClntLwnTF.getChildren().addAll(lClientTF, lAddressTF, lLawnNameTF, lGenLocationTF, lIntervalTF, lPriceTF, btnPane);
				centerPane.getChildren().addAll(addClntLwnLbl, addClntLwnTF);
				border.setCenter(centerPane);

			}//end handle

		});//end setonaction

		addClntBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(!cNameTF.getText().equals("") && !cBiAdTF.getText().equals("")) {

					if(addClntBtn.getText().equals("Update Client"))
						io.getClient(io.getClientIndex(tempClnt.getName())).setName(cNameTF.getText());
					else
						io.addClient(new Client(cNameTF.getText(), cBiAdTF.getText()));

					rightPane.getChildren().remove(1);
					if(shown == 0)
						rightPane.getChildren().add(1, populateList(listView, io.getClientNames()));
					else if(shown == 1)
						rightPane.getChildren().add(1, populateList(listView, io.getLawnNames()));

					cNameTF.setText("");
					cBiAdTF.setText("");
					addClntLwnLbl.getChildren().clear();
					addClntLwnTF.getChildren().clear();
					centerPane.getChildren().clear();
					//centerPane.getChildren().addAll(clntPageBtn, lwnPageBtn);
				}
				else {

					Alert alert = new Alert(AlertType.INFORMATION);//creates a dialog box warning the user that the client does not exist
					alert.setTitle("Client Creation Error");
					alert.setHeaderText(null);
					alert.setContentText("All fields must be filled in!");
					alert.showAndWait();

				}

			}//end handle

		});//end setonaction

		addLwnBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				int i = io.getClientIndex(lClientTF.getText());//checks to see if the client is in the list
				if(i != -1) {//if the client exists

					if(!lAddressTF.getText().equals("") && !lLawnNameTF.getText().equals("") && !lGenLocationTF.getText().equals("") && 
							!lIntervalTF.getText().equals("") && !lPriceTF.getText().equals("")) {

						io.addLawn(i, new Lawn(io.getClient(i), lAddressTF.getText(), lLawnNameTF.getText(),
								lGenLocationTF.getText(), Integer.parseInt(lIntervalTF.getText()), Double.parseDouble(lPriceTF.getText())));
						rightPane.getChildren().remove(1);
						if(shown == 0)
							rightPane.getChildren().add(1, populateList(listView, io.getClientNames()));
						else if(shown == 1)
							rightPane.getChildren().add(1, populateList(listView, io.getLawnNames()));
						lClientTF.setText("");
						lAddressTF.setText("");
						lLawnNameTF.setText("");
						lGenLocationTF.setText("");
						lIntervalTF.setText("");
						lPriceTF.setText("");
						addClntLwnLbl.getChildren().clear();
						addClntLwnTF.getChildren().clear();
						centerPane.getChildren().clear();
						centerPane.getChildren().addAll(clntPageBtn, lwnPageBtn);

					}
					else {

						Alert alert = new Alert(AlertType.INFORMATION);//creates a dialog box warning the user that the lawn had an error
						alert.setTitle("Lawn Creation Error");
						alert.setHeaderText(null);
						alert.setContentText("All fields must be filled in!");
						alert.showAndWait();

					}

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

				cNameTF.setText("");
				cBiAdTF.setText("");

				lClientTF.setText("");
				lAddressTF.setText("");
				lLawnNameTF.setText("");
				lGenLocationTF.setText("");
				lIntervalTF.setText("");
				lPriceTF.setText("");

				addClntLwnLbl.getChildren().clear();
				addClntLwnTF.getChildren().clear();
				centerPane.getChildren().clear();
				if(addClntBtn.getText().equals("Add Client"))
					centerPane.getChildren().addAll(clntPageBtn, lwnPageBtn);
				else if(addClntBtn.getText().equals("Update Client")) {
					centerPane.getChildren().addAll(addClntLwnLbl, displayInfo, lawnTA);
					border.setCenter(centerPane);
					sidePanelBtn.getChildren().addAll(editClntBtn, editLwnBtn, delClntBtn, delLwnBtn);
					border.setLeft(sidePanelBtn);	
				}

			}//end handle

		});//end setonaction

		editClntBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				sidePanelBtn.getChildren().clear();
				centerPane.getChildren().clear();
				btnPane.getChildren().clear();
				addClntBtn.setText("Update Client");
				btnPane.getChildren().addAll(addClntBtn, cnclAddBtn);
				addClntLwnLbl.getChildren().clear();
				addClntLwnLbl.setSpacing(20);
				addClntLwnLbl.setPadding(new Insets(20,2,20,20));
				addClntLwnLbl.getChildren().addAll(cNameLbl, cBiAdLbl);
				addClntLwnTF.getChildren().clear();
				addClntLwnTF.setSpacing(11);
				addClntLwnTF.setPadding(new Insets(20,20,20,2));
				cNameTF.setText(tempClnt.getName());
				cBiAdTF.setText(tempClnt.getBillAddress());
				addClntLwnTF.getChildren().addAll(cNameTF, cBiAdTF, btnPane);
				centerPane.getChildren().addAll(addClntLwnLbl, addClntLwnTF);
				border.setCenter(centerPane);

			}//end handle

		});//end setonaction

		editLwnBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				ObservableList<String> options = FXCollections.observableArrayList(tempClnt.getLawnNames());
				final ComboBox<String> comboBox = new ComboBox<>(options);
				centerPane.getChildren().clear();
				centerPane.getChildren().add(comboBox);
				
				comboBox.setOnMouseClicked(new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						
						tempLwn = tempClnt.getLawnFromAddress(comboBox.getValue());
						centerPane.getChildren().clear();
						btnPane.getChildren().clear();
						addClntLwnLbl.getChildren().clear();
						lClientTF.setText(tempLwn.getClient().toString());
						lAddressTF.setText(tempLwn.getAddress().toString());
						lLawnNameTF.setText(tempLwn.getLawnName().toString());
						lGenLocationTF.setText(tempLwn.getGenLocation().toString());
						lIntervalTF.setText("" + tempLwn.getInterval());
						lPriceTF.setText("" + tempLwn.getPrice());
						btnPane.getChildren().addAll(addLwnBtn, cnclAddBtn);
						addClntLwnLbl.getChildren().clear();
						addClntLwnLbl.setSpacing(20);
						addClntLwnLbl.setPadding(new Insets(21,2,20,20));
						addClntLwnLbl.getChildren().addAll(lClientLbl, lAddressLbl, lLawnNameLbl, lGenLocationLbl, lIntervalLbl, lPriceLbl);
						addClntLwnTF.getChildren().clear();
						addClntLwnTF.setSpacing(11);
						addClntLwnTF.setPadding(new Insets(20,20,20,2));
						addClntLwnTF.getChildren().addAll(lClientTF, lAddressTF, lLawnNameTF, lGenLocationTF, lIntervalTF, lPriceTF, btnPane);
						centerPane.getChildren().addAll(addClntLwnLbl, addClntLwnTF);
						border.setCenter(centerPane);
						
					}//end handle
					
				});//end setonmouseclicked

			}//end handle

		});//end setonaction

		delClntBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				sidePanelBtn.getChildren().clear();
				centerPane.getChildren().clear();
				border.setCenter(centerPane);

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Delete Client");
				alert.setHeaderText("Are you sure?");
				alert.setContentText("Deleting a client is permanant.");

				ButtonType buttonTypeOne = new ButtonType("Delete");
				ButtonType buttonTypeTwo = new ButtonType("Cancel");

				alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == buttonTypeOne){
					// ... user chose first option

					io.removeClient(io.getClientIndex(tempClnt.getName()));
					rightPane.getChildren().remove(1);
					if(shown == 0)
						rightPane.getChildren().add(1, populateList(listView, io.getClientNames()));
					else if(shown == 1)
						rightPane.getChildren().add(1, populateList(listView, io.getLawnNames()));

				}

			}//end handle

		});//end setonaction

		topPane.getChildren().add(menuBar);

		searchBox.setSpacing(10);
		searchBox.setPadding(new Insets(0, 20, 5, 20));
		searchBox.getChildren().addAll(searchLabel, searchTextField);

		centerPane.setSpacing(10);
		centerPane.setPadding(new Insets(20, 20, 20, 20));
		centerPane.setAlignment(Pos.CENTER);
		//centerPane.getChildren().addAll(clntPageBtn, lwnPageBtn);

		btnPane.setSpacing(10);
		btnPane.setPadding(new Insets(0, 20, 20, 20));

		rightPane.setSpacing(10);
		rightPane.setPadding(new Insets(20, 20, 20, 20));
		rightPane.getChildren().addAll(searchBox, listView);

		sidePanelBtn.setSpacing(10);
		sidePanelBtn.setPadding(new Insets(20, 20, 20, 20));
		sidePanelBtn.setAlignment(Pos.CENTER);

		border.setTop(topPane);
		border.setCenter(centerPane);
		border.setRight(rightPane);

		//primaryStage.setScene(new Scene(border, 1100, 600));
		((VBox) scene.getRoot()).getChildren().addAll(menuBar, border);
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			public void handle(WindowEvent we) {

				io.generateBackupFile();
				System.out.println("window is closing");

			}//end handle

		});//end setoncloserequest

	}//end start

	public void populateLawnTA(TextArea ta, String s) {

		Client temp = io.getClient(io.getClientIndex(s));

		for(int i = 0; i < temp.lawnListSize(); i++) {

			ta.insertText(i, "---------------------------------------------------------\n" + 
					temp.getSingleLawn(i).toString() + "\n");

		}

	}//end populateTA

	public ListView<String> populateList(ListView<String> listView, String[] s) {

		//list = FXCollections.<String>observableArrayList(s);
		listView.getItems().clear();
		listView.getItems().addAll(s);
		return listView;

	}//end populateList

	public String[] search(TextField search, ListView<String> list) {

		//String[] s;
		ArrayList<String> temp = new ArrayList<>();

		for(int i = 0; i < list.getItems().size(); i++)
			if(list.getItems().get(i).startsWith(search.getText()))
				temp.add(list.getItems().get(i));

		//return s = temp.toArray(new String[temp.size()]);
		return temp.toArray(new String[temp.size()]);

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