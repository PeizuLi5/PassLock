package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.DBUtils;
import application.SceneChangingUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class SettingPageController implements Initializable {

    @FXML
    private CheckBox capitalCheckBox;

    @FXML
    private CheckBox lowerCheckBox;

    @FXML
    private CheckBox specialCheckBox;

    @FXML
    private CheckBox numberCheckBox;

    @FXML
    private TextField lengthTextField;

    @FXML
    private Button backButton;

    @FXML
    private Button saveButton;
    
    private String author = new String();
    
    /**
     * setter of author
     * 
     * @param author	- user
     */
    public void setAuthor(String author) {
    	this.author = author;
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
		setCheckBoxs();
		
		//back to MainPage
		backButton.setOnMouseClicked(e -> {
			try {
				SceneChangingUtils.toMainPage(e, "Welcome " + author, author);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		
		//save the default generate password setting for user
		saveButton.setOnMouseClicked(e -> {
			boolean hasCapital = capitalCheckBox.isSelected();
			boolean hasLowercase = lowerCheckBox.isSelected();
			boolean hasSpecial = specialCheckBox.isSelected();
			boolean hasNumber = numberCheckBox.isSelected();
			String lengthString = lengthTextField.getText().toString(); 
			if(lengthTextField.getText().isEmpty()) {
				lengthTextField.setText("8");
				lengthString = lengthTextField.getText().toString();
			}
			
			if (hasCapital == false && hasLowercase == false && hasSpecial == false && hasNumber == false) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("Please at least check one box!");
				alert.show();
			}
			else {
				boolean noError = true;
				for(int i = 0; i < lengthString.length(); i++) {
					if(!Character.isDigit(lengthString.charAt(i))) {
						noError = false;
						break;
					}
				}
				
				if(noError) {
					int length = Integer.parseInt(lengthString);
					if(length < 1) {
						lengthTextField.clear();
						Alert alert = new Alert(AlertType.ERROR);
						alert.setContentText("Length cannot be less than 1");
						alert.show();
					}
					else if(length > 30) {
						lengthTextField.clear();
						Alert alert = new Alert(AlertType.ERROR);
						alert.setContentText("Length cannot be greater than 30");
						alert.show();
					}
					else {
						DBUtils.generatePasswordRequirement(author, hasCapital, hasLowercase, hasSpecial, hasNumber, length);
						try {
							SceneChangingUtils.toMainPage(e, "Welcome " + author, author);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
				else {
					lengthTextField.clear();
					Alert alert = new Alert(AlertType.ERROR);
					alert.setContentText("Please only enter numbers for the length");
					alert.show();
				}
			}
		});
	}

	/**
	 * set the check boxes based on the database
	 */
	private void setCheckBoxs() {
		boolean hasCapital = DBUtils.getHasCapital(author);
	    boolean hasLowerCase = DBUtils.getHasLowerCase(author);
	    boolean hasSpecialChar = DBUtils.getHasSpecialChar(author);
	    boolean hasNumber = DBUtils.getHasNumber(author);

	    capitalCheckBox.setSelected(hasCapital);
	    lowerCheckBox.setSelected(hasLowerCase);
	    specialCheckBox.setSelected(hasSpecialChar);
	    numberCheckBox.setSelected(hasNumber);
	}

}
