package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.DBUtils;
import application.SceneChangingUtils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

public class ResetPassword_Page_1_Controller implements Initializable {

	@FXML
	private ImageView passLockImageView;

	@FXML
	private Button signUpButton;

	@FXML
	private Button logInButton;

	@FXML
	private Button resetPasswordButton;

	@FXML
	private TextField emailTextField;

	@FXML
	private Alert alert = new Alert(AlertType.NONE);

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

		// Initialize text fields events
		setTextFieldEvents();

		// go registration page when the sign up button is clicked
		signUpButton.setOnMouseClicked(e -> {
			try {
				SceneChangingUtils.changeScene(e, "Sign Up", "view/RegisterPage.fxml");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		// back to main page if the PassLock icon is clicked
		passLockImageView.setOnMouseClicked(e -> {
			try {
				SceneChangingUtils.changeScene(e, "Home", "view/HomePage.fxml");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		// go to login page if the login button is clicked
		logInButton.setOnMouseClicked(e -> {
			try {
				SceneChangingUtils.changeScene(e, "Login", "view/LoginPage.fxml");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		// check the email if the reset password button is being clicked
		resetPasswordButton.setOnMouseClicked(e -> {
			email_check(e);
		});
	}

	/**
	 * Handles Text field events, call the email_check method when user press enter.
	 */
	private void setTextFieldEvents() {
		// change border color to normal
		emailTextField.setOnMouseClicked(e -> {
			emailTextField.setStyle("-fx-border-color: lightgray;");
		});

		// start the next process when user press enter
		emailTextField.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.TAB)) {
				emailTextField.setStyle("-fx-border-color: lightgray;");
			}
			if (e.getCode().equals(KeyCode.ENTER)) {
				email_check(e);
			}
		});
	}

	/**
	 * Check email from database.
	 * 
	 * @param e - javaFX event
	 */
	protected void email_check(Event e) {
		// give error for empty text field
		String email = emailTextField.getText().toString();
		if (hasEmptyField()) {
			alert.setAlertType(AlertType.ERROR);
			alert = printAlertMessage(alert);
			alert.show();
		}
		// go to check_email method in DBUtils class
		else {
			DBUtils.check_email(e, email);
		}
	}

	/**
	 * Print the error message and change the text field border color to red.
	 * 
	 * @param alert - alert object for message
	 * @return new alert message
	 */
	private Alert printAlertMessage(Alert alert) {
		String message = "Please enter your email.";
		emailTextField.setStyle("-fx-border-color: RED;");
		alert.setContentText(message);
		return alert;
	}

	/**
	 * Return true if there is an empty field.
	 * 
	 * @return true for empty field.
	 */
	protected boolean hasEmptyField() {
		if (!emailTextField.getText().trim().isEmpty()) {
			return false;
		}
		return true;
	}
}
