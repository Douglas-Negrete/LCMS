package GUI;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;
import List.Client;
import List.FileIO;
import List.Lawn;
import Mail.Mailer;
import Web.WebMain;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Pair;

/*
 * This class contains all elements for the GUI. The idea is to have all elements of the graphical portion contained here, while
 * not including any of the logic needed to populate each field 
 * 
 */

public class GUI extends Application {

	public FileIO io;
	int shown = 0;// 0 == client, 1 == lawn, 2 == checkedLawn
	Client tempClnt;
	Lawn tempLwn;
	SimpleDateFormat sf = new SimpleDateFormat("MM-dd-yyyy");
	NumberFormat df = new DecimalFormat("#0.00");
	public String ip;
	private String textFieldsColor = "e9f7e8";

	public GUI() {

		io = new FileIO();

	}//constructor

	@Override
	public void start(Stage primaryStage) throws Exception {

		if(io.isNew()) {

			Alert firstStartAlert = new Alert(AlertType.CONFIRMATION);
			firstStartAlert.setTitle("First launch of program");
			firstStartAlert.setHeaderText("Do you have a backup file, or are you starting for the first time?");

			ButtonType fromBackupBtn = new ButtonType("Backup from file");
			ButtonType newProgramBtn = new ButtonType("New program");

			firstStartAlert.getButtonTypes().setAll(fromBackupBtn, newProgramBtn);

			Optional<ButtonType> result = firstStartAlert.showAndWait();

			if (result.get() == fromBackupBtn){
				// ... user chose first option

				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Choose Backup File");
				fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("LCMS Files", "*.lcms"));

				File selectedFile = fileChooser.showOpenDialog(primaryStage);
				if (selectedFile != null) {

					io.setBackupFile(selectedFile);
					io.setBackupFileLocation(selectedFile.getAbsolutePath());
					//io.readInBackupFile();
					io.populateLawns();
					io.setEmailData();

				}
				else {

					Alert fileErrorAlert = new Alert(AlertType.ERROR);
					fileErrorAlert.setTitle("Program Start Error");
					fileErrorAlert.setHeaderText("No file selected, please restart program");
					fileErrorAlert.showAndWait();
					System.exit(0);

				}

			} else {
				// ... user chose second option
				File temp;
				temp = new File("BackupFile.lcms");
				io.setBackupFile(temp);
				io.initializeLastBackup();
				io.setBackupFileLocation(temp.getAbsolutePath());
				io.addClient(new Client("Example Client", "123 Example Billing Address Ave","temp"));

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

						confirmEmail.setContentText(resultEmail.get());

						Optional<ButtonType> confirmEmailBtn = confirmEmail.showAndWait();
						if (confirmEmailBtn.get() == correct){

							io.setBackupEmail(resultEmail.get());
							//io.emailList.add(resultEmail.get());
							flag = false;

						} else if (confirmEmailBtn.get() == notCorrect) {

							//resultEmail = dialog.showAndWait();

						}
					}
				}//end while
			}
		}
		else {

			try {

				io.readInBackupFile();
				io.populateLawns();
				io.writeLawnsHTML();
				io.checkAutoBackup();

			}
			catch(Exception e) {

				io.resetBackup();

			}

			if(io.readServerFromFile()) {

				try {
					ip = WebMain.startServer();
				} catch (Exception e) {

					e.printStackTrace();
				}

			}
		}

		if(!io.companyName.equals(""))
			primaryStage.setTitle("Lawn Care Made Simple ("+io.companyName+")");//title
		else
			primaryStage.setTitle("Lawn Care Made Simple");

		VBox sceneVBox = new VBox();
		sceneVBox.setStyle("-fx-background-color: #aef2a9");    // #c7ffc4");
		Scene scene = new Scene(sceneVBox, 1150, 600);//window size
		primaryStage.getIcons().add(new Image("file:image/icon/lawnMower.png"));

		primaryStage.setMinHeight(600);
		primaryStage.setMinWidth(1150);

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
				emailBill = new MenuItem("Backup Bills");
		MenuItem settings = new MenuItem("Settings"),
				help = new MenuItem("Help");

		ObservableList<String> list = FXCollections.<String>observableArrayList(io.getClientNames());//the actual string list that
		//will go in the list pane

		ListView<String> listView = new ListView<>(list);//the list pane in the right pane

		Label searchLabel = new Label("Search:");//makes a label for the search bar
		Label cNameLbl  = new Label("Name:"), 
				cBiAdLbl  = new Label("Billing Address:"),
				cPhoneNumLbl = new Label("Phone Number:"),
				cOwesLbl = new Label("Owes:"),
				cName = new Label(),
				cAddr = new Label(),
				cOwes = new Label(),
				cNum = new Label();
		Label lClientLbl = new Label("Client Name:"),
				lAddressLbl = new Label("Address:"),
				lLawnNameLbl = new Label("Lawn Name:"),
				lGenLocationLbl = new Label("General Location:"),
				lIntervalLbl = new Label("Interval(Days):"),
				lPriceLbl = new Label("Price:"),
				lDateLbl = new Label("Next Mow Date:"),
				lLawnLbl = new Label("");
		Label iSortedLawnsLbl = new Label("Lawns by Next Mow Date");
		Label sCompanyNameLbl = new Label("Set Company Name:"),
				sAutoBackupLbl = new Label("Set Auto Backup (Days) :"),
				sDisableServerLbl = new Label("Disable Server:"),
				sEditEmailsLbl = new Label("Edit Emails:");
		Label backupEmailLbl = new Label("The backup will be sent to this email:"),
				backupTitleLbl = new Label(""),
				transactionsLbl = new Label("A full transaction sheet can be found at " + new File("Transactions.txt").getAbsolutePath());

		TextField searchTextField = new TextField();//makes a search bar to search the list in the right pane
		TextField cNameTF  = new TextField(),
				cBiAdTF = new TextField(),
				cPhoneNumTF = new TextField();
		TextField lClientTF = new TextField(),
				lAddressTF = new TextField(),
				lLawnNameTF = new TextField(),
				lGenLocationTF = new TextField(),
				lIntervalTF = new TextField(),
				lPriceTF = new TextField();
		TextField sCompanyNameTF = new TextField();

		DatePicker datePicker = new DatePicker();

		Spinner<Integer> spin = new Spinner<>(0,30,io.getBackupInterval(),1);

		CheckBox disableServerCheckBox = new CheckBox(" (Change will occur after program restart)");
		CheckBox lMowedCheckBox = new CheckBox("Mowed"),
				lSkipCheckBox = new CheckBox("Skip"),
				lStopMowCheckBox = new CheckBox("Stop Mowing");

		ComboBox<String> emailComboBox = new ComboBox<>();

		TextArea lawnTA = new TextArea();
		TextArea notesTA = new TextArea();
		TextArea sortedLawnTA = new TextArea();
		TextArea helpTA = new TextArea();

		Button clntPageBtn = new Button("New Client"),
				lwnPageBtn = new Button("New Lawn"),
				addClntBtn = new Button("Add Client"),
				addLwnBtn = new Button("Add Lawn");
		Button cnclAddBtn = new Button("Cancel");
		Button cAddLawnBtn = new Button("Add Lawn"),
				editClntBtn = new Button("Edit Client"),
				editLwnBtn = new Button("Edit Lawn"),
				editOwesBtn = new Button("Edit Owes"),
				delClntBtn = new Button("Delete Client"),
				delLwnBtn = new Button("Delete Lawn");
		Button lAddNoteBtn = new Button("Add Note");
		Button sAddBtn = new Button("Add"),
				sDelBtn = new Button("Delete"),
				sUpdateBtn = new Button("Update"),
				sEditBackupBtn = new Button("Edit Backup Email");
		Button bSendBtn = new Button("Send"),
				lSendBtn = new Button("Send Lawn Lists"),
				lUpdateFromHTML = new Button("Update from Server");

		HBox topPane = new HBox();//what goes in the top section of the layout
		HBox searchBox = new HBox();//contains the search label, and the search box
		HBox addItemsBox = new HBox();//for the add item buttons
		HBox centerPane = new HBox();//temp for what goes in the center section
		HBox btnPane = new HBox();//pane for the buttons to populate
		HBox settingsBtnPane = new HBox(),
				settingsTFPane = new HBox(),
				sBackupBtnPane = new HBox();
		HBox iAddressBox = new HBox(),
				iCostIntervalBox = new HBox();
		HBox serverBtn = new HBox();

		VBox rightPane = new VBox();//what goes in the right section of the layout
		VBox leftPane = new VBox();//the lawn information that goes on the left
		VBox addClntLwnLbl = new VBox(),
				addClntLwnTF = new VBox();//for add clients or lawns
		VBox displayInfo = new VBox();//to display misc information
		VBox sidePanelBtn = new VBox();
		VBox settingsLbl = new VBox();
		VBox settingsItems = new VBox();

		BorderPane border = new BorderPane();//the layout for the scene, this layout has five sections: top, left, center, right, bottom

		menuBar.getMenus().addAll(menuFile, menuView, menuBackup, menuPreferences);
		menuBar.setStyle("-fx-background-color: linear-gradient(#f0f5f0 0%, #e1ebe0 25%, #d2e1d1 75%, #c3d7c1 100%);");

		menuFile.getItems().addAll(importList, save);

		menuView.getItems().addAll(client, lawn, transactions);

		menuBackup.getItems().addAll(emailFile, emailTrans, emailBill);

		menuPreferences.getItems().addAll(settings, help);

		importList.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				File selectedFile = null;
				while(selectedFile == null) {
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("Choose Backup File");
					fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("LCMS Files", "*.lcms"));
					selectedFile = fileChooser.showOpenDialog(primaryStage);
					if (selectedFile != null) {
						try {
							io.clearLists();
							io.setBackupFile(selectedFile);
							io.setBackupFileLocation(selectedFile.getAbsolutePath());
						}
						catch(Exception e) {

						}
					}
					else
						break;
				}

			}//end handle

		});//end setonaction importList

		save.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Save Confirmation");
				alert.setHeaderText("You have selected save file");
				alert.setContentText("This does not create a new backup file, this option saves current data.");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK){
					
					io.generateBackupFile();

					alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Backup Completed");
					alert.setHeaderText("You have successfully saved!");
					alert.setContentText("You have saved the file to the location: " + io.getBackupLocation());
					alert.showAndWait();
					
				}

			}//end handle

		});//end setonaction

		client.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent t) {
				
				shown = 0;

				leftPane.getChildren().clear();
				sidePanelBtn.getChildren().clear();
				displayInfo.getChildren().clear();
				centerPane.getChildren().clear();
				addClntLwnLbl.getChildren().clear();

				rightPane.getChildren().remove(1);
				rightPane.getChildren().add(1, populateList(listView, io.getClientNames(), 0));
				listView.getFocusModel().focus(0);
				tempClnt = io.getClient(io.getClientIndex(listView.getFocusModel().getFocusedItem()));
				cName.setText(tempClnt.getName());
				cAddr.setText(tempClnt.getBillAddress());
				cOwes.setText("$" + df.format(tempClnt.getOwed()));
				cNum.setText(tempClnt.getPhoneNum());
				lawnTA.clear();
				populateLawnTA(lawnTA, cName.getText());
				searchTextField.clear();

				sidePanelBtn.getChildren().addAll(cAddLawnBtn, editClntBtn, editLwnBtn, editOwesBtn, delClntBtn, delLwnBtn);
				addClntLwnLbl.getChildren().addAll(cNameLbl, cBiAdLbl, cOwesLbl, cPhoneNumLbl);
				displayInfo.getChildren().addAll(cName, cAddr, cOwes, cNum);
				centerPane.getChildren().addAll(addClntLwnLbl, displayInfo, lawnTA);
				border.setLeft(sidePanelBtn);
				border.setCenter(centerPane);

			}//end handle

		});//end setonaction

		lawn.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent t) {

				tempClnt = null;// added this line to get the edit lawn button to work
				shown = 1;
				
				io.populateLawns();

				leftPane.getChildren().clear();
				sidePanelBtn.getChildren().clear();
				serverBtn.getChildren().clear();
				btnPane.getChildren().clear();
				centerPane.getChildren().clear();
				displayInfo.getChildren().clear();

				sortedLawnTA.clear();
				populateSortedLawnTA(sortedLawnTA);
				sortedLawnTA.positionCaret(1);
				rightPane.getChildren().remove(1);
				rightPane.getChildren().add(1, populateList(listView, io.getLawnNames(), 1));
				listView.getFocusModel().focus(0);
				tempLwn = io.lawnList.get(listView.getFocusModel().getFocusedIndex());
				notesTA.clear();
				notesTA.appendText("Name:\t\t\t" + tempLwn.getLawnName() + "\n");
				notesTA.appendText("Address:\t\t\t" + tempLwn.getAddress() + "\n");
				notesTA.appendText("General Location:\t" + tempLwn.getGenLocation() + "\n");
				notesTA.appendText("Client:\t\t\t" + tempLwn.getClient().getName() + "\n");
				notesTA.appendText("Last Mowed:\t\t" + tempLwn.sf.format(tempLwn.getLastMow()) + "\n");
				notesTA.appendText("Next Mow:\t\t" + tempLwn.sf.format(tempLwn.getNextMow()) + "\n");
				notesTA.appendText("Cost:\t\t\t\t" + df.format(tempLwn.getPrice()) + "\n");
				notesTA.appendText("Interval:\t\t\t" + tempLwn.getInterval() + "\n");
				notesTA.appendText("------------------------------------------------------\n");
				notesTA.appendText("Notes:\n" + tempLwn.getNotes());
				searchTextField.clear();
				if(tempLwn.sf.format(tempLwn.getLastMow()).equals(tempLwn.sf.format(Calendar.getInstance().getTime())))
					lMowedCheckBox.setSelected(true);
				else
					lMowedCheckBox.setSelected(false);
				if(tempLwn.getSkip())
					lSkipCheckBox.setSelected(true);
				else
					lSkipCheckBox.setSelected(false);
				if(tempLwn.getNextMow().compareTo(java.sql.Date.valueOf("2000-01-01")) == 0)
					lStopMowCheckBox.setSelected(true);
				else
					lStopMowCheckBox.setSelected(false);
				
				leftPane.getChildren().addAll(iSortedLawnsLbl, sortedLawnTA, serverBtn);
				if(io.getServer())
					serverBtn.getChildren().addAll(lSendBtn, lUpdateFromHTML);
				else
					serverBtn.getChildren().add(lSendBtn);
				btnPane.getChildren().addAll(lMowedCheckBox, lSkipCheckBox, lStopMowCheckBox, editLwnBtn, lAddNoteBtn);
				displayInfo.getChildren().addAll(notesTA, btnPane);
				border.setLeft(leftPane);
				border.setCenter(displayInfo);

			}//end handle

		});//end setonaction

		transactions.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent t) {

				sidePanelBtn.getChildren().clear();
				leftPane.getChildren().clear();
				displayInfo.getChildren().clear();
				
				backupTitleLbl.setText("Transactions");
				lawnTA.clear();
				lawnTA.autosize();
				io.printTransactionFileTA(lawnTA);
				lawnTA.positionCaret(1);
				
				displayInfo.getChildren().addAll(backupTitleLbl, transactionsLbl, lawnTA);
				border.setCenter(displayInfo);

			}//end handle

		});//end setonaction

		emailFile.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				leftPane.getChildren().clear();
				sidePanelBtn.getChildren().clear();
				centerPane.getChildren().clear();
				displayInfo.getChildren().clear();
				
				backupEmailLbl.setText("The backup will be sent to this email: ");
				ObservableList<String> options = FXCollections.observableArrayList(io.emailList);
				emailComboBox.setItems(options);
				backupTitleLbl.setText("Backup File");
				lawnTA.clear();
				lawnTA.autosize();
				io.generateBackupFile();
				io.printBackupFileTA(lawnTA);
				lawnTA.positionCaret(1);
				lawnTA.autosize();
				
				centerPane.getChildren().addAll(backupEmailLbl, emailComboBox, bSendBtn);
				displayInfo.getChildren().addAll(backupTitleLbl, centerPane, lawnTA);
				border.setCenter(displayInfo);

			}//end handle

		});//end setonaction emailFile

		emailTrans.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				sidePanelBtn.getChildren().clear();
				leftPane.getChildren().clear();
				centerPane.getChildren().clear();
				displayInfo.getChildren().clear();

				backupEmailLbl.setText("The backup will be sent to this email: ");
				ObservableList<String> options = FXCollections.observableArrayList(io.emailList);
				emailComboBox.setItems(options);
				backupTitleLbl.setText("Backup Transactions");
				lawnTA.clear();
				lawnTA.autosize();
				io.printTransactionFileTA(lawnTA);
				lawnTA.positionCaret(1);
				
				centerPane.getChildren().addAll(backupEmailLbl, emailComboBox, bSendBtn);
				displayInfo.getChildren().addAll(backupTitleLbl, centerPane, lawnTA);
				border.setCenter(displayInfo);

			}//end handle

		});//end setonaction emailttrans

		emailBill.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				io.createBillFile();

				leftPane.getChildren().clear();
				sidePanelBtn.getChildren().clear();
				displayInfo.getChildren().clear();
				centerPane.getChildren().clear();
				
				backupEmailLbl.setText("The backup will be sent to this email: ");
				ObservableList<String> options = FXCollections.observableArrayList(io.emailList);
				emailComboBox.setItems(options);
				backupTitleLbl.setText("Backup Bills");
				lawnTA.clear();
				lawnTA.autosize();
				io.printBillFileTA(lawnTA);
				lawnTA.positionCaret(1);
				
				centerPane.getChildren().addAll(backupEmailLbl, emailComboBox, bSendBtn);
				displayInfo.getChildren().addAll(backupTitleLbl, centerPane, lawnTA);
				border.setCenter(displayInfo);

			}//end handle

		});//end setonaction emailbill

		settings.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				leftPane.getChildren().clear();
				sidePanelBtn.getChildren().clear();
				settingsItems.getChildren().clear();
				btnPane.getChildren().clear();
				displayInfo.getChildren().clear();
				centerPane.getChildren().clear();

				border.requestFocus();

				ObservableList<String> options = FXCollections.observableArrayList(io.emailList);
				emailComboBox.setItems(options);
				sCompanyNameTF.setPromptText(io.companyName);
				if(io.getServer())
					disableServerCheckBox.setSelected(false);
				else
					disableServerCheckBox.setSelected(true);
				
				settingsItems.getChildren().addAll(settingsTFPane, sBackupBtnPane, disableServerCheckBox, emailComboBox, settingsBtnPane);
				settingsItems.setAlignment(Pos.CENTER_LEFT);
				centerPane.getChildren().addAll(settingsLbl, settingsItems);
				border.setCenter(centerPane);

			}//end handle

		});//end setonaction settings

		help.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				leftPane.getChildren().clear();
				sidePanelBtn.getChildren().clear();
				displayInfo.getChildren().clear();
				centerPane.getChildren().clear();

				backupTitleLbl.setText("FAQ");
				helpTA.clear();
				io.printFAQFileTA(helpTA);
				helpTA.positionCaret(1);
				
				displayInfo.getChildren().addAll(backupTitleLbl, helpTA);
				centerPane.getChildren().add(displayInfo);
				border.setCenter(centerPane);

			}//end handle

		});//end setonaction help

		listView.setStyle("-fx-control-inner-background:#" + textFieldsColor);
		listView.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				
				displayInfo.getChildren().clear();

				if(shown == 0) {

					sidePanelBtn.getChildren().clear();
					addClntLwnLbl.getChildren().clear();
					centerPane.getChildren().clear();

					tempClnt = io.getClient(io.getClientIndex(listView.getFocusModel().getFocusedItem()));
					cName.setText(tempClnt.getName());
					cAddr.setText(tempClnt.getBillAddress());
					cOwes.setText("$" + df.format(tempClnt.getOwed()));
					cNum.setText(tempClnt.getPhoneNum());
					lawnTA.clear();
					populateLawnTA(lawnTA, cName.getText());

					sidePanelBtn.getChildren().addAll(cAddLawnBtn, editClntBtn, editLwnBtn, editOwesBtn, delClntBtn, delLwnBtn);
					addClntLwnLbl.getChildren().addAll(cNameLbl, cBiAdLbl, cOwesLbl, cPhoneNumLbl);
					displayInfo.getChildren().addAll(cName, cAddr, cOwes, cNum);
					centerPane.getChildren().addAll(addClntLwnLbl, displayInfo, lawnTA);
					border.setLeft(sidePanelBtn);
					border.setCenter(centerPane);

				}
				else if(shown == 1) {

					leftPane.getChildren().clear();
					serverBtn.getChildren().clear();
					btnPane.getChildren().clear();
					
					String temp[] = listView.getFocusModel().getFocusedItem().split(";");
					tempLwn = io.lawnList.get(io.getFromLawnName(temp[temp.length-1].trim()));
					notesTA.clear();
					notesTA.appendText("Name:\t\t\t" + tempLwn.getLawnName() + "\n");
					notesTA.appendText("Address:\t\t\t" + tempLwn.getAddress() + "\n");
					notesTA.appendText("General Location:\t" + tempLwn.getGenLocation() + "\n");
					notesTA.appendText("Client:\t\t\t" + tempLwn.getClient().getName() + "\n");
					notesTA.appendText("Last Mowed:\t\t" + tempLwn.sf.format(tempLwn.getLastMow()) + "\n");
					notesTA.appendText("Next Mow:\t\t" + tempLwn.sf.format(tempLwn.getNextMow()) + "\n");
					notesTA.appendText("Cost:\t\t\t\t" + df.format(tempLwn.getPrice()) + "\n");
					notesTA.appendText("Interval:\t\t\t" + tempLwn.getInterval() + "\n");
					notesTA.appendText("------------------------------------------------------\n");
					notesTA.appendText("Notes:\n" + tempLwn.getNotes());
					if(tempLwn.sf.format(tempLwn.getLastMow()).equals(tempLwn.sf.format(Calendar.getInstance().getTime())))
						lMowedCheckBox.setSelected(true);
					else
						lMowedCheckBox.setSelected(false);
					if(tempLwn.getSkip())
						lSkipCheckBox.setSelected(true);
					else
						lSkipCheckBox.setSelected(false);
					if(tempLwn.getNextMow().compareTo(java.sql.Date.valueOf("2000-01-01")) == 0)
						lStopMowCheckBox.setSelected(true);
					else
						lStopMowCheckBox.setSelected(false);
					
					leftPane.getChildren().addAll(iSortedLawnsLbl, sortedLawnTA, serverBtn);
					if(io.getServer())
						serverBtn.getChildren().addAll(lSendBtn, lUpdateFromHTML);
					else
						serverBtn.getChildren().add(lSendBtn);
					displayInfo.getChildren().addAll(notesTA, btnPane);
					btnPane.getChildren().addAll(lMowedCheckBox, lSkipCheckBox, lStopMowCheckBox, editLwnBtn, lAddNoteBtn);
					border.setLeft(leftPane);
					border.setCenter(displayInfo);

				}

			}//end handle

		});//end setonmouseclicked

		listView.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				
				displayInfo.getChildren().clear();

				if(shown == 0) {

					sidePanelBtn.getChildren().clear();
					addClntLwnLbl.getChildren().clear();
					centerPane.getChildren().clear();

					tempClnt = io.getClient(io.getClientIndex(listView.getFocusModel().getFocusedItem()));
					cName.setText(tempClnt.getName());
					cAddr.setText(tempClnt.getBillAddress());
					cOwes.setText("$" + df.format(tempClnt.getOwed()));
					cNum.setText(tempClnt.getPhoneNum());
					lawnTA.clear();
					populateLawnTA(lawnTA, cName.getText());

					sidePanelBtn.getChildren().addAll(cAddLawnBtn, editClntBtn, editLwnBtn, editOwesBtn, delClntBtn, delLwnBtn);
					addClntLwnLbl.getChildren().addAll(cNameLbl, cBiAdLbl, cOwesLbl, cPhoneNumLbl);
					displayInfo.getChildren().addAll(cName, cAddr, cOwes, cNum);
					centerPane.getChildren().addAll(addClntLwnLbl, displayInfo, lawnTA);
					border.setLeft(sidePanelBtn);
					border.setCenter(centerPane);

				}
				else if(shown == 1) {

					leftPane.getChildren().clear();
					serverBtn.getChildren().clear();
					btnPane.getChildren().clear();
					
					String temp[] = listView.getFocusModel().getFocusedItem().split(";");
					tempLwn = io.lawnList.get(io.getFromLawnName(temp[temp.length-1].trim()));
					notesTA.clear();
					notesTA.appendText("Name:\t\t\t" + tempLwn.getLawnName() + "\n");
					notesTA.appendText("Address:\t\t\t" + tempLwn.getAddress() + "\n");
					notesTA.appendText("General Location:\t" + tempLwn.getGenLocation() + "\n");
					notesTA.appendText("Client:\t\t\t" + tempLwn.getClient().getName() + "\n");
					notesTA.appendText("Last Mowed:\t\t" + tempLwn.sf.format(tempLwn.getLastMow()) + "\n");
					notesTA.appendText("Next Mow:\t\t" + tempLwn.sf.format(tempLwn.getNextMow()) + "\n");
					notesTA.appendText("Cost:\t\t\t\t" + df.format(tempLwn.getPrice()) + "\n");
					notesTA.appendText("Interval:\t\t\t" + tempLwn.getInterval() + "\n");
					notesTA.appendText("------------------------------------------------------\n");
					notesTA.appendText("Notes:\n" + tempLwn.getNotes());
					if(tempLwn.sf.format(tempLwn.getLastMow()).equals(tempLwn.sf.format(Calendar.getInstance().getTime())))
						lMowedCheckBox.setSelected(true);
					else
						lMowedCheckBox.setSelected(false);
					if(tempLwn.getSkip())
						lSkipCheckBox.setSelected(true);
					else
						lSkipCheckBox.setSelected(false);
					if(tempLwn.getNextMow().compareTo(java.sql.Date.valueOf("2000-01-01")) == 0)
						lStopMowCheckBox.setSelected(true);
					else
						lStopMowCheckBox.setSelected(false);
					
					leftPane.getChildren().addAll(iSortedLawnsLbl, sortedLawnTA, serverBtn);
					if(io.getServer())
						serverBtn.getChildren().addAll(lSendBtn, lUpdateFromHTML);
					else
						serverBtn.getChildren().add(lSendBtn);
					displayInfo.getChildren().addAll(notesTA, btnPane);
					btnPane.getChildren().addAll(lMowedCheckBox, lSkipCheckBox, lStopMowCheckBox, editLwnBtn, lAddNoteBtn);
					border.setLeft(leftPane);
					border.setCenter(displayInfo);

				}

			}//end handle

		});//end setonkeypressed

		cAddr.setWrapText(true);

		lAddressLbl.setWrapText(true);

		iSortedLawnsLbl.setFont(new Font(20));

		searchTextField.setStyle("-fx-control-inner-background:#"+textFieldsColor);
		searchTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {//creates a keylistener on the searchbox

			@Override
			public void handle(KeyEvent event) {

				if(event.getCode().equals(KeyCode.ENTER) || event.getCode().equals(KeyCode.BACK_SPACE)) {//when the enter or delete key is pressed

					rightPane.getChildren().remove(1);
					if(shown == 0)
						rightPane.getChildren().add(1, populateList(listView, search(searchTextField, io.getClientNames()), 0));
					else if(shown == 1)
						rightPane.getChildren().add(1, populateList(listView, search(searchTextField, io.getLawnNames()), 1));
					//search the list for the name entered in the searchbox

				}

			}//end handle

		});//end setonkeypressed

		cNameTF.setStyle("-fx-control-inner-background:#"+textFieldsColor);

		cBiAdTF.setStyle("-fx-control-inner-background:#"+textFieldsColor);

		cPhoneNumTF.setStyle("-fx-control-inner-background:#"+textFieldsColor);

		lClientTF.setStyle("-fx-control-inner-background:#"+textFieldsColor);

		lAddressTF.setStyle("-fx-control-inner-background:#"+textFieldsColor);

		lLawnNameTF.setStyle("-fx-control-inner-background:#"+textFieldsColor);

		lGenLocationTF.setStyle("-fx-control-inner-background:#"+textFieldsColor);

		lIntervalTF.setStyle("-fx-control-inner-background:#"+textFieldsColor);

		lPriceTF.setStyle("-fx-control-inner-background:#"+textFieldsColor);

		sCompanyNameTF.setStyle("-fx-control-inner-background:#"+textFieldsColor);

		datePicker.setStyle("-fx-control-inner-background:#"+textFieldsColor);

		spin.setStyle("-fx-control-inner-background:#"+textFieldsColor);
		spin.valueProperty().addListener(new ChangeListener<Integer>() {

			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {

				io.setBackupInterval(newValue);

			}//end changed
			
		});//end addlistener

		disableServerCheckBox.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(disableServerCheckBox.isSelected())
					io.setServer(false);
				else
					io.setServer(true);

			}//end handle

		});//end setonaction

		lMowedCheckBox.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				btnPane.getChildren().clear();
				displayInfo.getChildren().clear();

				if(lMowedCheckBox.isSelected()) {
					io.appendToTransactionFile("Lawn checked off: " + tempLwn.toTransaction());
					tempLwn.checkLawnOff();
					io.appendToTransactionFile("Client " + tempLwn.getClient().getName() + " now owes: " + tempLwn.getClient().getOwes());
				}
				else {
					io.appendToTransactionFile("Lawn unchecked: " + tempLwn.toTransaction());
					tempLwn.unCheckLawnOff();
					io.appendToTransactionFile("Client " + tempLwn.getClient().getName() + " now owes: " + tempLwn.getClient().getOwes());
				}

				notesTA.clear();
				notesTA.appendText("Name:\t\t\t" + tempLwn.getLawnName() + "\n");
				notesTA.appendText("Address:\t\t\t" + tempLwn.getAddress() + "\n");
				notesTA.appendText("General Location:\t" + tempLwn.getGenLocation() + "\n");
				notesTA.appendText("Client:\t\t\t" + tempLwn.getClient().getName() + "\n");
				notesTA.appendText("Last Mowed:\t\t" + tempLwn.sf.format(tempLwn.getLastMow()) + "\n");
				notesTA.appendText("Next Mow:\t\t" + tempLwn.sf.format(tempLwn.getNextMow()) + "\n");
				notesTA.appendText("Cost:\t\t\t\t" + df.format(tempLwn.getPrice()) + "\n");
				notesTA.appendText("Interval:\t\t\t" + tempLwn.getInterval() + "\n");
				notesTA.appendText("------------------------------------------------------\n");
				notesTA.appendText("Notes:\n" + tempLwn.getNotes());
				btnPane.getChildren().addAll(lMowedCheckBox, lSkipCheckBox, lStopMowCheckBox, editLwnBtn, lAddNoteBtn);
				displayInfo.getChildren().addAll(notesTA, btnPane);

			}//end handle

		});//end setonaction lmowedcheckbox

		lSkipCheckBox.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				btnPane.getChildren().clear();
				displayInfo.getChildren().clear();
				
				if(lSkipCheckBox.isSelected()) {
					tempLwn.skipLawn();
					tempLwn.setSkip(true);
				}
				else {
					tempLwn.unSkipLawn();
					tempLwn.setSkip(false);
				}

				notesTA.clear();
				notesTA.appendText("Name:\t\t\t" + tempLwn.getLawnName() + "\n");
				notesTA.appendText("Address:\t\t\t" + tempLwn.getAddress() + "\n");
				notesTA.appendText("General Location:\t" + tempLwn.getGenLocation() + "\n");
				notesTA.appendText("Client:\t\t\t" + tempLwn.getClient().getName() + "\n");
				notesTA.appendText("Last Mowed:\t\t" + tempLwn.sf.format(tempLwn.getLastMow()) + "\n");
				notesTA.appendText("Next Mow:\t\t" + tempLwn.sf.format(tempLwn.getNextMow()) + "\n");
				notesTA.appendText("Cost:\t\t\t\t" + df.format(tempLwn.getPrice()) + "\n");
				notesTA.appendText("Interval:\t\t\t" + tempLwn.getInterval() + "\n");
				notesTA.appendText("------------------------------------------------------\n");
				notesTA.appendText("Notes:\n" + tempLwn.getNotes());
				btnPane.getChildren().addAll(lMowedCheckBox, lSkipCheckBox, lStopMowCheckBox, editLwnBtn, lAddNoteBtn);
				displayInfo.getChildren().addAll(notesTA, btnPane);

			}//end handle

		});//end setonaction

		lStopMowCheckBox.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				btnPane.getChildren().clear();
				displayInfo.getChildren().clear();

				if(lStopMowCheckBox.isSelected())
					tempLwn.stopLawn();
				else
					tempLwn.unStopLawn();

				notesTA.clear();
				notesTA.appendText("Name:\t\t\t" + tempLwn.getLawnName() + "\n");
				notesTA.appendText("Address:\t\t\t" + tempLwn.getAddress() + "\n");
				notesTA.appendText("General Location:\t" + tempLwn.getGenLocation() + "\n");
				notesTA.appendText("Client:\t\t\t" + tempLwn.getClient().getName() + "\n");
				notesTA.appendText("Last Mowed:\t\t" + tempLwn.sf.format(tempLwn.getLastMow()) + "\n");
				notesTA.appendText("Next Mow:\t\t" + tempLwn.sf.format(tempLwn.getNextMow()) + "\n");
				notesTA.appendText("Cost:\t\t\t\t" + df.format(tempLwn.getPrice()) + "\n");
				notesTA.appendText("Interval:\t\t\t" + tempLwn.getInterval() + "\n");
				notesTA.appendText("------------------------------------------------------\n");
				notesTA.appendText("Notes:\n" + tempLwn.getNotes());
				btnPane.getChildren().addAll(lMowedCheckBox, lSkipCheckBox, lStopMowCheckBox, editLwnBtn, lAddNoteBtn);
				displayInfo.getChildren().addAll(notesTA, btnPane);

			}//end handle

		});//end setonaction stopmowcheckbox

		lawnTA.setEditable(false);
		lawnTA.setWrapText(true);
		lawnTA.setMinWidth(325);
		lawnTA.setMaxWidth(440);
		lawnTA.setMinHeight(400);
		lawnTA.setMaxHeight(500);
		lawnTA.setStyle("-fx-control-inner-background:#"+textFieldsColor);

		notesTA.setWrapText(true);
		notesTA.setEditable(false);
		notesTA.setMaxWidth(325);
		notesTA.setMinHeight(400);
		notesTA.setStyle("-fx-control-inner-background:#"+textFieldsColor);

		sortedLawnTA.setEditable(false);
		sortedLawnTA.setMinWidth(250);
		sortedLawnTA.setMaxWidth(300);
		sortedLawnTA.setMinHeight(400);
		sortedLawnTA.setMaxHeight(500);
		sortedLawnTA.setStyle("-fx-control-inner-background:#"+textFieldsColor);

		helpTA.setWrapText(true);
		helpTA.setEditable(false);
		helpTA.setMinHeight(440);
		helpTA.setMinWidth(600);
		helpTA.setStyle("-fx-control-inner-background:#"+textFieldsColor);

		clntPageBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				sidePanelBtn.getChildren().clear();
				leftPane.getChildren().clear();
				addClntLwnLbl.getChildren().clear();
				addClntLwnTF.getChildren().clear();
				btnPane.getChildren().clear();
				centerPane.getChildren().clear();
				
				cNameTF.setText("");
				cBiAdTF.setText("");
				cPhoneNumTF.setText("");
				addClntBtn.setText("Add Client");
				addClntLwnLbl.getChildren().addAll(cNameLbl, cBiAdLbl, cPhoneNumLbl);
				addClntLwnTF.getChildren().addAll(cNameTF, cBiAdTF, cPhoneNumTF, btnPane);
				btnPane.getChildren().addAll(addClntBtn, cnclAddBtn);
				centerPane.getChildren().addAll(addClntLwnLbl, addClntLwnTF);
				border.setCenter(centerPane);

			}//end handle

		});//end setOnAction

		lwnPageBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				sidePanelBtn.getChildren().clear();
				leftPane.getChildren().clear();
				addClntLwnLbl.getChildren().clear();
				addClntLwnTF.getChildren().clear();
				btnPane.getChildren().clear();
				centerPane.getChildren().clear();
				
				addLwnBtn.setText("Add Lawn");
				lClientTF.setPromptText("");
				lClientTF.setText("");
				lAddressTF.setText("");
				lLawnNameTF.setText("");
				lGenLocationTF.setText("");
				lIntervalTF.setText("");
				lPriceTF.setText("");
				datePicker.setValue(Calendar.getInstance().getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
				addClntLwnLbl.getChildren().addAll(lClientLbl, lAddressLbl, lLawnNameLbl, lGenLocationLbl, lIntervalLbl, lPriceLbl, lDateLbl);
				addClntLwnTF.getChildren().addAll(lClientTF, lAddressTF, lLawnNameTF, lGenLocationTF, lIntervalTF, lPriceTF, datePicker, btnPane);
				btnPane.getChildren().addAll(addLwnBtn, cnclAddBtn);
				centerPane.getChildren().addAll(addClntLwnLbl, addClntLwnTF);
				border.setCenter(centerPane);

			}//end handle

		});//end setonaction

		addClntBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(!cNameTF.getText().equals("") && !cBiAdTF.getText().equals("") && !cPhoneNumTF.getText().equals("")) {

					sidePanelBtn.getChildren().clear();
					addClntLwnLbl.getChildren().clear();
					addClntLwnTF.getChildren().clear();
					centerPane.getChildren().clear();

					if(addClntBtn.getText().equals("Update Client")) {
						io.getClient(io.getClientIndex(tempClnt.getName())).setName(cNameTF.getText());
						io.getClient(io.getClientIndex(tempClnt.getName())).setBillAddress(cBiAdTF.getText());
						io.getClient(io.getClientIndex(tempClnt.getName())).setPhoneNum(cPhoneNumTF.getText());
					}
					else
						io.addClient(new Client(cNameTF.getText(), cBiAdTF.getText(), cPhoneNumTF.getText()));

					rightPane.getChildren().remove(1);
					if(shown == 0)
						rightPane.getChildren().add(1, populateList(listView, io.getClientNames(), 0));
					else if(shown == 1)
						rightPane.getChildren().add(1, populateList(listView, io.getLawnNames(), 1));

					cNameTF.setText("");
					cBiAdTF.setText("");
					cPhoneNumTF.setText("");

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

				try {

					if(!addLwnBtn.getText().equals("Update Lawn")) {//if it isn't an update lawn, so it is an add lawn

						int i = io.getClientIndex(lClientTF.getText());//checks to see if the client is in the list

						if(i != -1) {//if the client exists

							if(!lAddressTF.getText().equals("") && !lLawnNameTF.getText().equals("") && !lGenLocationTF.getText().equals("") && 
									!lIntervalTF.getText().equals("") && !lPriceTF.getText().equals("")) {//are all of the fields filled in

								io.addLawn(i, new Lawn(io.getClient(i), lAddressTF.getText(), lLawnNameTF.getText(),
										lGenLocationTF.getText(), Integer.parseInt(lIntervalTF.getText()),
										Double.parseDouble(lPriceTF.getText())));

								io.getLawn(lAddressTF.getText()).setNextMow(java.sql.Date.valueOf(datePicker.getValue()));

								rightPane.getChildren().remove(1);
								if(shown == 0)
									rightPane.getChildren().add(1, populateList(listView, io.getClientNames(), 0));
								else if(shown == 1)
									rightPane.getChildren().add(1, populateList(listView, io.getLawnNames(), 1));

								addClntLwnLbl.getChildren().clear();
								addClntLwnTF.getChildren().clear();
								sidePanelBtn.getChildren().clear();
								centerPane.getChildren().clear();

								lClientTF.setText("");
								lClientTF.setPromptText("");
								lAddressTF.setText("");
								lLawnNameTF.setText("");
								lGenLocationTF.setText("");
								lIntervalTF.setText("");
								lPriceTF.setText("");

							}
							else {

								Alert alert = new Alert(AlertType.INFORMATION);//creates a dialog box warning the user that the lawn had an error
								alert.setTitle("Lawn Creation Error");
								alert.setHeaderText(null);
								alert.setContentText("All fields must be filled in!");
								alert.showAndWait();

							}

						}
						else if(io.getClientIndex(lClientTF.getPromptText()) != -1) {

							i = io.getClientIndex(lClientTF.getPromptText());//checks to see if the client is in the list

							if(!lAddressTF.getText().equals("") && !lLawnNameTF.getText().equals("") && !lGenLocationTF.getText().equals("") && 
									!lIntervalTF.getText().equals("") && !lPriceTF.getText().equals("")) {

								io.addLawn(i, new Lawn(io.getClient(i), lAddressTF.getText(), lLawnNameTF.getText(),
										lGenLocationTF.getText(), Integer.parseInt(lIntervalTF.getText()),
										Double.parseDouble(lPriceTF.getText())));

								io.getLawn(lAddressTF.getText()).setNextMow(java.sql.Date.valueOf(datePicker.getValue()));

								WebMain.serverRestart();

								rightPane.getChildren().remove(1);
								if(shown == 0)
									rightPane.getChildren().add(1, populateList(listView, io.getClientNames(), 0));
								else if(shown == 1)
									rightPane.getChildren().add(1, populateList(listView, io.getLawnNames(), 1));

								addClntLwnLbl.getChildren().clear();
								addClntLwnTF.getChildren().clear();
								sidePanelBtn.getChildren().clear();
								centerPane.getChildren().clear();

								lClientTF.setText("");
								lClientTF.setPromptText("");
								lAddressTF.setText("");
								lLawnNameTF.setText("");
								lGenLocationTF.setText("");
								lIntervalTF.setText("");
								lPriceTF.setText("");

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

								// Create the custom dialog.
								Dialog<Pair<String, String>> dialog = new Dialog<>();
								dialog.setTitle("Create Client");
								dialog.setHeaderText("Please enter a Billing Address, and a Phone Number");

								// Set the button types.
								ButtonType createButtonType = new ButtonType("Create", ButtonData.OK_DONE);
								dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

								// Create the username and password labels and fields.
								GridPane grid = new GridPane();
								grid.setHgap(10);
								grid.setVgap(10);
								grid.setPadding(new Insets(20, 150, 10, 10));

								TextField billingAddress = new TextField();
								TextField phoneNumber = new TextField();

								grid.add(new Label("Billing Address:"), 0, 0);
								grid.add(billingAddress, 1, 0);
								grid.add(new Label("Phone Number:"), 0, 1);
								grid.add(phoneNumber, 1, 1);

								dialog.getDialogPane().setContent(grid);

								// Request focus on the username field by default.
								Platform.runLater(() -> billingAddress.requestFocus());

								// Convert the result to a username-password-pair when the login button is clicked.
								dialog.setResultConverter(dialogButton -> {
									if (dialogButton == createButtonType) {
										return new Pair<>(billingAddress.getText(), phoneNumber.getText());
									}
									return null;
								});

								Optional<Pair<String, String>> result = dialog.showAndWait();

								result.ifPresent(addressPhone -> {

									addClntLwnLbl.getChildren().clear();
									addClntLwnTF.getChildren().clear();
									sidePanelBtn.getChildren().clear();
									centerPane.getChildren().clear();

									io.addClient(new Client(lClientTF.getText(), addressPhone.getKey(), addressPhone.getValue()));

									io.addLawn(io.getClientIndex(lClientTF.getText()), 
											new Lawn(io.getClient(io.getClientIndex(lClientTF.getText())), lAddressTF.getText(), 
													lLawnNameTF.getText(), lGenLocationTF.getText(), Integer.parseInt(lIntervalTF.getText()), 
													Double.parseDouble(lPriceTF.getText())));

									io.getLawn(lAddressTF.getText()).setNextMow(java.sql.Date.valueOf(datePicker.getValue()));

									WebMain.serverRestart();

									rightPane.getChildren().remove(1);
									if(shown == 0)
										rightPane.getChildren().add(1, populateList(listView, io.getClientNames(), 0));
									else if(shown == 1)
										rightPane.getChildren().add(1, populateList(listView, io.getLawnNames(), 1));

									lClientTF.setText("");
									lClientTF.setPromptText("");
									lAddressTF.setText("");
									lLawnNameTF.setText("");
									lGenLocationTF.setText("");
									lIntervalTF.setText("");
									lPriceTF.setText("");

								});

							}//end add new client
							else {

								lClientTF.setText("");//clears the client name area

							}

						}//end if the client does not exist else

					}//end if it is an add lawn
					else {//else it is an update lawn

						int i = io.getClientIndex(lClientTF.getText());//checks to see if the client is in the list

						if(i != -1) {//if the client exists

							if(!lAddressTF.getText().equals("") && !lLawnNameTF.getText().equals("") && !lGenLocationTF.getText().equals("") && 
									!lIntervalTF.getText().equals("") && !lPriceTF.getText().equals("")) {//if the text fields are not empty

								addClntLwnLbl.getChildren().clear();
								addClntLwnTF.getChildren().clear();
								sidePanelBtn.getChildren().clear();
								centerPane.getChildren().clear();

								io.getLawn(tempLwn.getAddress()).setClient(io.getClient(io.getClientIndex(lClientTF.getText())));
								io.getLawn(tempLwn.getAddress()).setAddress(lAddressTF.getText());
								io.getLawn(tempLwn.getAddress()).setLawnName(lLawnNameTF.getText());
								io.getLawn(tempLwn.getAddress()).setGenLocation(lGenLocationTF.getText());
								io.getLawn(tempLwn.getAddress()).setInterval(Integer.parseInt(lIntervalTF.getText()));
								io.getLawn(tempLwn.getAddress()).setPrice(Double.parseDouble(lPriceTF.getText()));

								rightPane.getChildren().remove(1);
								if(shown == 0)
									rightPane.getChildren().add(1, populateList(listView, io.getClientNames(), 0));
								else if(shown == 1)
									rightPane.getChildren().add(1, populateList(listView, io.getLawnNames(), 1));

								lClientTF.setText("");
								lClientTF.setPromptText("");
								lAddressTF.setText("");
								lLawnNameTF.setText("");
								lGenLocationTF.setText("");
								lIntervalTF.setText("");
								lPriceTF.setText("");

							}
							else {// one or more of the text fields are not filled in

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
							alert.showAndWait();

						}

					}

				}catch(NumberFormatException e) {

					Alert alert = new Alert(AlertType.INFORMATION);//creates a dialog box warning the user that the client does not exist
					alert.setTitle("Lawn Error");
					alert.setHeaderText(null);
					alert.setContentText("The Interval, and Price must be numbers!");
					alert.show();

				}
			}//end handle

		});//end setonaction

		cnclAddBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				addClntLwnLbl.getChildren().clear();
				addClntLwnTF.getChildren().clear();
				centerPane.getChildren().clear();

				cNameTF.setText("");
				cBiAdTF.setText("");
				cPhoneNumTF.setText("");

				lClientTF.setText("");
				lClientTF.setPromptText("");
				lAddressTF.setText("");
				lLawnNameTF.setText("");
				lGenLocationTF.setText("");
				lIntervalTF.setText("");
				lPriceTF.setText("");

				if(shown == 0) {

					sidePanelBtn.getChildren().clear();
					displayInfo.getChildren().clear();
					centerPane.getChildren().clear();

					sidePanelBtn.getChildren().addAll(cAddLawnBtn, editClntBtn, editLwnBtn, editOwesBtn, delClntBtn, delLwnBtn);
					addClntLwnLbl.getChildren().addAll(cNameLbl, cBiAdLbl, cOwesLbl, cPhoneNumLbl);
					displayInfo.getChildren().addAll(cName, cAddr, cOwes, cNum);
					centerPane.getChildren().addAll(addClntLwnLbl, displayInfo, lawnTA);
					border.setLeft(sidePanelBtn);
					border.setCenter(centerPane);

				}
				else if(shown == 1) {

					btnPane.getChildren().clear();
					displayInfo.getChildren().clear();

					notesTA.clear();
					notesTA.appendText("Name:\t\t\t" + tempLwn.getLawnName() + "\n");
					notesTA.appendText("Address:\t\t\t" + tempLwn.getAddress() + "\n");
					notesTA.appendText("General Location:\t" + tempLwn.getGenLocation() + "\n");
					notesTA.appendText("Client:\t\t\t" + tempLwn.getClient().getName() + "\n");
					notesTA.appendText("Last Mowed:\t\t" + tempLwn.sf.format(tempLwn.getLastMow()) + "\n");
					notesTA.appendText("Next Mow:\t\t" + tempLwn.sf.format(tempLwn.getNextMow()) + "\n");
					notesTA.appendText("Cost:\t\t\t\t" + df.format(tempLwn.getPrice()) + "\n");
					notesTA.appendText("Interval:\t\t\t" + tempLwn.getInterval() + "\n");
					notesTA.appendText("------------------------------------------------------\n");
					notesTA.appendText("Notes:\n" + tempLwn.getNotes());
					if(tempLwn.sf.format(tempLwn.getLastMow()).equals(tempLwn.sf.format(Calendar.getInstance().getTime())))
						lMowedCheckBox.setSelected(true);
					else
						lMowedCheckBox.setSelected(false);
					if(tempLwn.getSkip())
						lSkipCheckBox.setSelected(true);
					else
						lSkipCheckBox.setSelected(false);
					if(tempLwn.getNextMow().compareTo(java.sql.Date.valueOf("2000-01-01")) == 0)
						lStopMowCheckBox.setSelected(true);
					else
						lStopMowCheckBox.setSelected(false);

					btnPane.getChildren().addAll(lMowedCheckBox, lSkipCheckBox, lStopMowCheckBox, editLwnBtn, lAddNoteBtn);
					displayInfo.getChildren().addAll(notesTA, btnPane);
					border.setCenter(displayInfo);

				}

			}//end handle

		});//end setonaction

		cAddLawnBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				leftPane.getChildren().clear();
				addClntLwnLbl.getChildren().clear();
				addClntLwnTF.getChildren().clear();
				btnPane.getChildren().clear();
				centerPane.getChildren().clear();

				addLwnBtn.setText("Add Lawn");
				lClientTF.setPromptText(tempClnt.getName());
				lAddressTF.setText("");
				lLawnNameTF.setText("");
				lGenLocationTF.setText("");
				lIntervalTF.setText("");
				lPriceTF.setText("");
				btnPane.getChildren().addAll(addLwnBtn, cnclAddBtn);
				addClntLwnLbl.getChildren().addAll(lClientLbl, lAddressLbl, lLawnNameLbl, lGenLocationLbl, lIntervalLbl, lPriceLbl, lDateLbl);
				addClntLwnTF.getChildren().addAll(lClientTF, lAddressTF, lLawnNameTF, lGenLocationTF, lIntervalTF, lPriceTF, datePicker, btnPane);
				centerPane.getChildren().addAll(addClntLwnLbl, addClntLwnTF);
				border.setCenter(centerPane);
				lAddressTF.requestFocus();

			}//end handle

		});//end cAddLawnBtn

		editClntBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				sidePanelBtn.getChildren().clear();
				addClntLwnLbl.getChildren().clear();
				addClntLwnTF.getChildren().clear();
				btnPane.getChildren().clear();
				centerPane.getChildren().clear();

				addClntBtn.setText("Update Client");
				addClntLwnLbl.getChildren().addAll(cNameLbl, cBiAdLbl, cPhoneNumLbl);
				cNameTF.setText(tempClnt.getName());
				cBiAdTF.setText(tempClnt.getBillAddress());
				cPhoneNumTF.setText(tempClnt.getPhoneNum());
				addClntLwnTF.getChildren().addAll(cNameTF, cBiAdTF, cPhoneNumTF, btnPane);
				btnPane.getChildren().addAll(addClntBtn, cnclAddBtn);
				centerPane.getChildren().addAll(addClntLwnLbl, addClntLwnTF);
				border.setCenter(centerPane);

			}//end handle

		});//end setonaction

		editLwnBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(tempClnt != null) {//if we are making the edit from the client page

					if(tempClnt.lawnListSize() > 1) {//are there more than 1 lawns?

						centerPane.getChildren().clear();
						
						ObservableList<String> options = FXCollections.observableArrayList(tempClnt.getLawnAddresses());
						final ComboBox<String> comboBox = new ComboBox<>(options);
						if(!tempClnt.getName().endsWith("s"))
							lLawnLbl.setText(tempClnt.getName() + "'s Lawns: ");
						else
							lLawnLbl.setText(tempClnt.getName() + "' Lawns: ");
						
						centerPane.getChildren().addAll(lLawnLbl, comboBox);

						comboBox.setOnAction(new EventHandler<ActionEvent>() {

							@Override
							public void handle(ActionEvent event) {

								btnPane.getChildren().clear();
								addClntLwnLbl.getChildren().clear();
								addClntLwnTF.getChildren().clear();
								centerPane.getChildren().clear();

								tempLwn = tempClnt.getLawnFromAddress(comboBox.getValue());
								addLwnBtn.setText("Update Lawn");
								lClientTF.setText(tempLwn.getClient().getName());
								lAddressTF.setText(tempLwn.getAddress().toString());
								lLawnNameTF.setText(tempLwn.getLawnName().toString());
								lGenLocationTF.setText(tempLwn.getGenLocation().toString());
								lIntervalTF.setText("" + tempLwn.getInterval());
								lPriceTF.setText("" + df.format(tempLwn.getPrice()));
								datePicker.setValue(tempLwn.getNextMow().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
								btnPane.getChildren().addAll(addLwnBtn, cnclAddBtn);
								addClntLwnLbl.getChildren().addAll(lClientLbl, lAddressLbl, lLawnNameLbl, lGenLocationLbl, lIntervalLbl, lPriceLbl, lDateLbl);
								addClntLwnTF.getChildren().addAll(lClientTF, lAddressTF, lLawnNameTF, lGenLocationTF, lIntervalTF, lPriceTF, datePicker, btnPane);
								centerPane.getChildren().addAll(addClntLwnLbl, addClntLwnTF);
								border.setCenter(centerPane);

							}//end handle

						});//end combobox setonaction

					}
					else if(tempClnt.lawnListSize() == 1) {// is there only one lawn in the list?

						btnPane.getChildren().clear();
						addClntLwnLbl.getChildren().clear();
						addClntLwnTF.getChildren().clear();
						centerPane.getChildren().clear();

						tempLwn = tempClnt.getSingleLawn(0);
						addLwnBtn.setText("Update Lawn");
						lClientTF.setText(tempLwn.getClient().getName());
						lAddressTF.setText(tempLwn.getAddress().toString());
						lLawnNameTF.setText(tempLwn.getLawnName().toString());
						lGenLocationTF.setText(tempLwn.getGenLocation().toString());
						lIntervalTF.setText("" + tempLwn.getInterval());
						lPriceTF.setText("" + df.format(tempLwn.getPrice()));
						datePicker.setValue(tempLwn.getNextMow().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
						btnPane.getChildren().addAll(addLwnBtn, cnclAddBtn);
						addClntLwnLbl.getChildren().addAll(lClientLbl, lAddressLbl, lLawnNameLbl, lGenLocationLbl, lIntervalLbl, lPriceLbl, lDateLbl);
						addClntLwnTF.getChildren().addAll(lClientTF, lAddressTF, lLawnNameTF, lGenLocationTF, lIntervalTF, lPriceTF, datePicker, btnPane);
						centerPane.getChildren().addAll(addClntLwnLbl, addClntLwnTF);
						border.setCenter(centerPane);

					}
					else {//there are no lawns in the client's lawn list

						backupTitleLbl.setText("This client has no lawns");
						centerPane.getChildren().clear();
						centerPane.getChildren().add(backupTitleLbl);
						border.setCenter(centerPane);

					}

				}
				else {// if we are making the edit from the lawn page

					String temp[] = listView.getFocusModel().getFocusedItem().split(";");
					tempLwn = io.lawnList.get(io.getFromLawnName(temp[temp.length-1].trim()));

					btnPane.getChildren().clear();
					addClntLwnLbl.getChildren().clear();
					addClntLwnTF.getChildren().clear();
					centerPane.getChildren().clear();

					addLwnBtn.setText("Update Lawn");
					lClientTF.setText(tempLwn.getClient().getName());
					lAddressTF.setText(tempLwn.getAddress().toString());
					lLawnNameTF.setText(tempLwn.getLawnName().toString());
					lGenLocationTF.setText(tempLwn.getGenLocation().toString());
					lIntervalTF.setText("" + tempLwn.getInterval());
					lPriceTF.setText("" + df.format(tempLwn.getPrice()));
					datePicker.setValue(tempLwn.getNextMow().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

					btnPane.getChildren().addAll(addLwnBtn, cnclAddBtn);
					addClntLwnLbl.getChildren().addAll(lClientLbl, lAddressLbl, lLawnNameLbl, lGenLocationLbl, lIntervalLbl, lPriceLbl, lDateLbl);
					addClntLwnTF.getChildren().addAll(lClientTF, lAddressTF, lLawnNameTF, lGenLocationTF, lIntervalTF, lPriceTF, datePicker, btnPane);
					centerPane.getChildren().addAll(addClntLwnLbl, addClntLwnTF);
					border.setCenter(centerPane);

				}

			}//end handle

		});//end setonaction

		editOwesBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				TextInputDialog owes = new TextInputDialog();
				owes.setTitle("Edit Owes");
				owes.setHeaderText("Edit the amount owed by the client");
				owes.setContentText("New Amount:");

				Optional<String> newOwes = owes.showAndWait();
				try {

					if(newOwes.isPresent()) {
						String oldOwes = tempClnt.getOwes();
						tempClnt.setOwed(Double.parseDouble(newOwes.get()));
						displayInfo.getChildren().clear();
						cOwes.setText("$" + df.format(tempClnt.getOwed()));
						displayInfo.getChildren().addAll(cName, cAddr, cOwes, cNum);
						io.appendToTransactionFile("Owed amount for " + tempClnt.getName() + " changed from " + oldOwes + " to " + tempClnt.getOwes());
					}

				}
				catch(Exception e) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Input Error");
					alert.setHeaderText("Amount must be a number.");
					alert.show();
				}

			}//end handle

		});//end editOwesBtn

		delClntBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				sidePanelBtn.getChildren().clear();
				centerPane.getChildren().clear();

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Delete Client");
				alert.setHeaderText("Are you sure?");
				alert.setContentText("Deleting a client is permanant.");

				ButtonType delBtn = new ButtonType("Delete");
				ButtonType cnclBtn = new ButtonType("Cancel");

				alert.getButtonTypes().setAll(delBtn, cnclBtn);

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == delBtn){//user chose to delete the client

					io.removeAssociatedLawns(tempClnt);//removes all lawns that the client owns
					io.removeClient(io.getClientIndex(tempClnt.getName()));//removes the client from the list
					rightPane.getChildren().remove(1);//redisplays the listview
					rightPane.getChildren().add(1, populateList(listView, io.getClientNames(), 0));

				}
				else {

					addClntLwnLbl.getChildren().clear();
					displayInfo.getChildren().clear();
					centerPane.getChildren().clear();
					sidePanelBtn.getChildren().clear();

					addClntLwnLbl.getChildren().addAll(cNameLbl, cBiAdLbl, cOwesLbl, cPhoneNumLbl);
					displayInfo.getChildren().addAll(cName, cAddr, cOwes, cNum);
					centerPane.getChildren().addAll(addClntLwnLbl, displayInfo, lawnTA);
					sidePanelBtn.getChildren().addAll(cAddLawnBtn, editClntBtn, editLwnBtn, editOwesBtn, delClntBtn, delLwnBtn);
					border.setCenter(centerPane);
					border.setLeft(sidePanelBtn);

				}

			}//end handle

		});//end setonaction

		//deletes lawns from the client page
		delLwnBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(tempClnt.lawnListSize() > 1) {

					centerPane.getChildren().clear();

					ObservableList<String> options = FXCollections.observableArrayList(tempClnt.getLawnAddresses());
					final ComboBox<String> comboBox = new ComboBox<>(options);
					if(!tempClnt.getName().endsWith("s"))
						lLawnLbl.setText(tempClnt.getName() + "'s Lawns: ");
					else
						lLawnLbl.setText(tempClnt.getName() + "' Lawns: ");

					centerPane.getChildren().addAll(lLawnLbl, comboBox);

					comboBox.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {

							Alert alert = new Alert(AlertType.CONFIRMATION);
							alert.setTitle("Delete Lawn");
							alert.setHeaderText("Delete " + tempClnt.getLawnFromAddress(comboBox.getValue()).getAddress() + "?");
							alert.setContentText("Deleting a Lawn is permanant.");

							ButtonType delBtn = new ButtonType("Delete");
							ButtonType cnclBtn = new ButtonType("Cancel");

							alert.getButtonTypes().setAll(delBtn, cnclBtn);

							Optional<ButtonType> result = alert.showAndWait();
							if (result.get() == delBtn){
								io.lawnList.remove(tempClnt.getLawnFromAddress(comboBox.getValue()));
								tempClnt.removeLawn(tempClnt.getLawnFromAddress(comboBox.getValue()));
							}

							addClntLwnLbl.getChildren().clear();
							displayInfo.getChildren().clear();
							centerPane.getChildren().clear();

							cName.setText(tempClnt.getName());
							cAddr.setText(tempClnt.getBillAddress());
							cOwes.setText("$" + df.format(tempClnt.getOwed()));
							lawnTA.clear();
							populateLawnTA(lawnTA, cName.getText());

							addClntLwnLbl.getChildren().addAll(cNameLbl, cBiAdLbl, cOwesLbl, cPhoneNumLbl);
							displayInfo.getChildren().addAll(cName, cAddr, cOwes, cNum);
							centerPane.getChildren().addAll(addClntLwnLbl, displayInfo, lawnTA);
							border.setCenter(centerPane);

						}//end handle

					});//end combobox setonaction

				}
				else if(tempClnt.lawnListSize() == 1) {
					
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Delete Lawn");
					alert.setHeaderText("Delete " + tempClnt.getSingleLawn(0).getAddress() + "?");
					alert.setContentText("Deleting a Lawn is permanant.");

					ButtonType delBtn = new ButtonType("Delete");
					ButtonType cnclBtn = new ButtonType("Cancel");

					alert.getButtonTypes().setAll(delBtn, cnclBtn);

					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == delBtn){
						io.lawnList.remove(tempClnt.getSingleLawn(0));
						tempClnt.removeLawn(tempClnt.getSingleLawn(0));
					}

					addClntLwnLbl.getChildren().clear();
					displayInfo.getChildren().clear();
					centerPane.getChildren().clear();

					cName.setText(tempClnt.getName());
					cAddr.setText(tempClnt.getBillAddress());
					cOwes.setText("$" + df.format(tempClnt.getOwed()));
					lawnTA.clear();
					populateLawnTA(lawnTA, cName.getText());

					addClntLwnLbl.getChildren().addAll(cNameLbl, cBiAdLbl, cOwesLbl, cPhoneNumLbl);
					displayInfo.getChildren().addAll(cName, cAddr, cOwes, cNum);
					centerPane.getChildren().addAll(addClntLwnLbl, displayInfo, lawnTA);
					border.setCenter(centerPane);
					
				}
				else {//there are no lawns in the client's lawn list

					backupTitleLbl.setText("This client has no lawns");
					centerPane.getChildren().clear();
					centerPane.getChildren().add(backupTitleLbl);
					border.setCenter(centerPane);

				}

			}//end handle

		});//end setonaction

		//add a note to a lawn
		lAddNoteBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(backupTitleLbl.getText().equals("Add Note")) {//if we are on the add note page

					backupTitleLbl.setText("");

					if(!notesTA.getText().equals(""))
						tempLwn.addNotes(notesTA.getText());//addnotes is the method with the correct formatting when there are notes present
					else
						tempLwn.setNotes("");//this method literally just sets notes to blank, which is what we want

					btnPane.getChildren().clear();
					displayInfo.getChildren().clear();
					leftPane.getChildren().clear();

					notesTA.clear();
					notesTA.appendText("Name:\t\t\t" + tempLwn.getLawnName() + "\n");
					notesTA.appendText("Address:\t\t\t" + tempLwn.getAddress() + "\n");
					notesTA.appendText("General Location:\t" + tempLwn.getGenLocation() + "\n");
					notesTA.appendText("Client:\t\t\t" + tempLwn.getClient().getName() + "\n");
					notesTA.appendText("Last Mowed:\t\t" + tempLwn.sf.format(tempLwn.getLastMow()) + "\n");
					notesTA.appendText("Next Mow:\t\t" + tempLwn.sf.format(tempLwn.getNextMow()) + "\n");
					notesTA.appendText("Cost:\t\t\t\t" + df.format(tempLwn.getPrice()) + "\n");
					notesTA.appendText("Interval:\t\t\t" + tempLwn.getInterval() + "\n");
					notesTA.appendText("------------------------------------------------------\n");
					notesTA.appendText("Notes:\n" + tempLwn.getNotes());
					notesTA.autosize();
					if(tempLwn.sf.format(tempLwn.getLastMow()).equals(tempLwn.sf.format(Calendar.getInstance().getTime())))
						lMowedCheckBox.setSelected(true);
					else
						lMowedCheckBox.setSelected(false);
					if(tempLwn.getSkip())
						lSkipCheckBox.setSelected(true);
					else
						lSkipCheckBox.setSelected(false);
					if(tempLwn.getNextMow().compareTo(java.sql.Date.valueOf("2000-01-01")) == 0)
						lStopMowCheckBox.setSelected(true);
					else
						lStopMowCheckBox.setSelected(false);

					btnPane.getChildren().addAll(lMowedCheckBox, lSkipCheckBox, lStopMowCheckBox, editLwnBtn, lAddNoteBtn);
					displayInfo.getChildren().addAll(notesTA, btnPane);
					leftPane.getChildren().addAll(iSortedLawnsLbl, sortedLawnTA, lSendBtn);
					border.setLeft(leftPane);
					border.setCenter(displayInfo);

				}
				else {//else we are in the lawn page, and we want to get to the add note page

					notesTA.clear();
					notesTA.setText(tempLwn.getNotes());
					centerPane.getChildren().clear();
					backupTitleLbl.setText("Add Note");
					displayInfo.getChildren().clear();
					notesTA.setEditable(true);
					displayInfo.getChildren().addAll(backupTitleLbl, notesTA, lAddNoteBtn);
					border.setCenter(displayInfo);

				}

			}//end handle

		});//end setonaction

		//add an email to the email list
		sAddBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				TextInputDialog dialog = new TextInputDialog();
				dialog.setTitle("Add Email");
				dialog.setHeaderText("Add an email to the mailing list");

				Optional<String> resultAddress = dialog.showAndWait();
				if(!resultAddress.get().equals("") && resultAddress.isPresent()) {//if the email is not blank, and if there is a value given
					io.emailList.add(resultAddress.get());
					settingsItems.getChildren().remove(3);
					emailComboBox.setItems(FXCollections.observableArrayList(io.emailList));
					settingsItems.getChildren().add(3,emailComboBox);
				}


			}//end handle

		});//end setonaction saddbtn

		//button to delete emails from the email list
		sDelBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				//if there is an email selected
				if(!emailComboBox.getSelectionModel().getSelectedItem().equals(null)) {

					io.emailList.remove(io.emailList.indexOf(emailComboBox.getSelectionModel().getSelectedItem()));
					settingsItems.getChildren().remove(3);
					emailComboBox.setItems(FXCollections.observableArrayList(io.emailList));
					settingsItems.getChildren().add(3,emailComboBox);

				}

			}//end handle

		});//end setonaction sdelbtn

		//updates the company name
		sUpdateBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(!sCompanyNameTF.getText().equals("")) {
					io.companyName = sCompanyNameTF.getText();
					primaryStage.setTitle("Lawn Care Made Simple ("+io.companyName+")");//title
				}

			}//end handle

		});//end setonaction sSubmitbtn

		//edit the email that will be used to backup the program
		sEditBackupBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				TextInputDialog edit = new TextInputDialog();
				edit.setTitle("Edit Backup Email");
				edit.setHeaderText("Change backup email address from: " + io.getBackupEmail());
				edit.setContentText("New Address:");

				Optional<String> newEmail = edit.showAndWait();
				try {

					if(newEmail.isPresent()) {

						io.setBackupEmail(newEmail.get());
						settingsItems.getChildren().remove(3);
						emailComboBox.setItems(FXCollections.observableArrayList(io.emailList));
						settingsItems.getChildren().add(3,emailComboBox);

					}

				}
				catch(Exception e) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Input Error!");
					alert.showAndWait();
				}

			}//end handle

		});//end setonaction seditbackupbtn

		//button that actually sends messages through email
		bSendBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(backupTitleLbl.getText().equals("Backup File")) {//if we are in the backup tab

					if(!emailComboBox.getSelectionModel().isEmpty()) {//is there a value being used?

						String temp = emailComboBox.getValue();//gets the current email

						Task<Integer> task = new Task<Integer>() {

							@Override
							public Integer call() throws Exception {
								if(Mailer.send(temp, "LCMS Backup", "This is a backup of the program", io.getBackupLocation()) == 1)
									return 1 ;
								else
									return 0;
							}//end call

						};//end new task

						task.setOnSucceeded(e -> {

							if(task.getValue() == 1) {

								Alert alert = new Alert(AlertType.CONFIRMATION);
								alert.setTitle("Backup");
								alert.setHeaderText("Email sent successfully!");
								alert.showAndWait();

							}
							else {

								Alert alert = new Alert(AlertType.ERROR);
								alert.setTitle("Backup");
								alert.setHeaderText("Email not sent!");
								alert.showAndWait();

							}

						});//end setonsucceeded

						new Thread(task).start();

						displayInfo.getChildren().clear();

					}
					else {//else there is no value being used

						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Backup");
						alert.setHeaderText("No email selected!");
						alert.showAndWait();

					}

				}
				else if(backupTitleLbl.getText().equals("Backup Transactions")) {// if we are in the transactions tab

					if(!emailComboBox.getSelectionModel().isEmpty()) {//is there a value being used?

						String temp = emailComboBox.getValue();

						Task<Integer> task = new Task<Integer>() {

							@Override
							public Integer call() throws Exception {
								if(Mailer.send(temp, "LCMS Backup", "This is a backup of the Transactions", "Transactions.txt") == 1)
									return 1 ;
								else
									return 0;
							}//end call

						};//end new task

						task.setOnSucceeded(e -> {

							if(task.getValue() == 1) {

								Alert alert = new Alert(AlertType.CONFIRMATION);
								alert.setTitle("Backup");
								alert.setHeaderText("Email sent successfully!");
								alert.showAndWait();

							}
							else {

								Alert alert = new Alert(AlertType.ERROR);
								alert.setTitle("Backup");
								alert.setHeaderText("Email not sent!");
								alert.showAndWait();

							}

						});//end setonsucceeded

						new Thread(task).start();

						displayInfo.getChildren().clear();

					}
					else {//else there is no value being used

						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Backup");
						alert.setHeaderText("No email selected!");
						alert.showAndWait();

					}

				}
				else if(backupTitleLbl.getText().equals("Backup Bills")) {//if we are in the bills tab

					if(!emailComboBox.getSelectionModel().isEmpty()) {//is there a value being used?

						String temp = emailComboBox.getValue();//gets the selected email

						Task<Integer> task = new Task<Integer>() {

							@Override
							public Integer call() throws Exception {
								if(Mailer.send(temp, "LCMS Backup", "This is a backup of the Bills", "bill.txt") == 1)
									return 1 ;
								else
									return 0;
							}//end call

						};//end new task

						task.setOnSucceeded(e -> {

							if(task.getValue() == 1) {

								Alert alert = new Alert(AlertType.CONFIRMATION);
								alert.setTitle("Backup");
								alert.setHeaderText("Email sent successfully!");
								alert.showAndWait();

							}
							else {

								Alert alert = new Alert(AlertType.ERROR);
								alert.setTitle("Backup");
								alert.setHeaderText("Email not sent!");
								alert.showAndWait();

							}

						});//end setonsucceeded

						new Thread(task).start();

						displayInfo.getChildren().clear();

					}
					else {//else there is no value being used

						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Backup");
						alert.setHeaderText("No email selected!");
						alert.showAndWait();

					}

				}
				else if(backupTitleLbl.getText().equals("Send Lawn List")) {//if we are sending the lawn list through email

					if(!emailComboBox.getSelectionModel().isEmpty()) {//is there a value being used?

						String temp = emailComboBox.getValue();//gets the selected email

						Task<Integer> task = new Task<Integer>() {

							//inner method because the task can not access a method outside of 'start'
							public String populateMailLawnList() {

								String att = "";

								att += "Lawn Lists for: " + io.companyName + "\n";

								if(!disableServerCheckBox.isSelected())
									att += "Link to webserver: " + ip + ":8080\n";

								for(int i = 0; i < io.lawnList.size(); i++) {

									if((io.lawnList.get(i).getNextMow().compareTo(Calendar.getInstance().getTime()) < 0 ||
											io.lawnList.get(i).getNextMow().compareTo(Calendar.getInstance().getTime()) == 0) &&
											io.lawnList.get(i).getNextMow().compareTo(java.sql.Date.valueOf("2000-01-01")) != 0) {

										att += "-------------------------------------------------\n" +
												"Lawn Name:\t" + io.lawnList.get(i).getLawnName() + "\n" +
												"Address:\t" + io.lawnList.get(i).getAddress() + "\n";

									}

								}

								return att;

							}//end populateMailLawnList

							@Override
							public Integer call() throws Exception {
								if(Mailer.sendList(temp, "This is a list of lawns that need to be mowed", "", populateMailLawnList()) == 1)
									return 1;
								else
									return 0;
							}//end call

						};//end new task

						task.setOnSucceeded(e -> {

							if(task.getValue() == 1) {

								Alert alert = new Alert(AlertType.CONFIRMATION);
								alert.setTitle("Backup");
								alert.setHeaderText("Email sent successfully!");
								alert.showAndWait();

							}
							else {

								Alert alert = new Alert(AlertType.ERROR);
								alert.setTitle("Backup");
								alert.setHeaderText("Email not sent!");
								alert.showAndWait();

							}

						});//end setonsucceeded

						new Thread(task).start();

						displayInfo.getChildren().clear();

					}
					else {//else there is no value being used

						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Backup");
						alert.setHeaderText("No email selected!");
						alert.showAndWait();

					}

				}

			}//end handle

		});//end setonaction bSendbtn

		//button used to send all the lawns that need to be mowed for the day through email
		lSendBtn.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {

				sidePanelBtn.getChildren().clear();
				leftPane.getChildren().clear();
				displayInfo.getChildren().clear();
				centerPane.getChildren().clear();

				backupTitleLbl.setText("Send Lawn List");
				backupEmailLbl.setText("Today's Lawn list will be sent to: ");

				ObservableList<String> options = FXCollections.observableArrayList(io.emailList);
				emailComboBox.setItems(options);

				lawnTA.clear();
				lawnTA.autosize();
				populateMailLawnList(lawnTA);
				lawnTA.autosize();

				centerPane.getChildren().addAll(backupEmailLbl, emailComboBox, bSendBtn);
				displayInfo.getChildren().addAll(backupTitleLbl, centerPane, lawnTA);

				border.setCenter(displayInfo);

			}//end handle

		});//end setonaction

		lUpdateFromHTML.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				io.readInLawnsHTML();

			}//end handle

		});//end setonaction

		//when the program starts, the first client will be displayed
		listView.getFocusModel().focus(0);
		tempClnt = io.getClient(io.getClientIndex(listView.getFocusModel().getFocusedItem()));
		cName.setText(tempClnt.getName());
		cAddr.setText(tempClnt.getBillAddress());
		cOwes.setText("$" + df.format(tempClnt.getOwed()));
		cNum.setText(tempClnt.getPhoneNum());
		lawnTA.clear();
		populateLawnTA(lawnTA, cName.getText());
		addClntLwnLbl.getChildren().clear();
		displayInfo.getChildren().clear();
		sidePanelBtn.getChildren().clear();
		centerPane.getChildren().clear();
		addClntLwnLbl.getChildren().addAll(cNameLbl, cBiAdLbl, cOwesLbl, cPhoneNumLbl);
		displayInfo.getChildren().addAll(cName, cAddr, cOwes, cNum);
		sidePanelBtn.getChildren().addAll(cAddLawnBtn, editClntBtn, editLwnBtn, editOwesBtn, delClntBtn, delLwnBtn);
		centerPane.getChildren().addAll(addClntLwnLbl, displayInfo, lawnTA);
		border.setCenter(centerPane);
		border.setLeft(sidePanelBtn);

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

		serverBtn.setSpacing(10);
		serverBtn.setPadding(new Insets(0, 10, 10, 10));
		serverBtn.setAlignment(Pos.CENTER);

		settingsBtnPane.setSpacing(10);
		settingsBtnPane.setPadding(new Insets(0, 10, 10, 10));
		settingsBtnPane.setAlignment(Pos.CENTER);
		settingsBtnPane.getChildren().addAll(sAddBtn, sDelBtn);

		iAddressBox.setSpacing(20);
		iAddressBox.setAlignment(Pos.CENTER);

		iCostIntervalBox.setSpacing(20);
		iCostIntervalBox.setAlignment(Pos.CENTER);

		settingsTFPane.setSpacing(10);
		settingsTFPane.setAlignment(Pos.CENTER_LEFT);
		settingsTFPane.getChildren().addAll(sCompanyNameTF, sUpdateBtn);

		sBackupBtnPane.setSpacing(10);
		sBackupBtnPane.setAlignment(Pos.CENTER);
		sBackupBtnPane.getChildren().addAll(spin, sEditBackupBtn);

		rightPane.setSpacing(10);
		rightPane.setPadding(new Insets(20, 20, 20, 0));
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
		sidePanelBtn.setPadding(new Insets(20, 0, 20, 20));
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

		((VBox) scene.getRoot()).getChildren().addAll(menuBar, border);
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			public void handle(WindowEvent we) {

				io.generateBackupFile();
				System.exit(0);

			}//end handle

		});//end setoncloserequest

	}//end start

	public void populateLawnTA(TextArea ta, String s) {

		Client temp = io.getClient(io.getClientIndex(s));

		if(temp.lawnListSize() == 0)
			ta.appendText("\n\n\n\n\n\n\n\n\n\n\n\n                               No lawns associated with this client yet");
		else
			for(int i = 0; i < temp.lawnListSize(); i++) {

				ta.appendText("---------------------------------------------------------\n" + 
						temp.getSingleLawn(i).toString() + "\n");

			}

	}//end populateTA

	public void populateMailLawnList(TextArea ta) {

		for(int i = 0; i < io.lawnList.size(); i++) {

			if((io.lawnList.get(i).getNextMow().compareTo(Calendar.getInstance().getTime()) < 0 ||
					io.lawnList.get(i).getNextMow().compareTo(Calendar.getInstance().getTime()) == 0) &&
					io.lawnList.get(i).getNextMow().compareTo(java.sql.Date.valueOf("2000-01-01")) != 0) {

				ta.appendText("-------------------------------------------------\n" +
						"Lawn Name:\t" + io.lawnList.get(i).getLawnName() + "\n" +
						"Address:\t\t" + io.lawnList.get(i).getAddress() + "\n" +
						"Last Mow:\t" + new SimpleDateFormat("E MMMM d, y").format(io.lawnList.get(i).getLastMow()) + "\n" +
						"Next Mow:\t" + new SimpleDateFormat("E MMMM d, y").format(io.lawnList.get(i).getNextMow()) + "\n");

			}

		}

	}//end populateMailLawnList

	public void populateSortedLawnTA(TextArea ta) {

		for(int i = 0; i < io.lawnList.size(); i++) {

			if(!sf.format(io.lawnList.get(i).getNextMow()).equals("01-01-2000")) {

				ta.appendText("-------------------------------------------------\n" +
						"Lawn Name:\t" + io.lawnList.get(i).getLawnName() + "\n" +
						"Address:\t\t" + io.lawnList.get(i).getAddress() + "\n" +
						"Last Mow:\t" + new SimpleDateFormat("E MMMM d, y").format(io.lawnList.get(i).getLastMow()) + "\n" +
						"Next Mow:\t" + new SimpleDateFormat("E MMMM d, y").format(io.lawnList.get(i).getNextMow()) + "\n");

			}


		}

	}//end populateSortedLawnTA

	public ListView<String> populateList(ListView<String> listView, String[] s, int n) {

		if(n == 1) {

			String[] newList = new String[s.length];
			String temp[];
			int count = 0;

			for(int i = 0; i < s.length; i++) {

				temp = s[i].split(";");
				tempLwn = io.lawnList.get(io.getFromLawnName(temp[temp.length-1].trim()));

				if(tempLwn.getNextMow().compareTo(java.sql.Date.valueOf("2000-01-01")) != 0) {

					newList[i-count] = s[i];

				}
				else {

					count++;
					newList[s.length - count] = s[i];

				}

			}

			listView.getItems().clear();
			listView.getItems().addAll(newList);
			return listView;

		}
		else {

			listView.getItems().clear();
			listView.getItems().addAll(s);
			return listView;

		}


	}//end populateList

	//searches whatever is in the listview and displays results back in listview
	public String[] search(TextField search, String[] list) {

		ArrayList<String> temp = new ArrayList<>();

		for(int i = 0; i < list.length; i++)
			if(list[i].toLowerCase().startsWith(search.getText().toLowerCase()))
				temp.add(list[i]);

		for(int i = 0; i < list.length; i++)
			if(list[i].toLowerCase().contains(search.getText().toLowerCase()) && !temp.contains(list[i]))
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