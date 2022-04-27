package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.AccountRequirementsUtils;
import application.DBUtils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

public class RegisterPageController implements Initializable{

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private Label lowerLetterLabel;

    @FXML
    private Label upperLetterLabel;

    @FXML
    private Label specialLetterLabel;

    @FXML
    private Label numberLabel;
    
    @FXML
    private Label minLengthLabel;

    @FXML
    private TextField passwordTextField;

    @FXML
    private TextField rePasswordTextField;

    @FXML
    private TextField securityQuestionTextField;

    @FXML
    private TextField securityAnswerTextField;

    @FXML
    private Button cancelButton;
    
    @FXML
    private Button registerButton;

    private Alert alert = new Alert(AlertType.NONE);
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setTextFieldEvents();
		
		cancelButton.setOnMouseClicked(e -> {
			try {
				DBUtils.changeScene(e, "PassLock", "view/HomePage.fxml");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		
		registerButton.setOnMouseClicked(e -> {
			registerAccount(e);
		});
		
	}

	//register process
	protected void registerAccount(Event e) {
		String username, email, firstname, lastname, securityQuestion, securityAnswer;
		String password = passwordTextField.getText().toString();
		String rePassword = rePasswordTextField.getText().toString();
		if(hasEmptyTextField()) {
			alert.setAlertType(AlertType.ERROR);
			alert = printErrorMessage(alert);
			alert.show();
		}
		else if(checkValidEmail() == false) {
			alert.setAlertType(AlertType.ERROR);
			alert.setContentText("Please enter a valid email");
			emailTextField.setStyle("-fx-text-box-border: #FF0000;");
			alert.show();
		}
		else if(checkRequirement(password) == false) {
			alert.setAlertType(AlertType.ERROR);
			alert.setContentText("Please fulfill all password requirement");
			passwordTextField.setStyle("-fx-text-box-border: #FF0000;");
			alert.show();
		}
		else if(password.equals(rePassword) == false) {
			alert.setAlertType(AlertType.ERROR);
			alert.setContentText("Password and re-entered password do not match.");
			passwordTextField.setStyle("-fx-text-box-border: #FF0000;");
			rePasswordTextField.setStyle("-fx-text-box-border: #FF0000;");
			alert.show();
		}
		else {
			firstname = firstNameTextField.getText().toString();
			lastname = lastNameTextField.getText().toString();
			username = usernameTextField.getText().toString();
			email = emailTextField.getText().toString();
			password = passwordTextField.getText().toString();
			securityQuestion = securityQuestionTextField.getText().toString();
			securityAnswer = securityAnswerTextField.getText().toString();
			
			DBUtils.registerAccount(e, firstname, lastname, username, email, password, securityQuestion, securityAnswer);
		}
	}
	
	//print alert message and change the empty text field border to red
	protected Alert printErrorMessage(Alert alert) {
		String message = "";
		if(firstNameTextField.getText().trim().isEmpty()) {
			message += "Missing first name.\n";
			firstNameTextField.setStyle("-fx-text-box-border: #FF0000;");
		}
		if(lastNameTextField.getText().trim().isEmpty()) {
			message += "Missing last name.\n";
			lastNameTextField.setStyle("-fx-text-box-border: #FF0000;");
		}
		if(usernameTextField.getText().trim().isEmpty()) {
			message += "Missing user name.\n";
			usernameTextField.setStyle("-fx-text-box-border: #FF0000;");
		}
		if(emailTextField.getText().trim().isEmpty()) {
			message += "Missing email address.\n";
			emailTextField.setStyle("-fx-text-box-border: #FF0000;");
		}
		if(passwordTextField.getText().trim().isEmpty()) {
			message += "Missing password.\n";
			passwordTextField.setStyle("-fx-text-box-border: #FF0000;");
		}
		if(rePasswordTextField.getText().trim().isEmpty()) {
			message += "Missing re-enter password.\n";
			rePasswordTextField.setStyle("-fx-text-box-border: #FF0000;");
		}
		if(securityQuestionTextField.getText().trim().isEmpty()) {
			message += "Missing security question.\n";
			securityQuestionTextField.setStyle("-fx-text-box-border: #FF0000;");
		}
		if(securityAnswerTextField.getText().trim().isEmpty()) {
			message += "Missing security answer.";
			securityAnswerTextField.setStyle("-fx-text-box-border: #FF0000;");
		}
		alert.setContentText(message);
		return alert;
		
	}

	//check if there exists a empty text field
	protected boolean hasEmptyTextField() {
		if(!firstNameTextField.getText().trim().isEmpty() &&
		   !lastNameTextField.getText().trim().isEmpty() &&
		   !usernameTextField.getText().trim().isEmpty() &&
		   !emailTextField.getText().trim().isEmpty() &&
		   !passwordTextField.getText().trim().isEmpty() &&
		   !rePasswordTextField.getText().trim().isEmpty() &&
		   !securityQuestionTextField.getText().trim().isEmpty() &&
		   !securityAnswerTextField.getText().trim().isEmpty())
			return false;
		return true;
	}
	
	//calls the static method on AccountRequirements class to check the validation of email
	protected boolean checkValidEmail() {
		String email = emailTextField.getText().toString();
		return AccountRequirementsUtils.isValidEmail(email);
	}
	
	/*
	 * change the label color to green if it meets the requirement
	 * change the label color to red if it does not meet the requirement 
	 */
	protected boolean checkRequirement(String password) {
		if(AccountRequirementsUtils.hasUpperCase(password)) {
			upperLetterLabel.setTextFill(Color.GREEN);
		}
		else {
			upperLetterLabel.setTextFill(Color.RED);
		}
		
		if(AccountRequirementsUtils.hasLowerCase(password)) {
			lowerLetterLabel.setTextFill(Color.GREEN);
		}
		else {
			lowerLetterLabel.setTextFill(Color.RED);
		}
		
		if(AccountRequirementsUtils.hasSpecial(password)){
			specialLetterLabel.setTextFill(Color.GREEN);
		}
		else {
			specialLetterLabel.setTextFill(Color.RED);
		}
		
		if(AccountRequirementsUtils.hasNumber(password)) {
			numberLabel.setTextFill(Color.GREEN);
		}
		else {
			numberLabel.setTextFill(Color.RED);
		}
		
		if(password.length() >= 8) {
			minLengthLabel.setTextFill(Color.GREEN);
		}
		else {
			minLengthLabel.setTextFill(Color.RED);
		}
		
		return (AccountRequirementsUtils.hasUpperCase(password) && 
				AccountRequirementsUtils.hasLowerCase(password) && 
				AccountRequirementsUtils.hasSpecial(password) && 
				AccountRequirementsUtils.hasNumber(password) && 
				password.length() >= 8);
	}
	
	/*
	 * When users clicks the text field return to the default border color
	 * When users press enter, start register process
	 */
	protected void setTextFieldEvents() {
		firstNameTextField.setOnMouseClicked(e -> {
			firstNameTextField.setStyle("-fx-text-box-border: lightgray;");
		});
		
		firstNameTextField.setOnKeyPressed(e -> {
			if(e.getCode().equals(KeyCode.TAB))
				firstNameTextField.setStyle("-fx-text-box-border: lightgray;");
			if(e.getCode().equals(KeyCode.ENTER)) {
				registerAccount(e);
			}
		});
		
		lastNameTextField.setOnMouseClicked(e -> {
			lastNameTextField.setStyle("-fx-text-box-border: lightgray;");
		});
		
		lastNameTextField.setOnKeyPressed(e -> {
			if(e.getCode().equals(KeyCode.TAB))
				lastNameTextField.setStyle("-fx-text-box-border: lightgray;");
			if(e.getCode().equals(KeyCode.ENTER)) {
				registerAccount(e);
			}
		});
		
		usernameTextField.setOnMouseClicked(e -> {
			usernameTextField.setStyle("-fx-text-box-border: lightgray;");
		});
		
		usernameTextField.setOnKeyPressed(e -> {
			if(e.getCode().equals(KeyCode.TAB))
				usernameTextField.setStyle("-fx-text-box-border: lightgray;");
			if(e.getCode().equals(KeyCode.ENTER)) {
				registerAccount(e);
			}
		});
		
		emailTextField.setOnMouseClicked(e -> {
			emailTextField.setStyle("-fx-text-box-border: lightgray;");
		});
		
		emailTextField.setOnKeyPressed(e -> {
			if(e.getCode().equals(KeyCode.TAB))
				emailTextField.setStyle("-fx-text-box-border: lightgray;");
			if(e.getCode().equals(KeyCode.ENTER)) {
				registerAccount(e);
			}
		});
		
		passwordTextField.setOnMouseClicked(e -> {
			passwordTextField.setStyle("-fx-text-box-border: lightgray;");
		});
		
		passwordTextField.setOnKeyPressed(e -> {
			if(e.getCode().equals(KeyCode.TAB))
				passwordTextField.setStyle("-fx-text-box-border: lightgray;");
			if(e.getCode().equals(KeyCode.ENTER)) {
				registerAccount(e);
			}
		});
		
		passwordTextField.setOnKeyReleased(e -> {
			String password = passwordTextField.getText().toString();
			if(password != null) {
				checkRequirement(password);
			}
		});
		
		rePasswordTextField.setOnMouseClicked(e -> {
			rePasswordTextField.setStyle("-fx-text-box-border: lightgray;");
		});
		
		rePasswordTextField.setOnKeyPressed(e -> {
			if(e.getCode().equals(KeyCode.TAB))
				rePasswordTextField.setStyle("-fx-text-box-border: lightgray;");
			if(e.getCode().equals(KeyCode.ENTER)) {
				registerAccount(e);
			}
		});
		
		securityQuestionTextField.setOnMouseClicked(e -> {
			securityQuestionTextField.setStyle("-fx-text-box-border: lightgray;");
		});
		
		securityQuestionTextField.setOnKeyPressed(e -> {
			if(e.getCode().equals(KeyCode.TAB))
				securityQuestionTextField.setStyle("-fx-text-box-border: lightgray;");
			if(e.getCode().equals(KeyCode.ENTER)) {
				registerAccount(e);
			}
		});
		
		securityAnswerTextField.setOnMouseClicked(e -> {
			securityAnswerTextField.setStyle("-fx-text-box-border: lightgray;");
		});
		
		securityAnswerTextField.setOnKeyPressed(e -> {
			if(e.getCode().equals(KeyCode.TAB))
				securityAnswerTextField.setStyle("-fx-text-box-border: lightgray;");
			if(e.getCode().equals(KeyCode.ENTER)) {
				registerAccount(e);
			}
		});
	}
	
}
