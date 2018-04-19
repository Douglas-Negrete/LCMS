package GUI;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
	public String ip;

	public GUI() {

		io = new FileIO();

	}//constructor

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
			io.writeLawnsHTML();

			if(io.readServerFromFile()) {

				//ip = WebServer.startServer();
				try {
					ip = WebMain.startServer();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		primaryStage.setTitle("Lawn Care Made Simple");//title
		Scene scene = new Scene(new VBox(), 1150, 600);//window size

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
		//addBackupMail = new MenuItem("Edit Backup Email");
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
				sAutoBackupLbl = new Label("Set Auto Backup:"),
				sDisableServerLbl = new Label("Disable Server:"),
				sEditEmailsLbl = new Label("Edit Emails:");
		Label backupEmailLbl = new Label("The backup will be sent to this email:"),
				backupTitleLbl = new Label("");

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
		//TextField bEmail = new TextField();// gonna not use this anymore

		DatePicker datePicker = new DatePicker();

		Spinner<Integer> spin = new Spinner<>(0,31,7,1);

		CheckBox disableServerCheckBox = new CheckBox();
		CheckBox lMowedCheckBox = new CheckBox("Mowed"),
				lSkipCheckBox = new CheckBox("Skip");

		ComboBox<String> emailComboBox = new ComboBox<>();

		TextArea lawnTA = new TextArea();
		TextArea notesTA = new TextArea();
		TextArea sortedLawnTA = new TextArea();

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
		Button lStopMowBtn = new Button("Stop Mowing"),
				lAddNoteBtn = new Button("Add Note");
		Button sAddBtn = new Button("Add"),
				sDelBtn = new Button("Delete"),
				sUpdateBtn = new Button("Update");
		Button bSendBtn = new Button("Send"),
				lSendBtn = new Button("Send Lawn Lists");

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
		VBox leftPane = new VBox();//the lawn information that goes on the left
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

		//menuBackup.getItems().addAll(emailFile, emailTrans, emailBill, addBackupMail);
		menuBackup.getItems().addAll(emailFile, emailTrans, emailBill);

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

		client.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent t) {

				centerPane.getChildren().clear();
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

				tempClnt = null;// added this line to get the edit lawn button to work

				sidePanelBtn.getChildren().clear();
				centerPane.getChildren().clear();
				displayInfo.getChildren().clear();

				shown = 1;
				rightPane.getChildren().remove(1);
				rightPane.getChildren().add(1, populateList(listView, io.getLawnNames()));
				leftPane.getChildren().clear();
				sortedLawnTA.clear();
				populateSortedLawnTA(sortedLawnTA);
				leftPane.getChildren().addAll(iSortedLawnsLbl, sortedLawnTA, lSendBtn);
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
				ObservableList<String> options = FXCollections.observableArrayList(io.emailList);
				emailComboBox.setItems(options);
				//bEmail.setPromptText(io.getBackupEmail());
				//bEmail.setText(io.getBackupEmail());
				//bEmail.setEditable(false);
				//bEmail.setFocusTraversable(false);
				//centerPane.getChildren().addAll(backupEmailLbl, bEmail, bSendBtn);
				centerPane.getChildren().addAll(backupEmailLbl, emailComboBox, bSendBtn);
				lawnTA.clear();
				lawnTA.autosize();
				io.generateBackupFile();
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
				leftPane.getChildren().clear();

				displayInfo.getChildren().clear();
				backupTitleLbl.setText("Backup Transactions");
				centerPane.getChildren().clear();
				ObservableList<String> options = FXCollections.observableArrayList(io.emailList);
				emailComboBox.setItems(options);
				//				bEmail.setPromptText(io.getBackupEmail());
				//				bEmail.setEditable(false);
				//				bEmail.setFocusTraversable(false);
				//				centerPane.getChildren().addAll(backupEmailLbl, bEmail, bSendBtn);
				centerPane.getChildren().addAll(backupEmailLbl, emailComboBox, bSendBtn);
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
				leftPane.getChildren().clear();

				displayInfo.getChildren().clear();
				backupTitleLbl.setText("Backup Bills");
				centerPane.getChildren().clear();
				ObservableList<String> options = FXCollections.observableArrayList(io.emailList);
				emailComboBox.setItems(options);
				//				bEmail.setPromptText(io.getBackupEmail());
				//				bEmail.setEditable(false);
				//				bEmail.setFocusTraversable(false);
				//				centerPane.getChildren().addAll(backupEmailLbl, bEmail, bSendBtn);
				centerPane.getChildren().addAll(backupEmailLbl, emailComboBox, bSendBtn);
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
				btnPane.getChildren().clear();
				displayInfo.getChildren().clear();
				leftPane.getChildren().clear();

				ObservableList<String> options = FXCollections.observableArrayList(io.emailList);
				emailComboBox.setItems(options);

				sCompanyNameTF.setPromptText(io.companyName);

				settingsItems.getChildren().clear();
				if(io.getServer())
					disableServerCheckBox.setSelected(true);
				else
					disableServerCheckBox.setSelected(false);
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

				centerPane.getChildren().clear();
				sidePanelBtn.getChildren().clear();
				leftPane.getChildren().clear();

				backupTitleLbl.setText("FAQ");

				notesTA.clear();

				displayInfo.getChildren().clear();
				displayInfo.getChildren().addAll(backupTitleLbl, notesTA);

				centerPane.getChildren().add(displayInfo);

				border.setCenter(centerPane);

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
					cNum.setText(tempClnt.getPhoneNum());
					lawnTA.clear();
					populateLawnTA(lawnTA, cName.getText());
					centerPane.getChildren().clear();
					addClntLwnLbl.getChildren().clear();
					addClntLwnLbl.getChildren().addAll(cNameLbl, cBiAdLbl, cOwesLbl, cPhoneNumLbl);
					displayInfo.getChildren().clear();
					displayInfo.getChildren().addAll(cName, cAddr, cOwes, cNum);
					centerPane.getChildren().addAll(addClntLwnLbl, displayInfo, lawnTA);
					border.setCenter(centerPane);
					sidePanelBtn.getChildren().clear();
					sidePanelBtn.getChildren().addAll(cAddLawnBtn, editClntBtn, editLwnBtn, editOwesBtn, delClntBtn, delLwnBtn);
					border.setLeft(sidePanelBtn);

				}
				else if(shown == 1) {

					tempLwn = io.lawnList.get(listView.getFocusModel().getFocusedIndex());
					displayInfo.getChildren().clear();
					notesTA.clear();
					notesTA.setMaxWidth(325);
					notesTA.setMinHeight(400);
					notesTA.appendText("Name:\t\t\t" + tempLwn.getLawnName() + "\n");
					notesTA.appendText("Address:\t\t\t" + tempLwn.getAddress() + "\n");
					notesTA.appendText("General Location:\t" + tempLwn.getGenLocation() + "\n");
					notesTA.appendText("Client:\t\t\t" + tempLwn.getClient().getName() + "\n");
					notesTA.appendText("Last Mowed:\t\t" + tempLwn.sf.format(tempLwn.getLastMow()) + "\n");
					notesTA.appendText("Next Mow:\t\t" + tempLwn.sf.format(tempLwn.getNextMow()) + "\n");
					notesTA.appendText("Cost:\t\t\t\t" + tempLwn.getPrice() + "\n");
					notesTA.appendText("Interval:\t\t\t" + tempLwn.getInterval() + "\n");
					notesTA.appendText("------------------------------------------------------\n");
					notesTA.appendText("Notes:\n" + tempLwn.getNotes());
					btnPane.getChildren().clear();
					if(tempLwn.sf.format(tempLwn.getLastMow()).equals(tempLwn.sf.format(Calendar.getInstance().getTime())))
						lMowedCheckBox.setSelected(true);
					else
						lMowedCheckBox.setSelected(false);
					if(tempLwn.getSkip())
						lSkipCheckBox.setSelected(true);
					else
						lSkipCheckBox.setSelected(false);
					btnPane.getChildren().addAll(lMowedCheckBox, lSkipCheckBox, lStopMowBtn, editLwnBtn, lAddNoteBtn);
					displayInfo.getChildren().addAll(notesTA, btnPane);
					leftPane.getChildren().clear();
					leftPane.getChildren().addAll(iSortedLawnsLbl, sortedLawnTA, lSendBtn);
					border.setLeft(leftPane);
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
					cNum.setText(tempClnt.getPhoneNum());
					lawnTA.clear();
					populateLawnTA(lawnTA, cName.getText());
					centerPane.getChildren().clear();
					addClntLwnLbl.getChildren().clear();
					addClntLwnLbl.getChildren().addAll(cNameLbl, cBiAdLbl, cOwesLbl, cPhoneNumLbl);
					displayInfo.getChildren().clear();
					displayInfo.getChildren().addAll(cName, cAddr, cOwes, cNum);
					centerPane.getChildren().addAll(addClntLwnLbl, displayInfo, lawnTA);
					border.setCenter(centerPane);
					sidePanelBtn.getChildren().clear();
					sidePanelBtn.getChildren().addAll(cAddLawnBtn, editClntBtn, editLwnBtn, editOwesBtn, delClntBtn, delLwnBtn);
					border.setLeft(sidePanelBtn);

				}
				else if(shown == 1) {

					tempLwn = io.lawnList.get(listView.getFocusModel().getFocusedIndex());
					displayInfo.getChildren().clear();
					notesTA.clear();
					notesTA.setMaxWidth(325);
					notesTA.setMinHeight(400);
					notesTA.appendText("Name:\t\t\t" + tempLwn.getLawnName() + "\n");
					notesTA.appendText("Address:\t\t\t" + tempLwn.getAddress() + "\n");
					notesTA.appendText("General Location:\t" + tempLwn.getGenLocation() + "\n");
					notesTA.appendText("Client:\t\t\t" + tempLwn.getClient().getName() + "\n");
					notesTA.appendText("Last Mowed:\t\t" + tempLwn.sf.format(tempLwn.getLastMow()) + "\n");
					notesTA.appendText("Next Mow:\t\t" + tempLwn.sf.format(tempLwn.getNextMow()) + "\n");
					notesTA.appendText("Cost:\t\t\t\t" + tempLwn.getPrice() + "\n");
					notesTA.appendText("Interval:\t\t\t" + tempLwn.getInterval() + "\n");
					notesTA.appendText("------------------------------------------------------\n");
					notesTA.appendText("Notes:\n" + tempLwn.getNotes());
					btnPane.getChildren().clear();
					if(tempLwn.sf.format(tempLwn.getLastMow()).equals(tempLwn.sf.format(Calendar.getInstance().getTime())))
						lMowedCheckBox.setSelected(true);
					else
						lMowedCheckBox.setSelected(false);
					if(tempLwn.getSkip())
						lSkipCheckBox.setSelected(true);
					else
						lSkipCheckBox.setSelected(false);
					btnPane.getChildren().addAll(lMowedCheckBox, lSkipCheckBox, lStopMowBtn, editLwnBtn, lAddNoteBtn);
					displayInfo.getChildren().addAll(notesTA, btnPane);
					leftPane.getChildren().clear();
					leftPane.getChildren().addAll(iSortedLawnsLbl, sortedLawnTA, lSendBtn);
					border.setLeft(leftPane);
					border.setCenter(displayInfo);

				}

			}//end handle

		});//end setonkeypressed

		cAddr.setWrapText(true);

		lAddressLbl.setWrapText(true);

		iSortedLawnsLbl.setFont(new Font(20));

		searchTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {//creates a keylistener on the searchbox

			@Override
			public void handle(KeyEvent event) {

				if(event.getCode().equals(KeyCode.ENTER) || event.getCode().equals(KeyCode.BACK_SPACE)) {//when the enter or delete key is pressed

					rightPane.getChildren().remove(1);
					if(shown == 0)
						rightPane.getChildren().add(1, populateList(listView, search(searchTextField, io.getClientNames())));
					else if(shown == 1)
						rightPane.getChildren().add(1, populateList(listView, search(searchTextField, io.getLawnNames())));
					//search the list for the name entered in the searchbox

				}

			}//end handle

		});//end setonkeypressed

		disableServerCheckBox.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(disableServerCheckBox.isSelected())
					io.setServer(true);
				else
					io.setServer(false);

			}//end handle

		});//end setonaction

		lMowedCheckBox.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(lMowedCheckBox.isSelected())
					tempLwn.checkLawnOff();
				else
					tempLwn.unCheckLawnOff();

				displayInfo.getChildren().clear();
				notesTA.clear();
				notesTA.setMaxWidth(325);
				notesTA.setMinHeight(400);
				notesTA.appendText("Name:\t\t\t" + tempLwn.getLawnName() + "\n");
				notesTA.appendText("Address:\t\t\t" + tempLwn.getAddress() + "\n");
				notesTA.appendText("General Location:\t" + tempLwn.getGenLocation() + "\n");
				notesTA.appendText("Client:\t\t\t" + tempLwn.getClient().getName() + "\n");
				notesTA.appendText("Last Mowed:\t\t" + tempLwn.sf.format(tempLwn.getLastMow()) + "\n");
				notesTA.appendText("Next Mow:\t\t" + tempLwn.sf.format(tempLwn.getNextMow()) + "\n");
				notesTA.appendText("Cost:\t\t\t\t" + tempLwn.getPrice() + "\n");
				notesTA.appendText("Interval:\t\t\t" + tempLwn.getInterval() + "\n");
				notesTA.appendText("------------------------------------------------------\n");
				notesTA.appendText("Notes:\n" + tempLwn.getNotes());
				btnPane.getChildren().clear();
				btnPane.getChildren().addAll(lMowedCheckBox, lSkipCheckBox, lStopMowBtn, editLwnBtn, lAddNoteBtn);
				displayInfo.getChildren().addAll(notesTA, btnPane);

			}//end handle

		});//end setonaction lmowedcheckbox

		lSkipCheckBox.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(lSkipCheckBox.isSelected()) {
					tempLwn.skipLawn();
					tempLwn.setSkip(true);
				}
				else {
					tempLwn.unSkipLawn();
					tempLwn.setSkip(false);
				}

				displayInfo.getChildren().clear();
				notesTA.clear();
				notesTA.setMaxWidth(325);
				notesTA.setMinHeight(400);
				notesTA.appendText("Name:\t\t\t" + tempLwn.getLawnName() + "\n");
				notesTA.appendText("Address:\t\t\t" + tempLwn.getAddress() + "\n");
				notesTA.appendText("General Location:\t" + tempLwn.getGenLocation() + "\n");
				notesTA.appendText("Client:\t\t\t" + tempLwn.getClient().getName() + "\n");
				notesTA.appendText("Last Mowed:\t\t" + tempLwn.sf.format(tempLwn.getLastMow()) + "\n");
				notesTA.appendText("Next Mow:\t\t" + tempLwn.sf.format(tempLwn.getNextMow()) + "\n");
				notesTA.appendText("Cost:\t\t\t\t" + tempLwn.getPrice() + "\n");
				notesTA.appendText("Interval:\t\t\t" + tempLwn.getInterval() + "\n");
				notesTA.appendText("------------------------------------------------------\n");
				notesTA.appendText("Notes:\n" + tempLwn.getNotes());
				btnPane.getChildren().clear();
				btnPane.getChildren().addAll(lMowedCheckBox, lSkipCheckBox, lStopMowBtn, editLwnBtn, lAddNoteBtn);
				displayInfo.getChildren().addAll(notesTA, btnPane);

			}//end handle

		});//end setonaction

		lawnTA.setEditable(false);
		lawnTA.setWrapText(true);
		lawnTA.setMinWidth(325);
		lawnTA.setMaxWidth(440);
		lawnTA.setMinHeight(400);
		lawnTA.setMaxHeight(500);
		//lawnTA.setBackground(new Background(new BackgroundFill(Color.CRIMSON, null, null)));

		notesTA.setWrapText(true);
		notesTA.setEditable(false);

		sortedLawnTA.setEditable(false);
		sortedLawnTA.setMinWidth(250);
		sortedLawnTA.setMaxWidth(300);
		sortedLawnTA.setMinHeight(400);
		sortedLawnTA.setMaxHeight(500);

		clntPageBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				sidePanelBtn.getChildren().clear();
				leftPane.getChildren().clear();

				centerPane.getChildren().clear();
				btnPane.getChildren().clear();
				cNameTF.setText("");
				cBiAdTF.setText("");
				cPhoneNumTF.setText("");
				addClntBtn.setText("Add Client");
				btnPane.getChildren().addAll(addClntBtn, cnclAddBtn);
				addClntLwnLbl.getChildren().clear();
				addClntLwnLbl.getChildren().addAll(cNameLbl, cBiAdLbl, cPhoneNumLbl);
				addClntLwnTF.getChildren().clear();
				addClntLwnTF.getChildren().addAll(cNameTF, cBiAdTF, cPhoneNumTF, btnPane);
				centerPane.getChildren().addAll(addClntLwnLbl, addClntLwnTF);
				border.setCenter(centerPane);

			}//end handle

		});//end setOnAction

		lwnPageBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				sidePanelBtn.getChildren().clear();
				leftPane.getChildren().clear();

				centerPane.getChildren().clear();
				btnPane.getChildren().clear();
				addLwnBtn.setText("Add Lawn");
				lClientTF.setPromptText("");
				lClientTF.setText("");
				lAddressTF.setText("");
				lLawnNameTF.setText("");
				lGenLocationTF.setText("");
				lIntervalTF.setText("");
				lPriceTF.setText("");
				btnPane.getChildren().addAll(addLwnBtn, cnclAddBtn);
				addClntLwnLbl.getChildren().clear();
				addClntLwnLbl.getChildren().addAll(lClientLbl, lAddressLbl, lLawnNameLbl, lGenLocationLbl, lIntervalLbl, lPriceLbl, lDateLbl);
				addClntLwnTF.getChildren().clear();
				addClntLwnTF.getChildren().addAll(lClientTF, lAddressTF, lLawnNameTF, lGenLocationTF, lIntervalTF, lPriceTF, datePicker, btnPane);
				centerPane.getChildren().addAll(addClntLwnLbl, addClntLwnTF);
				border.setCenter(centerPane);

			}//end handle

		});//end setonaction

		addClntBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(!cNameTF.getText().equals("") && !cBiAdTF.getText().equals("") && !cPhoneNumTF.getText().equals("")) {

					if(addClntBtn.getText().equals("Update Client")) {
						io.getClient(io.getClientIndex(tempClnt.getName())).setName(cNameTF.getText());
						io.getClient(io.getClientIndex(tempClnt.getName())).setBillAddress(cBiAdTF.getText());
						io.getClient(io.getClientIndex(tempClnt.getName())).setPhoneNum(cPhoneNumTF.getText());
					}
					else
						io.addClient(new Client(cNameTF.getText(), cBiAdTF.getText(), cPhoneNumTF.getText()));

					rightPane.getChildren().remove(1);
					if(shown == 0)
						rightPane.getChildren().add(1, populateList(listView, io.getClientNames()));
					else if(shown == 1)
						rightPane.getChildren().add(1, populateList(listView, io.getLawnNames()));

					cNameTF.setText("");
					cBiAdTF.setText("");
					cPhoneNumTF.setText("");
					addClntLwnLbl.getChildren().clear();
					addClntLwnTF.getChildren().clear();
					sidePanelBtn.getChildren().clear();
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

				try {

					if(!addLwnBtn.getText().equals("Update Lawn")) {//if it isn't an update lawn

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
									rightPane.getChildren().add(1, populateList(listView, io.getClientNames()));
								else if(shown == 1)
									rightPane.getChildren().add(1, populateList(listView, io.getLawnNames()));

								addClntLwnLbl.getChildren().clear();
								addClntLwnTF.getChildren().clear();
								sidePanelBtn.getChildren().clear();
								centerPane.getChildren().clear();

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
									rightPane.getChildren().add(1, populateList(listView, io.getClientNames()));
								else if(shown == 1)
									rightPane.getChildren().add(1, populateList(listView, io.getLawnNames()));

								addClntLwnLbl.getChildren().clear();
								addClntLwnTF.getChildren().clear();
								sidePanelBtn.getChildren().clear();
								centerPane.getChildren().clear();

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

									io.addClient(new Client(lClientTF.getText(), addressPhone.getKey(), addressPhone.getValue()));

									io.addLawn(io.getClientIndex(lClientTF.getText()), 
											new Lawn(io.getClient(io.getClientIndex(lClientTF.getText())), lAddressTF.getText(), 
													lLawnNameTF.getText(), lGenLocationTF.getText(), Integer.parseInt(lIntervalTF.getText()), 
													Double.parseDouble(lPriceTF.getText())));

									io.getLawn(lAddressTF.getText()).setNextMow(java.sql.Date.valueOf(datePicker.getValue()));

									WebMain.serverRestart();

									rightPane.getChildren().remove(1);
									if(shown == 0)
										rightPane.getChildren().add(1, populateList(listView, io.getClientNames()));
									else if(shown == 1)
										rightPane.getChildren().add(1, populateList(listView, io.getLawnNames()));

									addClntLwnLbl.getChildren().clear();
									addClntLwnTF.getChildren().clear();
									sidePanelBtn.getChildren().clear();
									centerPane.getChildren().clear();

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
									!lIntervalTF.getText().equals("") && !lPriceTF.getText().equals("")) {

								io.getLawn(tempLwn.getAddress()).setClient(io.getClient(io.getClientIndex(lClientTF.getText())));
								io.getLawn(tempLwn.getAddress()).setAddress(lAddressTF.getText());
								io.getLawn(tempLwn.getAddress()).setLawnName(lLawnNameTF.getText());
								io.getLawn(tempLwn.getAddress()).setGenLocation(lGenLocationTF.getText());
								io.getLawn(tempLwn.getAddress()).setInterval(Integer.parseInt(lIntervalTF.getText()));
								io.getLawn(tempLwn.getAddress()).setPrice(Double.parseDouble(lPriceTF.getText()));

								rightPane.getChildren().remove(1);
								if(shown == 0)
									rightPane.getChildren().add(1, populateList(listView, io.getClientNames()));
								else if(shown == 1)
									rightPane.getChildren().add(1, populateList(listView, io.getLawnNames()));

								addClntLwnLbl.getChildren().clear();
								addClntLwnTF.getChildren().clear();
								sidePanelBtn.getChildren().clear();
								centerPane.getChildren().clear();

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

						}

					}

					lClientTF.setText("");
					lClientTF.setPromptText("");
					lAddressTF.setText("");
					lLawnNameTF.setText("");
					lGenLocationTF.setText("");
					lIntervalTF.setText("");
					lPriceTF.setText("");

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

				cNameTF.setText("");
				cBiAdTF.setText("");

				lClientTF.setText("");
				lClientTF.setPromptText("");
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
					sidePanelBtn.getChildren().clear();
					addClntLwnLbl.getChildren().clear();
					addClntLwnLbl.getChildren().addAll(cNameLbl, cBiAdLbl, cOwesLbl, cPhoneNumLbl);
					displayInfo.getChildren().clear();
					displayInfo.getChildren().addAll(cName, cAddr, cOwes, cNum);
					centerPane.getChildren().addAll(addClntLwnLbl, displayInfo, lawnTA);
					border.setCenter(centerPane);
					sidePanelBtn.getChildren().addAll(cAddLawnBtn, editClntBtn, editLwnBtn, delClntBtn, delLwnBtn);
					border.setLeft(sidePanelBtn);

				}

			}//end handle

		});//end setonaction

		cAddLawnBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				//sidePanelBtn.getChildren().clear();
				leftPane.getChildren().clear();

				centerPane.getChildren().clear();
				btnPane.getChildren().clear();
				addLwnBtn.setText("Add Lawn");
				lClientTF.setPromptText(tempClnt.getName());
				lAddressTF.setText("");
				lLawnNameTF.setText("");
				lGenLocationTF.setText("");
				lIntervalTF.setText("");
				lPriceTF.setText("");
				btnPane.getChildren().addAll(addLwnBtn, cnclAddBtn);
				addClntLwnLbl.getChildren().clear();
				addClntLwnLbl.getChildren().addAll(lClientLbl, lAddressLbl, lLawnNameLbl, lGenLocationLbl, lIntervalLbl, lPriceLbl, lDateLbl);
				addClntLwnTF.getChildren().clear();
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
				centerPane.getChildren().clear();
				btnPane.getChildren().clear();
				addClntBtn.setText("Update Client");
				btnPane.getChildren().addAll(addClntBtn, cnclAddBtn);
				addClntLwnLbl.getChildren().clear();
				addClntLwnLbl.getChildren().addAll(cNameLbl, cBiAdLbl, cPhoneNumLbl);
				addClntLwnTF.getChildren().clear();
				cNameTF.setText(tempClnt.getName());
				cBiAdTF.setText(tempClnt.getBillAddress());
				cPhoneNumTF.setText(tempClnt.getPhoneNum());
				addClntLwnTF.getChildren().addAll(cNameTF, cBiAdTF, cPhoneNumTF, btnPane);
				centerPane.getChildren().addAll(addClntLwnLbl, addClntLwnTF);
				border.setCenter(centerPane);

			}//end handle

		});//end setonaction

		editLwnBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(tempClnt != null) {//if we are making the edit from the client page

					if(tempClnt.lawnListSize() > 1) {//are there more than 1 lawns?

						ObservableList<String> options = FXCollections.observableArrayList(tempClnt.getLawnNames());
						final ComboBox<String> comboBox = new ComboBox<>(options);
						centerPane.getChildren().clear();
						if(!tempClnt.getName().endsWith("s"))
							lLawnLbl.setText(tempClnt.getName() + "'s Lawns: ");
						else
							lLawnLbl.setText(tempClnt.getName() + "' Lawns: ");
						centerPane.getChildren().addAll(lLawnLbl, comboBox);

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
								datePicker.setValue(tempLwn.getNextMow().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
								btnPane.getChildren().addAll(addLwnBtn, cnclAddBtn);
								addClntLwnLbl.getChildren().clear();
								addClntLwnLbl.getChildren().addAll(lClientLbl, lAddressLbl, lLawnNameLbl, lGenLocationLbl, lIntervalLbl, lPriceLbl, lDateLbl);
								addClntLwnTF.getChildren().clear();
								addClntLwnTF.getChildren().addAll(lClientTF, lAddressTF, lLawnNameTF, lGenLocationTF, lIntervalTF, lPriceTF, datePicker, btnPane);
								centerPane.getChildren().addAll(addClntLwnLbl, addClntLwnTF);
								border.setCenter(centerPane);

							}//end handle

						});//end combobox setonaction

					}
					else if(tempClnt.lawnListSize() == 1) {// is there only one lawn in the list?

						tempLwn = tempClnt.getSingleLawn(0);
						centerPane.getChildren().clear();
						btnPane.getChildren().clear();
						addLwnBtn.setText("Update Lawn");
						lClientTF.setText(tempLwn.getClient().getName());
						lAddressTF.setText(tempLwn.getAddress().toString());
						lLawnNameTF.setText(tempLwn.getLawnName().toString());
						lGenLocationTF.setText(tempLwn.getGenLocation().toString());
						lIntervalTF.setText("" + tempLwn.getInterval());
						lPriceTF.setText("" + tempLwn.getPrice());
						datePicker.setValue(tempLwn.getNextMow().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
						btnPane.getChildren().addAll(addLwnBtn, cnclAddBtn);
						addClntLwnLbl.getChildren().clear();
						addClntLwnLbl.getChildren().addAll(lClientLbl, lAddressLbl, lLawnNameLbl, lGenLocationLbl, lIntervalLbl, lPriceLbl, lDateLbl);
						addClntLwnTF.getChildren().clear();
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

					tempLwn = io.lawnList.get(listView.getFocusModel().getFocusedIndex());
					centerPane.getChildren().clear();
					btnPane.getChildren().clear();
					addLwnBtn.setText("Update Lawn");
					lClientTF.setText(tempLwn.getClient().getName());
					lAddressTF.setText(tempLwn.getAddress().toString());
					lLawnNameTF.setText(tempLwn.getLawnName().toString());
					lGenLocationTF.setText(tempLwn.getGenLocation().toString());
					lIntervalTF.setText("" + tempLwn.getInterval());
					lPriceTF.setText("" + tempLwn.getPrice());
					datePicker.setValue(tempLwn.getNextMow().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
					btnPane.getChildren().addAll(addLwnBtn, cnclAddBtn);
					addClntLwnLbl.getChildren().clear();
					addClntLwnLbl.getChildren().addAll(lClientLbl, lAddressLbl, lLawnNameLbl, lGenLocationLbl, lIntervalLbl, lPriceLbl, lDateLbl);
					addClntLwnTF.getChildren().clear();
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
					tempClnt.setOwed(Double.parseDouble(newOwes.get()));
					displayInfo.getChildren().clear();
					cOwes.setText("" + tempClnt.getOwed());
					displayInfo.getChildren().addAll(cName, cAddr, cOwes, cNum);
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

		delLwnBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				ObservableList<String> options = FXCollections.observableArrayList(tempClnt.getLawnNames());
				final ComboBox<String> comboBox = new ComboBox<>(options);
				centerPane.getChildren().clear();
				if(!tempClnt.getName().endsWith("s"))
					lLawnLbl.setText(tempClnt.getName() + "'s Lawns: ");
				else
					lLawnLbl.setText(tempClnt.getName() + "' Lawns: ");
				centerPane.getChildren().addAll(lLawnLbl, comboBox);

				comboBox.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {

						centerPane.getChildren().clear();

						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setTitle("Delete Lawn");
						alert.setHeaderText("Delete " + tempClnt.getLawnFromAddress(comboBox.getValue()).getAddress() + "?");
						alert.setContentText("Deleting a Lawn is permanant.");

						ButtonType buttonTypeOne = new ButtonType("Delete");
						ButtonType buttonTypeTwo = new ButtonType("Cancel");

						alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

						Optional<ButtonType> result = alert.showAndWait();
						if (result.get() == buttonTypeOne){
							tempClnt.removeLawn(tempClnt.getLawnFromAddress(comboBox.getValue()));
						}
						else {
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
						}

					}//end handle

				});//end combobox setonaction

			}//end handle

		});//end setonaction

		lAddNoteBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(backupTitleLbl.getText().equals("Add Note")) {

					backupTitleLbl.setText("");
					if(!notesTA.getText().equals(""))
						tempLwn.addNotes(notesTA.getText());
					else
						tempLwn.setNotes("");
					notesTA.setEditable(false);

					displayInfo.getChildren().clear();
					notesTA.clear();
					notesTA.setMaxWidth(325);
					notesTA.setMinHeight(400);
					notesTA.appendText("Name:\t\t\t" + tempLwn.getLawnName() + "\n");
					notesTA.appendText("Address:\t\t\t" + tempLwn.getAddress() + "\n");
					notesTA.appendText("General Location:\t" + tempLwn.getGenLocation() + "\n");
					notesTA.appendText("Client:\t\t\t" + tempLwn.getClient().getName() + "\n");
					notesTA.appendText("Last Mowed:\t\t" + tempLwn.sf.format(tempLwn.getLastMow()) + "\n");
					notesTA.appendText("Next Mow:\t\t" + tempLwn.sf.format(tempLwn.getNextMow()) + "\n");
					notesTA.appendText("Cost:\t\t\t\t" + tempLwn.getPrice() + "\n");
					notesTA.appendText("Interval:\t\t\t" + tempLwn.getInterval() + "\n");
					notesTA.appendText("------------------------------------------------------\n");
					notesTA.appendText("Notes:\n" + tempLwn.getNotes());
					notesTA.autosize();
					btnPane.getChildren().clear();
					if(tempLwn.sf.format(tempLwn.getLastMow()).equals(tempLwn.sf.format(Calendar.getInstance().getTime())))
						lMowedCheckBox.setSelected(true);
					else
						lMowedCheckBox.setSelected(false);
					btnPane.getChildren().addAll(lMowedCheckBox, lSkipCheckBox, lStopMowBtn, editLwnBtn, lAddNoteBtn);
					displayInfo.getChildren().addAll(notesTA, btnPane);
					leftPane.getChildren().clear();
					leftPane.getChildren().addAll(iSortedLawnsLbl, sortedLawnTA, lSendBtn);
					border.setLeft(leftPane);
					border.setCenter(displayInfo);

				}
				else {

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

				if(backupTitleLbl.getText().equals("Backup File")) {

					if(!emailComboBox.getSelectionModel().isEmpty()) {//is there a value being used?

						String temp = emailComboBox.getValue();

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
				else if(backupTitleLbl.getText().equals("Send Lawn List")) {

					if(!emailComboBox.getSelectionModel().isEmpty()) {//is there a value being used?

						String temp = emailComboBox.getValue();

						Task<Integer> task = new Task<Integer>() {

							public String populateMailLawnList() {

								String att = "";

								for(int i = 0; i < io.lawnList.size(); i++) {

									if(new SimpleDateFormat("MM-dd-yyyy").format(io.lawnList.get((io.lawnList.size()-1) - i).getNextMow()).equals(new SimpleDateFormat("MM-dd-yyyy").format(Calendar.getInstance().getTime()))) {

										att += "-------------------------------------------------\n" +
												"Lawn Name:\t" + io.lawnList.get((io.lawnList.size()-1) - i).getLawnName() + "\n" +
												"Address:\t" + io.lawnList.get((io.lawnList.size()-1) - i).getAddress() + "\n";

									}

								}
								return att;

							}//end populateMailLawnList

							@Override
							public Integer call() throws Exception {
								if(Mailer.sendList(temp, "LCMS Backup", "This is a list of lawns that need to be mowed\n", populateMailLawnList()) == 1)
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

		lSendBtn.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {

				sidePanelBtn.getChildren().clear();
				leftPane.getChildren().clear();

				displayInfo.getChildren().clear();
				backupTitleLbl.setText("Send Lawn List");
				centerPane.getChildren().clear();
				ObservableList<String> options = FXCollections.observableArrayList(io.emailList);
				emailComboBox.setItems(options);
				centerPane.getChildren().addAll(backupEmailLbl, emailComboBox, bSendBtn);
				lawnTA.clear();
				lawnTA.autosize();
				populateMailLawnList(lawnTA);
				lawnTA.autosize();
				displayInfo.getChildren().addAll(backupTitleLbl, centerPane, lawnTA);
				border.setCenter(displayInfo);

			}//end handle

		});//end setonaction

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

		//primaryStage.setScene(new Scene(border, 1100, 600));
		((VBox) scene.getRoot()).getChildren().addAll(menuBar, border);
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			public void handle(WindowEvent we) {

				io.generateBackupFile();
				System.exit(0);
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

	public void populateMailLawnList(TextArea ta) {

		for(int i = 0; i < io.lawnList.size(); i++) {

			if(new SimpleDateFormat("MM-dd-yyyy").format(io.lawnList.get((io.lawnList.size()-1) - i).getNextMow()).equals(new SimpleDateFormat("MM-dd-yyyy").format(Calendar.getInstance().getTime()))) {

				ta.appendText("-------------------------------------------------\n" +
						"Lawn Name:\t" + io.lawnList.get((io.lawnList.size()-1) - i).getLawnName() + "\n" +
						"Address:\t\t" + io.lawnList.get((io.lawnList.size()-1) - i).getAddress() + "\n" +
						"Last Mow:\t" + new SimpleDateFormat("E MMMM d, y").format(io.lawnList.get((io.lawnList.size()-1) - i).getLastMow()) + "\n" +
						"Next Mow:\t" + new SimpleDateFormat("E MMMM d, y").format(io.lawnList.get((io.lawnList.size()-1) - i).getNextMow()) + "\n");

			}

		}

	}//end populateMailLawnList

	public void populateSortedLawnTA(TextArea ta) {

		for(int i = 0; i < io.lawnList.size(); i++) {

			ta.insertText(i, "-------------------------------------------------\n" +
					"Lawn Name:\t" + io.lawnList.get((io.lawnList.size()-1) - i).getLawnName() + "\n" +
					"Address:\t\t" + io.lawnList.get((io.lawnList.size()-1) - i).getAddress() + "\n" +
					"Last Mow:\t" + new SimpleDateFormat("E MMMM d, y").format(io.lawnList.get((io.lawnList.size()-1) - i).getLastMow()) + "\n" +
					"Next Mow:\t" + new SimpleDateFormat("E MMMM d, y").format(io.lawnList.get((io.lawnList.size()-1) - i).getNextMow()) + "\n");


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