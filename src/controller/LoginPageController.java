package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.DBUtils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

//TODO: Create a checkExpirationDate Method that gives user warning when a password get expired.
public class LoginPageController implements Initializable{

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordPasswordField;

    @FXML
    private TextField usernameTextField;

    @FXML
    private Button forgetPasswordButton;

    @FXML
    private Button signupButton;
    
    @FXML
    private ImageView passLockImage;

    private Alert alert = new Alert(AlertType.NONE);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setTextFieldEvents();
		
		signupButton.setOnMouseClicked(e -> {
			try {
				DBUtils.changeScene(e, "Sign Up", "view/RegisterPage.fxml");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		
		passLockImage.setOnMouseClicked(e -> {
			try {
				DBUtils.changeScene(e, "Home", "view/HomePage.fxml");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		
		loginButton.setOnMouseClicked(e -> {
			login(e);
		});
	}

	private void setTextFieldEvents() {
		usernameTextField.setOnMouseClicked(e -> {
			usernameTextField.setStyle("-fx-text-box-border: lightgray;");
		});
		
		usernameTextField.setOnKeyPressed(e -> {
			if(e.getCode().equals(KeyCode.TAB))
				usernameTextField.setStyle("-fx-text-box-border: lightgray;");
			if(e.getCode().equals(KeyCode.ENTER)) {
				login(e);
			}
		});
		
		passwordPasswordField.setOnMouseClicked(e -> {
			passwordPasswordField.setStyle("-fx-text-box-border: lightgray;");
		});
		
		passwordPasswordField.setOnKeyPressed(e -> {
			if(e.getCode().equals(KeyCode.TAB))
				passwordPasswordField.setStyle("-fx-text-box-border: lightgray;");
			if(e.getCode().equals(KeyCode.ENTER)) {
				login(e);
			}
		});
	}

	protected void login(Event e) {
		String username = usernameTextField.getText().toString();
		String password = passwordPasswordField.getText().toString();
		if(hasEmptyField()) {
			alert.setAlertType(AlertType.ERROR);
			alert = printAlertMessage(alert);
			alert.show();
		}
		else {
			DBUtils.loginAccount(e, username, password);
		}
	}

	private Alert printAlertMessage(Alert alert) {
		String message = "Please fill all the text fields.";
		usernameTextField.setStyle("-fx-text-box-border: #FF0000;");
		passwordPasswordField.setStyle("-fx-text-box-border: #FF0000;");
		alert.setContentText(message);
		return alert;
	}

	//return true if there is an empty field.
	protected boolean hasEmptyField() {
		if(!usernameTextField.getText().trim().isEmpty() &&
	       !passwordPasswordField.getText().trim().isEmpty()) {
			return false;
		}
		return true;
	}
	
	

}
