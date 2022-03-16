package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.Scanner;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ControllerClass {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private Button EmailContacts;

	@FXML
	private Button PDFBtn;

	@FXML
	private TextArea textArea;

	@FXML
	private MenuButton MenuBtn;

	@FXML
	private MenuItem Email;

	@FXML
	private MenuItem FullName;

	@FXML
	private MenuItem Title;

	@FXML
	private MenuItem MailingAddress;

	@FXML
	private MenuItem Save;

	@FXML
	private MenuItem SaveAs;

	@FXML
	private Button ShowCon;
	@FXML
	private Label emailLabel;

	@FXML
	private Label mailingLabel;

	@FXML
	private Label nameLabel;

	@FXML
	private Label titleLabel;

	@FXML
	private GridPane gridP;

	@FXML
	private ScrollPane ScrollP;

	@FXML
	private TextField Subject;

	@FXML
	private Button AddContactsBtn;

	@FXML
	private MenuItem SaveContacts;

	@FXML
	private MenuItem SMTPServer;

	@FXML
	private MenuItem PortN;

	  @FXML
	private MenuItem Reset;

	@FXML
	private Button Edit;

	private SendEmailOffice365[] Mailer;

	private File Template;// defined here to save in the same file after opening
							// it

	private int j;// an index that is be used for adding more contacts

	private ArrayList<TextField> Em;// Emails
	private ArrayList<TextField> Ma;// Mailing Addresses
	private ArrayList<TextField> Ti;// Titles
	private ArrayList<TextField> Na;// Names
	private String smtp = "smtp.kfupm.edu.sa";
	private int port = 587;

	@FXML
	void SaveAsContacts(ActionEvent event) {
		try {
			FileChooser filechooser = new FileChooser();
			filechooser.setInitialFileName("Contacts");
			ExtensionFilter ExFilter = new ExtensionFilter("TXT files (.txt)", "*.txt");
			filechooser.getExtensionFilters().add(ExFilter);
			File file = filechooser.showSaveDialog(Main.primaryStage);
			if (file != null) {
				PrintWriter writer = new PrintWriter(file);
				for (int i = 0; i < Em.size(); i++) {
					writer.print(Ti.get(i).getText() + " " + Na.get(i).getText() + " " + Em.get(i).getText() + " "
							+ Ma.get(i).getText() + "\n");
				}
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setHeaderText("File saved Succesfully!");
				alert.showAndWait();
				writer.flush();
				writer.close();
			}
		}catch (FileNotFoundException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Error");
			alert.setContentText("Saving cancelled");
			alert.showAndWait();
		}
	}

	@FXML
	void Editable(ActionEvent event) {
		if (Edit.getText().equals("Edit")) {
			Edit.setText("Stop Editing");
			for (int i = 0; i < Em.size(); i++) {
				AddContactsBtn.setDisable(false);
				Em.get(i).setEditable(true);
				Ma.get(i).setEditable(true);
				Ti.get(i).setEditable(true);
				Na.get(i).setEditable(true);
			}
		} else {
			Edit.setText("Edit");
			for (int i = 0; i < Em.size(); i++) {
				AddContactsBtn.setDisable(true);
				Em.get(i).setEditable(false);
				Ma.get(i).setEditable(false);
				Ti.get(i).setEditable(false);
				Na.get(i).setEditable(false);
			}
		}
	}

	@FXML
	void TextEntered(KeyEvent event) {
		// you cannot save unless you type

		SaveAs.setDisable(false);

	}

	@FXML
	void SaveAsFile(ActionEvent event) {
		try {
			FileChooser file = new FileChooser();
			file.setInitialFileName("Template");
			ExtensionFilter ExFilter = new ExtensionFilter("TXT files (.txt)", "*.txt");
			file.getExtensionFilters().add(ExFilter);
			File f = file.showSaveDialog(Main.primaryStage);
			if (f != null) {
				PrintWriter a = new PrintWriter(f);
				a.print(textArea.getText());
				a.flush();
				a.close();
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setHeaderText("File saved Succesfully!");
				alert.showAndWait();
			}

		} catch (FileNotFoundException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Error");
			alert.setContentText("Saving cancelled");
			alert.showAndWait();
		}
	}

	@FXML
	void SaveFile(ActionEvent event) throws FileNotFoundException {//no need to prompt a dialog for exception here because
																//the saving option won't be available unless a file is opened
		PrintWriter a = new PrintWriter(Template);
		a.print(textArea.getText());
		a.flush();
		a.close();
	}

	@FXML
	void CreatePDF(ActionEvent event) {
		for (int i = Em.size() - 1; i >= 0; i--) {
			if (Em.get(i).getText().trim().equals("")) {
				Em.remove(i);
			}
		}
		String[] tag = new String[Em.size()];
		for (int i = 0; i < tag.length; i++) {//to replace tags with actual receiver data
			tag[i] = textArea.getText();
			if (textArea.getText().indexOf("[[TITLE]]") != -1) {
				tag[i] = tag[i].replace(textArea.getText().substring(textArea.getText().indexOf("[[TITLE]]"),
						textArea.getText().indexOf("[[TITLE]]") + 9), Ti.get(i).getText());
			}
			if (textArea.getText().indexOf("[[FULL_NAME]]") != -1) {
				tag[i] = tag[i].replace(textArea.getText().substring(textArea.getText().indexOf("[[FULL_NAME]]"),
						textArea.getText().indexOf("[[FULL_NAME]]") + 13), Na.get(i).getText());
			}
			if (textArea.getText().indexOf("[[EMAIL]]") != -1) {
				tag[i] = tag[i].replace(textArea.getText().substring(textArea.getText().indexOf("[[EMAIL]]"),
						textArea.getText().indexOf("[[EMAIL]]") + 9), Em.get(i).getText());
			}
			if (textArea.getText().indexOf("[[MAILING_ADDRESS]]") != -1) {
				tag[i] = tag[i].replace(textArea.getText().substring(textArea.getText().indexOf("[[MAILING_ADDRESS]]"),
						textArea.getText().indexOf("[[MAILING_ADDRESS]]") + 19), Ma.get(i).getText());
			}
		}

		try {
			FileChooser filechooser = new FileChooser();
			filechooser.setInitialFileName("Message");
			ExtensionFilter ExFilter = new ExtensionFilter("PDF files", "*.PDF");
			filechooser.getExtensionFilters().add(ExFilter);
			File file = filechooser.showSaveDialog(Main.primaryStage);
			OutputStream write = new FileOutputStream(file);

			Document document = new Document();
			PdfWriter.getInstance(document, write);
			document.open();
			for (int i = 0; i < tag.length; i++) {
				document.add(new Paragraph(Ma.get(i).getText()));
				document.add(new Paragraph("\n\n\n"));
				document.add(new Paragraph(Subject.getText()));
				document.add(new Paragraph("\n"));
				document.add(new Paragraph(tag[i]));
				document.newPage();
			}
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setHeaderText("File saved Succesfully!");
			alert.showAndWait();

			document.close();
			write.close();
		} catch (NullPointerException e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setHeaderText("File Not Saved");
			alert.setContentText("Saving cancelled");
			alert.showAndWait();
		} catch (DocumentException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Error");
			alert.setContentText("Document Exception");
			alert.showAndWait();
		} catch (FileNotFoundException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Error");
			alert.setContentText("FileNotFound Exception");
			alert.showAndWait();
		} catch (IOException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Error");
			alert.setContentText("IOException");
			alert.showAndWait();
		}
	}

	@FXML
	void LoadFile(ActionEvent event) {
		File f;
		FileChooser file = new FileChooser();
		file.setTitle("open");
		ExtensionFilter ExFilter = new ExtensionFilter("CSV files", "*.csv");
		file.getExtensionFilters().add(ExFilter);

		try {
			f = file.showOpenDialog(Main.primaryStage);
			FileInputStream read = new FileInputStream(f);
			Scanner s = new Scanner(read);

			String line = "";
			String[] content = null;
			while (s.hasNext()) {
				line += s.nextLine();
				line += ","; // because the end of the line doesn't have a
								// comma so it will be combined with the
								// first element of the next line
				content = line.split(",");
			}

			String title = content[0];
			String fullname = content[1];
			String email = content[2];
			String mailingAddress = content[3];
			String[] Title = new String[(content.length / 4) - 1];
			String[] Fullname = new String[(content.length / 4) - 1];
			String[] Email = new String[(content.length / 4) - 1];
			String[] MailingAddress = new String[(content.length / 4) - 1];

			int k = 0;
			int k1 = 0;
			int k2 = 0;
			int k3 = 0;
			for (int i = 4; i < content.length; i = i + 4) {

				Title[k] = content[i];
				k++;
			}
			for (int i = 5; i < content.length; i = i + 4) {

				Fullname[k1] = content[i];
				k1++;
			}
			for (int i = 6; i < content.length; i = i + 4) {

				Email[k2] = content[i];
				k2++;
			}
			for (int i = 7; i < content.length; i = i + 4) {

				MailingAddress[k3] = content[i];
				k3++;
			}

			titleLabel.setText(title);
			nameLabel.setText(fullname);
			emailLabel.setText(email);
			mailingLabel.setText(mailingAddress);

			Ti = new ArrayList<TextField>();
			Na = new ArrayList<TextField>();
			Em = new ArrayList<TextField>();
			Ma = new ArrayList<TextField>();

			for (int i = 0; i < Title.length; i++) {
				if (Title.length > 16) {//the grid pane is initially set with 17 row
					for (j = 0; j < Title.length - 16; j++) {
						gridP.addRow(17 + j, new Text(""));

					}
				}
				Ti.add(new TextField());
				Ti.get(i).setText(Title[i]);
				Ti.get(i).setEditable(false);
				gridP.add(Ti.get(i), 0, i + 1);

			}
			for (int i = 0; i < Title.length; i++) {
				Na.add(new TextField());
				Na.get(i).setText(Fullname[i]);
				Na.get(i).setEditable(false);
				gridP.add(Na.get(i), 1, i + 1);

			}
			for (int i = 0; i < Title.length; i++) {
				Em.add(new TextField());
				Em.get(i).setText(Email[i]);
				Em.get(i).setEditable(false);

				gridP.add(Em.get(i), 2, i + 1);

			}
			for (int i = 0; i < Title.length; i++) {
				Ma.add(new TextField());
				Ma.get(i).setText(MailingAddress[i]);
				Ma.get(i).setEditable(false);
				gridP.add(Ma.get(i), 3, i + 1);

			}
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setHeaderText("File Loaded Succesfully!");
			alert.showAndWait();
			PDFBtn.setDisable(false);
			EmailContacts.setDisable(false);
			textArea.setEditable(true);
			MenuBtn.setDisable(false);
			ShowCon.setDisable(false);
			Subject.setDisable(false);
			SaveContacts.setDisable(false);
			SMTPServer.setDisable(false);
			PortN.setDisable(false);
			Reset.setDisable(false);
			s.close();

		} catch (NullPointerException e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setHeaderText("no file is opened");
			alert.setContentText("a file of type CSV should be chosen");
			alert.showAndWait();
		}catch (FileNotFoundException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Error");
			alert.setContentText("FileNotFound Exception");
			alert.showAndWait();
		}
	}

	@FXML
	void LoadTemplate(ActionEvent event) throws FileNotFoundException {

		FileChooser file = new FileChooser();
		file.setTitle("open");
		ExtensionFilter ExFilter = new ExtensionFilter("Text files", "*.txt");
		file.getExtensionFilters().add(ExFilter);

		try {
			Template = file.showOpenDialog(Main.primaryStage);

			FileInputStream read = new FileInputStream(Template);
			Scanner scan = new Scanner(read);
			String text = "";
			for (int i = 1; i > 0;) {
				if (scan.hasNextLine()) {
					text += scan.nextLine() + "\n";
				} else {
					i--;
				}
				textArea.setText(text);

			}
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setHeaderText("File saved Succesfully!");
			alert.showAndWait();
			Save.setDisable(false);
			SaveAs.setDisable(false);
			scan.close();
		} catch (NullPointerException e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setHeaderText("no file is opened");
			alert.setContentText("a file of type Text should be chosen");
			alert.showAndWait();
		}
	}

	@FXML
	void SendEM(ActionEvent event) {
		try {
			for (int i = Em.size() - 1; i >= 0; i--) {
				if (Em.get(i).getText().trim().equals("")) {
					Em.remove(i);
				}
			} // if it went from 0 it will remove the first element that is
				// empty and the next empty element its index will be changed so
				// going in the opposite will prevent that

			String[] tag = new String[Em.size()];
			for (int i = 0; i < tag.length; i++) {//to replace tags with actual receiver data
				if (!Em.get(i).getText().trim().equals("")) {
					tag[i] = textArea.getText();
					if (textArea.getText().indexOf("[[TITLE]]") != -1) {
						tag[i] = tag[i].replace(textArea.getText().substring(textArea.getText().indexOf("[[TITLE]]"),
								textArea.getText().indexOf("[[TITLE]]") + 9), Ti.get(i).getText());
					}
					if (textArea.getText().indexOf("[[FULL_NAME]]") != -1) {
						tag[i] = tag[i]
								.replace(textArea.getText().substring(textArea.getText().indexOf("[[FULL_NAME]]"),
										textArea.getText().indexOf("[[FULL_NAME]]") + 13), Na.get(i).getText());
					}
					if (textArea.getText().indexOf("[[EMAIL]]") != -1) {
						tag[i] = tag[i].replace(textArea.getText().substring(textArea.getText().indexOf("[[EMAIL]]"),
								textArea.getText().indexOf("[[EMAIL]]") + 9), Em.get(i).getText());
					}
					if (textArea.getText().indexOf("[[MAILING_ADDRESS]]") != -1) {
						tag[i] = tag[i]
								.replace(
										textArea.getText().substring(textArea.getText().indexOf("[[MAILING_ADDRESS]]"),
												textArea.getText().indexOf("[[MAILING_ADDRESS]]") + 19),
										Ma.get(i).getText());
					}
				}
			}

			PasswordDialog p = new PasswordDialog();
			p.start((Stage) Main.primaryStage);
			String userName = PasswordDialog.login.getUserName();
			String password = PasswordDialog.login.getPassword();
			Mailer = new SendEmailOffice365[Em.size()];

			String info = "";
			for (int i = 0; i < Mailer.length; i++) {
				Mailer[i] = new SendEmailOffice365(userName, password, Em.get(i).getText(), Subject.getText(), tag[i]);
				Mailer[i].setSMTP(smtp);
				Mailer[i].setPort(port);
				if (Mailer[i].sendEmail())
					info += "Email was sent to: " + Na.get(i).getText() + "  Successfully\n";
				else
					info += "Error, email wasn't sent to " + Na.get(i).getText() + "\n";
			}
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setHeaderText("Sending email results");
			alert.setContentText(info);
			alert.showAndWait();

		} catch (NullPointerException e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setHeaderText("Error");
			alert.setContentText("Sending is canceled");
			alert.showAndWait();
		}
	}

	@FXML
	void addTitle(ActionEvent event) {
		textArea.setText(textArea.getText() + " " + Title.getText());
	}

	@FXML
	void AddFullName(ActionEvent event) {
		textArea.setText(textArea.getText() + " " + FullName.getText());
	}

	@FXML
	void addEmail(ActionEvent event) {
		textArea.setText(textArea.getText() + " " + Email.getText());
	}

	@FXML
	void addmailing(ActionEvent event) {
		textArea.setText(textArea.getText() + " " + MailingAddress.getText());
	}

	@FXML
	void ShowContacts(ActionEvent event) {

		if (ShowCon.getText().toLowerCase().equals("show contacts")) {
			ShowCon.setText("Hide contacts");
			textArea.setVisible(false);
			Subject.setVisible(false);
			ScrollP.setVisible(true);
			MenuBtn.setVisible(false);
			Edit.setVisible(true);
			AddContactsBtn.setVisible(true);

		} else if (ShowCon.getText().toLowerCase().equals("hide contacts")) {
			ShowCon.setText("Show contacts");
			textArea.setVisible(true);
			Subject.setVisible(true);
			ScrollP.setVisible(false);
			MenuBtn.setVisible(true);
			Edit.setVisible(false);
			AddContactsBtn.setVisible(false);
		}
	}

	@FXML
	void AddContacts(ActionEvent event) {
		if (Ti.size() < 17) {
			Ti.add(new TextField());
			Na.add(new TextField());
			Em.add(new TextField());
			Ma.add(new TextField());

			gridP.add(Ti.get(Ti.size() - 1), 0, Ti.size());
			gridP.add(Na.get(Na.size() - 1), 1, Ti.size());
			gridP.add(Em.get(Em.size() - 1), 2, Ti.size());
			gridP.add(Ma.get(Ma.size() - 1), 3, Ti.size());
		} else {
			gridP.addRow(16 + j, new Text(""));
			j++;
			Ti.add(new TextField());
			Na.add(new TextField());
			Em.add(new TextField());
			Ma.add(new TextField());

			gridP.add(Ti.get(Ti.size() - 1), 0, 16 + j);
			gridP.add(Na.get(Na.size() - 1), 1, 16 + j);
			gridP.add(Em.get(Em.size() - 1), 2, 16 + j);
			gridP.add(Ma.get(Ma.size() - 1), 3, 16 + j);
		}

	}

	@FXML
	void SMTP(ActionEvent event) {
		try {
			TextInputDialog dialog = new TextInputDialog();
			dialog.setHeaderText("change SMTP Server");
			dialog.setContentText("Insert your SMTP server here: ");
			smtp = dialog.showAndWait().get();
		} catch (NoSuchElementException e) {

		}
	}

	@FXML
	void setPort(ActionEvent event) {
		try {
			TextInputDialog dialog = new TextInputDialog();
			dialog.setHeaderText("change Port number");
			dialog.setContentText("Insert your Port Number here: ");
			String p = dialog.showAndWait().get();

			port = Integer.parseInt(p);
		} catch (NumberFormatException e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setHeaderText("Error");
			a.setContentText("only numbers can be entered");
			a.showAndWait();
		} catch (NoSuchElementException e) {

		}
	}


    @FXML
    void ResetSettings(ActionEvent event) {//to reset to default settings
    	smtp = "smtp.kfupm.edu.sa";
    	 port = 587;
    }

	@FXML
	void Close(ActionEvent event) {
		System.exit(0);
	}

	@FXML
	void initialize() {
		 assert AddContactsBtn != null : "fx:id=\"AddContactsBtn\" was not injected: check your FXML file 'design.fxml'.";
	        assert Edit != null : "fx:id=\"Edit\" was not injected: check your FXML file 'design.fxml'.";
	        assert Email != null : "fx:id=\"Email\" was not injected: check your FXML file 'design.fxml'.";
	        assert EmailContacts != null : "fx:id=\"EmailContacts\" was not injected: check your FXML file 'design.fxml'.";
	        assert FullName != null : "fx:id=\"FullName\" was not injected: check your FXML file 'design.fxml'.";
	        assert MailingAddress != null : "fx:id=\"MailingAddress\" was not injected: check your FXML file 'design.fxml'.";
	        assert MenuBtn != null : "fx:id=\"MenuBtn\" was not injected: check your FXML file 'design.fxml'.";
	        assert PDFBtn != null : "fx:id=\"PDFBtn\" was not injected: check your FXML file 'design.fxml'.";
	        assert PortN != null : "fx:id=\"PortN\" was not injected: check your FXML file 'design.fxml'.";
	        assert Reset != null : "fx:id=\"Reset\" was not injected: check your FXML file 'design.fxml'.";
	        assert SMTPServer != null : "fx:id=\"SMTPServer\" was not injected: check your FXML file 'design.fxml'.";
	        assert Save != null : "fx:id=\"Save\" was not injected: check your FXML file 'design.fxml'.";
	        assert SaveAs != null : "fx:id=\"SaveAs\" was not injected: check your FXML file 'design.fxml'.";
	        assert SaveContacts != null : "fx:id=\"SaveContacts\" was not injected: check your FXML file 'design.fxml'.";
	        assert ScrollP != null : "fx:id=\"ScrollP\" was not injected: check your FXML file 'design.fxml'.";
	        assert ShowCon != null : "fx:id=\"ShowCon\" was not injected: check your FXML file 'design.fxml'.";
	        assert Subject != null : "fx:id=\"Subject\" was not injected: check your FXML file 'design.fxml'.";
	        assert Title != null : "fx:id=\"Title\" was not injected: check your FXML file 'design.fxml'.";
	        assert emailLabel != null : "fx:id=\"emailLabel\" was not injected: check your FXML file 'design.fxml'.";
	        assert gridP != null : "fx:id=\"gridP\" was not injected: check your FXML file 'design.fxml'.";
	        assert mailingLabel != null : "fx:id=\"mailingLabel\" was not injected: check your FXML file 'design.fxml'.";
	        assert nameLabel != null : "fx:id=\"nameLabel\" was not injected: check your FXML file 'design.fxml'.";
	        assert textArea != null : "fx:id=\"textArea\" was not injected: check your FXML file 'design.fxml'.";
	        assert titleLabel != null : "fx:id=\"titleLabel\" was not injected: check your FXML file 'design.fxml'.";


	}

}
