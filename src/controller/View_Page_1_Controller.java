package controller;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Account;
import application.DBUtils;
import application.SceneChangingUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

public class View_Page_1_Controller implements Initializable{

    @FXML
    private Button backButton;

    @FXML
    private Button modifyButton;

    @FXML
    private Button DeleteButton;

    @FXML
    private Label websiteNameLabel;

    @FXML
    private Label urlLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private Label creationDateLabel;

    @FXML
    private Label expirationDateLabel;

    @FXML
    private Button copyButton;

    private Account account;
    private String author = "";
    
    public void setAuthor(String author) {
    	this.author = author;
    }
    
    public void setAccount(Account account) {
    	this.account = new Account(account);
    }
    
    public Account getAccount() {
    	return account;
    }
    
	/**
	 * Called to initialize a controller after its root element has been completely
	 * processed.
	 * 
	 * @param location  - The location used to resolve relative paths for the root
	 *                  object, or null if the location is not known.
	 * @param resources - The resources used to localize the root object, or null if
	 *                  the root object was not localized.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//set all the labels based on the account user selected
		websiteNameLabel.setText(getAccount().getWebsite_name());
		urlLabel.setText("URL: " + getAccount().getUrl());
		if(getAccount().getEmail().isEmpty()) {
			usernameLabel.setText("Username: " + getAccount().getUsername());
			emailLabel.setText("Email: " + getAccount().getUsername());
		}
		else if (getAccount().getUsername().isEmpty()){
			usernameLabel.setText("Username: " + getAccount().getEmail());
			emailLabel.setText("Email: " + getAccount().getEmail());
		}
		else {
			usernameLabel.setText("Username: " + getAccount().getUsername());
			emailLabel.setText("Email: " + getAccount().getEmail());
		}
		passwordLabel.setText("Password: " + getAccount().getPassword());
		creationDateLabel.setText("Creation Date: " + getAccount().getCreationDate());
		expirationDateLabel.setText("Expiration Date: " + getAccount().getExpirationDate());
		
		//back button listener to go back to MainPage
		backButton.setOnMouseClicked(e -> {
			try {
				SceneChangingUtils.toMainPage(e, "Welcome " + author, author);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		
		//copy the password to clipboard once user click this button
		copyButton.setOnMouseClicked(e -> {			
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			StringSelection stringSelection = new StringSelection(getAccount().getPassword());
			clipboard.setContents(stringSelection, null);
		});
		
		//delete the account user select (give comfirmation first)
		DeleteButton.setOnMouseClicked(e -> {
            try {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setContentText("Are you sure you want to delete this item?");
                Optional<ButtonType> result = alert.showAndWait();

                String websiteName = getAccount().getWebsite_name();
                String url = getAccount().getUrl();
                String password = getAccount().getEncryptedPassword();
                Date creationDate = Date.valueOf(getAccount().getCreationDate());
                Date expirationDate = Date.valueOf(getAccount().getExpirationDate());

                if(result.get() == ButtonType.OK) {
                    DBUtils.delete(e, author, websiteName, url, password, creationDate, expirationDate);
                }
                else {
                    SceneChangingUtils.toMainPage(e, "Welcome " + author, author);

                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
		
		//go to modify page (passing user data and account data)
		modifyButton.setOnMouseClicked(e -> {
			try {
				SceneChangingUtils.toModifyPage(e, "Modify", author, account);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}


}
