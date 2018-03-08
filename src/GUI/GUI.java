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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
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
					io.readInBackupFile();
					io.populateLawns();
					io.setEmailData();
				}

			} else {
				// ... user chose second option
				File temp;
				temp = new File("BackupFile.txt");
				io.setBackupFile(temp);
				io.setBackupFileLocation(temp.getAbsolutePath());
				io.addClient(new Client("Example Client", "123 Example Billing Address Ave", "(999) 999-9999"));

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
		else {
			io.readInBackupFile();
			io.populateLawns();
			io.setEmailData();
		}

		primaryStage.setTitle("Lawn Care Made Simple");//title
		Scene scene = new Scene(new VBox(), 1100, 600);//window size
		//primaryStage.getIcons().add(new Image("/src/lawnMower.png"));

		MenuBar menuBar = new MenuBar();//The menu for the topPane
		Menu menuFile = new Menu("File");//file submenu for the menu
		Menu menuView = new Menu("View");//view what is displayed in the right pane list
		Menu menuBackup = new Menu("Backup");
		Menu menuPreferences = new Menu("Preferences");
		MenuItem importList = new MenuItem("Import"),
				save = new MenuItem("Save");
		MenuItem client = new MenuItem("Clients"),//menu items for the view option: current clients,
				lawn = new MenuItem("Lawns"),//     current lawns, which lawns have been taken care of
				transactions = new MenuItem("Transactions");
		MenuItem emailFile = new MenuItem("Backup File"),
				emailTrans = new MenuItem("Backup Transactions"),
				emailBill = new MenuItem("Backup Bills"),
				addBackupMail = new MenuItem("Edit Backup Email");
		MenuItem settings = new MenuItem("Settings"),
				help = new MenuItem("Help");

		ObservableList<String> list = FXCollections.<String>observableArrayList(io.getClientNames());//the actual string list that
		//will go in the list pane

		ListView<String> listView = new ListView<>(list);//the list pane in the right pane

		Label searchLabel = new Label("Search:");//makes a label for the search bar
		Label cNameLbl  = new Label("Name:"), 
				cBiAdLbl  = new Label("Billing Address:"),
				cOwesLbl = new Label("Owes:"),
				cName = new Label(),
				cAddr = new Label(),
				cOwes = new Label();
		Label lClientLbl = new Label("Client Name:"),
				lAddressLbl = new Label("Address:"),
				lLawnNameLbl = new Label("Lawn Name:"),
				lGenLocationLbl = new Label("General Location:"),
				lIntervalLbl = new Label("Interval(Days):"),
				lPriceLbl = new Label("Price:");
		Label iSortedLawnsLbl = new Label("Lawns by Next Mow Date");
		Label sCompanyNameLbl = new Label("Set Company Name:"),
				sAutoBackupLbl = new Label("Set Auto Backup:"),
				sDisableServerLbl = new Label("Disable Server:"),
				sEditEmailsLbl = new Label("Edit Emails:");
		Label backupEmailLbl = new Label("The backup will be sent to this email:"),
				backupTitleLbl = new Label("");

		TextField searchTextField = new TextField();//makes a search bar to search the list in the right pane
		TextField cNameTF  = new TextField(),
				cBiAdTF = new TextField();
		TextField lClientTF = new TextField(),
				lAddressTF = new TextField(),
				lLawnNameTF = new TextField(),
				lGenLocationTF = new TextField(),
				lIntervalTF = new TextField(),
				lPriceTF = new TextField();
		TextField sCompanyNameTF = new TextField();
		TextField bEmail = new TextField();
		
		Spinner<Integer> spin = new Spinner<>(0,31,7,1);
		CheckBox disableServerCheckBox = new CheckBox();
		ComboBox<String> emailComboBox = new ComboBox<>();

		TextArea lawnTA = new TextArea();
		TextArea notesTA = new TextArea();
		TextArea sortedLawnTA = new TextArea();

		Button clntPageBtn = new Button("New Client"),
				lwnPageBtn = new Button("New Lawn"),
				addClntBtn = new Button("Add Client"),
				addLwnBtn = new Button("Add Lawn");
		Button cnclAddBtn = new Button("Cancel");
		Button editClntBtn = new Button("Edit Client"),
				editLwnBtn = new Button("Edit Lawn"),
				delClntBtn = new Button("Delete Client"),
				delLwnBtn = new Button("Delete Lawn");
		Button lSkipBtn = new Button("Skip"),
				lMowedBtn = new Button("Mowed"),
				lStopMowBtn = new Button("Stop Mowing");
		Button sAddBtn = new Button("Add"),
				sDelBtn = new Button("Delete"),
				sUpdateBtn = new Button("Update");
		Button bSendBtn = new Button("Send");

		HBox topPane = new HBox();//what goes in the top section of the layout
		HBox searchBox = new HBox();//contains the search label, and the search box
		HBox addItemsBox = new HBox();//for the add item buttons
		HBox centerPane = new HBox();//temp for what goes in the center section
		HBox btnPane = new HBox();//pane for the buttons to populate
		HBox settingsBtnPane = new HBox(),
				settingsTFPane = new HBox();
		HBox iAddressBox = new HBox(),
				iCostIntervalBox = new HBox();
		
		VBox rightPane = new VBox();//what goes in the right section of the layout
		VBox leftPane = new VBox();
		VBox addClntLwnLbl = new VBox(),
				addClntLwnTF = new VBox();//for add clients or lawns
		VBox displayInfo = new VBox();//to display misc information
		VBox sidePanelBtn = new VBox();
		VBox settingsLbl = new VBox();
		VBox settingsItems = new VBox();

		BorderPane border = new BorderPane();//the layout for the scene, this layout has five sections: top, left, center, right, bottom

		menuBar.getMenus().addAll(menuFile, menuView, menuBackup, menuPreferences);

		menuFile.getItems().addAll(importList, save);

		menuView.getItems().addAll(client, lawn, transactions);

		menuBackup.getItems().addAll(emailFile, emailTrans, emailBill, addBackupMail);

		menuPreferences.getItems().addAll(settings, help);

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

			}//end handle

		});//end setonaction importList

		save.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				sidePanelBtn.getChildren().clear();

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Save Confirmation");
				alert.setHeaderText("You have selected save file");
				alert.setContentText("This does not create a new backup file, this option saves current data. Do you wish to continue?");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK){
					// ... user chose OK
					io.generateBackupFile();
					
					alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Backup Completed");
					alert.setHeaderText("You have successfully saved!");
					alert.setContentText("You have saved the file to the location: " + io.getBackupLocation());
					alert.show();
				} else {
					// ... user chose CANCEL or closed the dialog
				}


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
					io.setBackupEmail(result.get());
					System.out.println("Your name: " + result.get());
				}

			}//end handle

		});//end setonaction

		client.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent t) {

				sidePanelBtn.getChildren().clear();
				leftPane.getChildren().clear();
				displayInfo.getChildren().clear();

				shown = 0;
				rightPane.getChildren().remove(1);
				rightPane.getChildren().add(1, populateList(listView, io.getClientNames()));

			}//end handle

		});//end setonaction

		lawn.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent t) {

				sidePanelBtn.getChildren().clear();
				centerPane.getChildren().clear();
				displayInfo.getChildren().clear();

				shown = 1;
				rightPane.getChildren().remove(1);
				rightPane.getChildren().add(1, populateList(listView, io.getLawnNames()));
				leftPane.getChildren().clear();
				sortedLawnTA.clear();
				populateSortedLawnTA(sortedLawnTA);
				leftPane.getChildren().addAll(iSortedLawnsLbl, sortedLawnTA);
				border.setLeft(leftPane);

			}//end handle

		});//end setonaction

		transactions.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent t) {

				sidePanelBtn.getChildren().clear();

				//shown = 2;
				//rightPane.getChildren().remove(1);
				//rightPane.getChildren().add(1, populateList(listView, io.getCheckedLawns()));

			}//end handle

		});//end setonaction
		
		emailFile.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				sidePanelBtn.getChildren().clear();
				leftPane.getChildren().clear();
				
				displayInfo.getChildren().clear();
				backupTitleLbl.setText("Backup File");
				centerPane.getChildren().clear();
				bEmail.setPromptText(io.getBackupEmail());
				bEmail.setEditable(false);
				bEmail.setFocusTraversable(false);
				centerPane.getChildren().addAll(backupEmailLbl, bEmail, bSendBtn);
				lawnTA.clear();
				lawnTA.autosize();
				io.printBackupFileTA(lawnTA);
				lawnTA.autosize();
				displayInfo.getChildren().addAll(backupTitleLbl, centerPane, lawnTA);
				border.setCenter(displayInfo);
				
			}//end handle
			
		});//end setonaction emailFile
		
		emailTrans.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				sidePanelBtn.getChildren().clear();
				
				displayInfo.getChildren().clear();
				backupTitleLbl.setText("Backup Transactions");
				centerPane.getChildren().clear();
				bEmail.setPromptText(io.getBackupEmail());
				bEmail.setEditable(false);
				bEmail.setFocusTraversable(false);
				centerPane.getChildren().addAll(backupEmailLbl, bEmail, bSendBtn);
				lawnTA.clear();
				lawnTA.autosize();
				displayInfo.getChildren().addAll(backupTitleLbl, centerPane, lawnTA);
				border.setCenter(displayInfo);
				
			}//end handle
			
		});//end setonaction emailttrans
		
		emailBill.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				sidePanelBtn.getChildren().clear();
				
				displayInfo.getChildren().clear();
				backupTitleLbl.setText("Backup Bills");
				centerPane.getChildren().clear();
				bEmail.setPromptText(io.getBackupEmail());
				bEmail.setEditable(false);
				bEmail.setFocusTraversable(false);
				centerPane.getChildren().addAll(backupEmailLbl, bEmail, bSendBtn);
				lawnTA.clear();
				lawnTA.autosize();
				displayInfo.getChildren().addAll(backupTitleLbl, centerPane, lawnTA);
				border.setCenter(displayInfo);
				
			}//end handle
			
		});//end setonaction emailbill

		settings.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				sidePanelBtn.getChildren().clear();

				ObservableList<String> options = FXCollections.observableArrayList(io.emailList);
				emailComboBox.setItems(options);
				
				sCompanyNameTF.setPromptText(io.companyName);

				settingsItems.getChildren().clear();
				settingsItems.getChildren().addAll(settingsTFPane, spin, disableServerCheckBox, emailComboBox, settingsBtnPane);
				settingsItems.setAlignment(Pos.CENTER_LEFT);

				centerPane.getChildren().clear();
				centerPane.getChildren().addAll(settingsLbl, settingsItems);
				border.setCenter(centerPane);

			}//end handle

		});//end setonaction settings

		help.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {



			}//end handle

		});//end setonaction help

		listView.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				if(shown == 0) {

					tempClnt = io.getClient(io.getClientIndex(listView.getFocusModel().getFocusedItem()));
					cName.setText(tempClnt.getName());
					cAddr.setText(tempClnt.getBillAddress());
					cOwes.setText("" + tempClnt.getOwed());
					lawnTA.clear();
					populateLawnTA(lawnTA, cName.getText());
					centerPane.getChildren().clear();
					addClntLwnLbl.getChildren().clear();
					addClntLwnLbl.getChildren().addAll(cNameLbl, cBiAdLbl, cOwesLbl);
					displayInfo.getChildren().clear();
					displayInfo.getChildren().addAll(cName, cAddr, cOwes);
					centerPane.getChildren().addAll(addClntLwnLbl, displayInfo, lawnTA);
					border.setCenter(centerPane);
					sidePanelBtn.getChildren().clear();
					sidePanelBtn.getChildren().addAll(editClntBtn, editLwnBtn, delClntBtn, delLwnBtn);
					border.setLeft(sidePanelBtn);

				}
				else if(shown == 1) {
					
					tempLwn = io.lawnList.get(listView.getFocusModel().getFocusedIndex());
					System.out.println(tempLwn.toString());
					displayInfo.getChildren().clear();
					notesTA.clear();
					notesTA.setMaxWidth(325);
					notesTA.setMinHeight(400);
					notesTA.appendText("Name:\t\t\t" + tempLwn.getLawnName() + "\n");
					notesTA.appendText("Address:\t\t\t" + tempLwn.getAddress() + "\n");
					notesTA.appendText("General Location:\t" + tempLwn.getGenLocation() + "\n");
					notesTA.appendText("Client:\t\t\t" + tempLwn.getClient().getName() + "\n");
					notesTA.appendText("Last Mowed:\t\t" + tempLwn.getLastMow() + "\n");
					notesTA.appendText("Next Mow:\t\t" + tempLwn.getNextMow() + "\n");
					notesTA.appendText("Cost:\t\t\t\t" + tempLwn.getPrice() + "\n");
					notesTA.appendText("Interval:\t\t\t" + tempLwn.getInterval() + "\n");
					notesTA.appendText("------------------------------------------------------\n");
					notesTA.appendText("Notes\n");
					btnPane.getChildren().clear();
					btnPane.getChildren().addAll(lMowedBtn, lSkipBtn, lStopMowBtn, editLwnBtn);
					displayInfo.getChildren().addAll(notesTA, btnPane);
					border.setCenter(displayInfo);
					
				}

			}//end handle

		});//end setonmouseclicked
		
		listView.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				
				if(shown == 0) {

					tempClnt = io.getClient(io.getClientIndex(listView.getFocusModel().getFocusedItem()));
					cName.setText(tempClnt.getName());
					cAddr.setText(tempClnt.getBillAddress());
					cOwes.setText("" + tempClnt.getOwed());
					lawnTA.clear();
					populateLawnTA(lawnTA, cName.getText());
					centerPane.getChildren().clear();
					addClntLwnLbl.getChildren().clear();
					addClntLwnLbl.getChildren().addAll(cNameLbl, cBiAdLbl, cOwesLbl);
					displayInfo.getChildren().clear();
					displayInfo.getChildren().addAll(cName, cAddr, cOwes);
					centerPane.getChildren().addAll(addClntLwnLbl, displayInfo, lawnTA);
					border.setCenter(centerPane);
					sidePanelBtn.getChildren().clear();
					sidePanelBtn.getChildren().addAll(editClntBtn, editLwnBtn, delClntBtn, delLwnBtn);
					border.setLeft(sidePanelBtn);

				}
				else if(shown == 1) {
					
					tempLwn = io.lawnList.get(listView.getFocusModel().getFocusedIndex());
					System.out.println(tempLwn.toString());
					displayInfo.getChildren().clear();
					notesTA.clear();
					notesTA.setMaxWidth(325);
					notesTA.setMinHeight(400);
					notesTA.appendText("Name:\t\t\t" + tempLwn.getLawnName() + "\n");
					notesTA.appendText("Address:\t\t\t" + tempLwn.getAddress() + "\n");
					notesTA.appendText("General Location:\t" + tempLwn.getGenLocation() + "\n");
					notesTA.appendText("Client:\t\t\t" + tempLwn.getClient().getName() + "\n");
					notesTA.appendText("Last Mowed:\t\t" + tempLwn.getLastMow() + "\n");
					notesTA.appendText("Next Mow:\t\t" + tempLwn.getNextMow() + "\n");
					notesTA.appendText("Cost:\t\t\t\t" + tempLwn.getPrice() + "\n");
					notesTA.appendText("Interval:\t\t\t" + tempLwn.getInterval() + "\n");
					notesTA.appendText("------------------------------------------------------\n");
					notesTA.appendText("Notes\n");
					btnPane.getChildren().clear();
					btnPane.getChildren().addAll(lMowedBtn, lSkipBtn, lStopMowBtn, editLwnBtn);
					displayInfo.getChildren().addAll(notesTA, btnPane);
					border.setCenter(displayInfo);
					
				}

			}//end handle

		});//end setonkeypressed
		
		iSortedLawnsLbl.setFont(new Font(20));

		searchTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {//creates a keylistener on the searchbox

			@Override
			public void handle(KeyEvent event) {

				if(event.getCode().equals(KeyCode.ENTER)) {//when the enter key is pressed

					rightPane.getChildren().remove(1);
					if(shown == 0)
						rightPane.getChildren().add(1, populateList(listView, search(searchTextField, io.getClientNames())));
					else if(shown == 1)
						rightPane.getChildren().add(1, populateList(listView, search(searchTextField, io.getLawnNames())));
					//search the list for the name entered in the searchbox

				}

			}//end handle

		});//end setonkeypressed

		lawnTA.setEditable(false);
		lawnTA.setMinWidth(325);
		lawnTA.setMaxWidth(440);
		lawnTA.setMinHeight(400);
		lawnTA.setMaxHeight(500);
		//lawnTA.setBackground(new Background(new BackgroundFill(Color.CRIMSON, null, null)));
		
		sortedLawnTA.setEditable(false);
		sortedLawnTA.setMinWidth(250);
		sortedLawnTA.setMaxWidth(300);
		sortedLawnTA.setMinHeight(400);
		sortedLawnTA.setMaxHeight(500);

		clntPageBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				sidePanelBtn.getChildren().clear();

				centerPane.getChildren().clear();
				btnPane.getChildren().clear();
				cNameTF.setText("");
				cBiAdTF.setText("");
				addClntBtn.setText("Add Client");
				btnPane.getChildren().addAll(addClntBtn, cnclAddBtn);
				addClntLwnLbl.getChildren().clear();
				addClntLwnLbl.getChildren().addAll(cNameLbl, cBiAdLbl);
				addClntLwnTF.getChildren().clear();
				addClntLwnTF.getChildren().addAll(cNameTF, cBiAdTF, btnPane);
				centerPane.getChildren().addAll(addClntLwnLbl, addClntLwnTF);
				border.setCenter(centerPane);

			}//end handle

		});//end setOnAction

		lwnPageBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				sidePanelBtn.getChildren().clear();

				centerPane.getChildren().clear();
				btnPane.getChildren().clear();
				addLwnBtn.setText("Add Lawn");
				lClientTF.setText("");
				lAddressTF.setText("");
				lLawnNameTF.setText("");
				lGenLocationTF.setText("");
				lIntervalTF.setText("");
				lPriceTF.setText("");
				btnPane.getChildren().addAll(addLwnBtn, cnclAddBtn);
				addClntLwnLbl.getChildren().clear();
				addClntLwnLbl.getChildren().addAll(lClientLbl, lAddressLbl, lLawnNameLbl, lGenLocationLbl, lIntervalLbl, lPriceLbl);
				addClntLwnTF.getChildren().clear();
				addClntLwnTF.getChildren().addAll(lClientTF, lAddressTF, lLawnNameTF, lGenLocationTF, lIntervalTF, lPriceTF, btnPane);
				centerPane.getChildren().addAll(addClntLwnLbl, addClntLwnTF);
				border.setCenter(centerPane);

			}//end handle

		});//end setonaction

		addClntBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(!cNameTF.getText().equals("") && !cBiAdTF.getText().equals("")) {

					if(addClntBtn.getText().equals("Update Client")) {
						io.getClient(io.getClientIndex(tempClnt.getName())).setName(cNameTF.getText());
						io.getClient(io.getClientIndex(tempClnt.getName())).setBillAddress(cBiAdTF.getText());;
					}
					else
						io.addClient(new Client(cNameTF.getText(), cBiAdTF.getText(), "DUMMY PHONE NUMBER"));

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
								lGenLocationTF.getText(), Integer.parseInt(lIntervalTF.getText()), Double.parseDouble(lPriceTF.getText()),0));

						rightPane.getChildren().remove(1);
						if(shown == 0)
							rightPane.getChildren().add(1, populateList(listView, io.getClientNames()));
						else if(shown == 1)
							rightPane.getChildren().add(1, populateList(listView, io.getLawnNames()));

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

					Alert alert = new Alert(AlertType.INFORMATION);//creates a dialog box warning the user that the client does not exist
					alert.setTitle("Lawn Creation Error");
					alert.setHeaderText(null);
					alert.setContentText("The client entered does not exist!");

					ButtonType add = new ButtonType("Add Client");
					ButtonType ok = new ButtonType("Okay");

					alert.getButtonTypes().setAll(ok, add);
					Optional<ButtonType> choice = alert.showAndWait();

					if(choice.get() == add) {

						TextInputDialog dialog = new TextInputDialog();
						dialog.setTitle("Add Client");
						dialog.setHeaderText("A client with the entered name will be created");
						dialog.setContentText("Please enter a billing address for the client:");
						dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setVisible(false);
						dialog.initStyle(StageStyle.UNDECORATED);

						Alert confirmAddress = new Alert(AlertType.CONFIRMATION);
						confirmAddress.setTitle("Confirm Billing Address");
						confirmAddress.setHeaderText("Is this address correct?");

						ButtonType correct = new ButtonType("Correct");
						ButtonType notCorrect = new ButtonType("Incorrect");

						confirmAddress.getButtonTypes().setAll(correct, notCorrect);

						boolean flag = true;
						while(flag) {

							// Traditional way to get the response value.
							Optional<String> resultAddress = dialog.showAndWait();
							if (resultAddress.isPresent()){

								System.out.println("Your bAddress: " + resultAddress.get());

								confirmAddress.setContentText(resultAddress.get());

								Optional<ButtonType> confirmEmailBtn = confirmAddress.showAndWait();
								if (confirmEmailBtn.get() == correct){

									io.addClient(new Client(lClientTF.getText(), resultAddress.get(), "DUMMY PHONE NUMBER"));

									io.addLawn(io.getClientIndex(lClientTF.getText()), 
											new Lawn(io.getClient(io.getClientIndex(lClientTF.getText())), lAddressTF.getText(), 
													lLawnNameTF.getText(), lGenLocationTF.getText(), Integer.parseInt(lIntervalTF.getText()), 
													Double.parseDouble(lPriceTF.getText()),0));

									rightPane.getChildren().remove(1);
									if(shown == 0)
										rightPane.getChildren().add(1, populateList(listView, io.getClientNames()));
									else if(shown == 1)
										rightPane.getChildren().add(1, populateList(listView, io.getLawnNames()));

									addClntLwnLbl.getChildren().clear();
									addClntLwnTF.getChildren().clear();
									centerPane.getChildren().clear();
									centerPane.getChildren().addAll(clntPageBtn, lwnPageBtn);

									flag = false;

								} else if (confirmEmailBtn.get() == notCorrect) {

									//resultEmail = dialog.showAndWait();

								}

							}//end is billing address present

						}//end while

					}//end add new client
					else {
						lClientTF.setText("");//clears the client name area
					}

				}

				lClientTF.setText("");
				lAddressTF.setText("");
				lLawnNameTF.setText("");
				lGenLocationTF.setText("");
				lIntervalTF.setText("");
				lPriceTF.setText("");

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

				if(addClntBtn.getText().equals("Update Client") || addClntBtn.getText().equals("Update Lawn")) {

					centerPane.getChildren().clear();
					addClntLwnLbl.getChildren().clear();
					addClntLwnLbl.getChildren().addAll(cNameLbl, cBiAdLbl);
					displayInfo.getChildren().clear();
					displayInfo.getChildren().addAll(cName, cAddr);
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
				addClntLwnLbl.getChildren().addAll(cNameLbl, cBiAdLbl);
				addClntLwnTF.getChildren().clear();
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

				comboBox.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {

						tempLwn = tempClnt.getLawnFromAddress(comboBox.getValue());
						centerPane.getChildren().clear();
						btnPane.getChildren().clear();
						addLwnBtn.setText("Update Lawn");
						lClientTF.setText(tempLwn.getClient().getName());
						lAddressTF.setText(tempLwn.getAddress().toString());
						lLawnNameTF.setText(tempLwn.getLawnName().toString());
						lGenLocationTF.setText(tempLwn.getGenLocation().toString());
						lIntervalTF.setText("" + tempLwn.getInterval());
						lPriceTF.setText("" + tempLwn.getPrice());
						btnPane.getChildren().addAll(addLwnBtn, cnclAddBtn);
						addClntLwnLbl.getChildren().clear();
						addClntLwnLbl.getChildren().addAll(lClientLbl, lAddressLbl, lLawnNameLbl, lGenLocationLbl, lIntervalLbl, lPriceLbl);
						addClntLwnTF.getChildren().clear();
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

		sAddBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				TextInputDialog dialog = new TextInputDialog();
				dialog.setTitle("Add Email");
				dialog.setHeaderText("Add an email to the mailing list");
				dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setVisible(false);
				dialog.initStyle(StageStyle.UNDECORATED);

				Optional<String> resultAddress = dialog.showAndWait();
				if(!resultAddress.get().equals("")) {
					io.emailList.add(resultAddress.get());
					settingsItems.getChildren().remove(3);
					emailComboBox.setItems(FXCollections.observableArrayList(io.emailList));
					settingsItems.getChildren().add(3,emailComboBox);
					System.out.println("Your bAddress: " + resultAddress.get());
				}
				

			}//end handle

		});//end setonaction saddbtn
		
		sDelBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				if(!emailComboBox.getSelectionModel().getSelectedItem().equals(null)) {
					
					io.emailList.remove(io.emailList.indexOf(emailComboBox.getSelectionModel().getSelectedItem()));
					settingsItems.getChildren().remove(3);
					emailComboBox.setItems(FXCollections.observableArrayList(io.emailList));
					settingsItems.getChildren().add(3,emailComboBox);
					
				}
				
			}//end handle
			
		});//end setonaction sdelbtn

		sUpdateBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(!sCompanyNameTF.getText().equals("")) {
					io.companyName = sCompanyNameTF.getText();
				}

			}//end handle

		});//end setonaction sSubmitbtn
		
		bSendBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				
				
			}//end handle
			
		});//end setonaction bSendbtn
		
		backupTitleLbl.setFont(new Font(30));

		topPane.getChildren().add(menuBar);

		searchBox.setSpacing(10);
		searchBox.setPadding(new Insets(10, 10, 10, 10));
		searchBox.setAlignment(Pos.CENTER);
		searchBox.getChildren().addAll(searchLabel, searchTextField);

		addItemsBox.setSpacing(10);
		addItemsBox.setPadding(new Insets(10, 10, 10, 10));
		addItemsBox.setAlignment(Pos.CENTER);
		addItemsBox.getChildren().addAll(clntPageBtn, lwnPageBtn);

		centerPane.setSpacing(10);
		centerPane.setPadding(new Insets(20, 20, 20, 20));
		centerPane.setAlignment(Pos.CENTER);

		btnPane.setSpacing(10);
		btnPane.setPadding(new Insets(0, 10, 10, 10));
		btnPane.setAlignment(Pos.CENTER);

		settingsBtnPane.setSpacing(10);
		settingsBtnPane.setPadding(new Insets(0, 10, 10, 10));
		settingsBtnPane.setAlignment(Pos.CENTER);
		settingsBtnPane.getChildren().addAll(sAddBtn, sDelBtn);
		
		iAddressBox.setSpacing(20);
		iAddressBox.setAlignment(Pos.CENTER);
		
		iCostIntervalBox.setSpacing(20);
		iCostIntervalBox.setAlignment(Pos.CENTER);

		settingsTFPane.setSpacing(10);
		settingsTFPane.setAlignment(Pos.CENTER);
		settingsTFPane.getChildren().addAll(sCompanyNameTF, sUpdateBtn);

		rightPane.setSpacing(10);
		rightPane.setPadding(new Insets(20, 20, 20, 20));
		rightPane.getChildren().addAll(searchBox, listView, addItemsBox);
		
		leftPane.setSpacing(10);
		leftPane.setPadding(new Insets(20, 20, 20, 20));
		leftPane.setAlignment(Pos.CENTER);

		addClntLwnLbl.setSpacing(19);
		addClntLwnLbl.setPadding(new Insets(0,0,48,0));
		addClntLwnLbl.setAlignment(Pos.CENTER_RIGHT);

		addClntLwnTF.setSpacing(11);
		addClntLwnTF.setPadding(new Insets(20,20,20,2));
		addClntLwnTF.setAlignment(Pos.CENTER);

		displayInfo.setSpacing(19);
		displayInfo.setPadding(new Insets(0,0,48,0));
		displayInfo.setAlignment(Pos.CENTER);

		sidePanelBtn.setSpacing(10);
		sidePanelBtn.setPadding(new Insets(20, 20, 20, 20));
		sidePanelBtn.setAlignment(Pos.CENTER);

		settingsLbl.setSpacing(18);
		settingsLbl.setPadding(new Insets(20, 20, 22, 20));
		settingsLbl.setAlignment(Pos.CENTER_RIGHT);
		settingsLbl.getChildren().addAll(sCompanyNameLbl, sAutoBackupLbl, sDisableServerLbl, sEditEmailsLbl);

		settingsItems.setSpacing(12);
		settingsItems.setPadding(new Insets(45, 20, 0, 20));
		settingsItems.setAlignment(Pos.CENTER);

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
	
	public void populateSortedLawnTA(TextArea ta) {
		
		for(int i = 0; i < io.lawnList.size(); i++) {
			
			ta.insertText(i, "-------------------------------------------------\n" +
					"Lawn Name:\t" + io.lawnList.get(i).getLawnName() + "\n" +
					"Address:\t\t" + io.lawnList.get(i).getAddress() + "\n" +
					"Last Mow:\t" + io.lawnList.get(i).getLastMow() + "\n" +
					"Next Mow:\t" + io.lawnList.get(i).getNextMow() + "\n");
			
			
		}
		
	}//end populateSortedLawnTA

	public ListView<String> populateList(ListView<String> listView, String[] s) {

		listView.getItems().clear();
		listView.getItems().addAll(s);
		return listView;

	}//end populateList

	public String[] search(TextField search, String[] list) {

		ArrayList<String> temp = new ArrayList<>();

		for(int i = 0; i < list.length; i++)
			if(list[i].startsWith(search.getText()))
				temp.add(list[i]);

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