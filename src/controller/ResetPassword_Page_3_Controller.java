package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.AccountRequirementsUtils;
import application.DBUtils;
import application.SceneChangingUtils;
import edu.sjsu.yazdankhah.crypto.util.PassUtil;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

public class ResetPassword_Page_3_Controller implements Initializable {
	@FXML
	private ImageView passLockImageView;

	@FXML
	private Button signUpButton;

	@FXML
	private Button logInButton;

	@FXML
	private Button submitButton;

	@FXML
	private PasswordField newPasswordField;

	@FXML
	private PasswordField confirmPasswordField;

	@FXML
	private Button aboutPassLocButton;

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

	private String email = "";

	private Alert alert = new Alert(AlertType.NONE);

	public void setEmail(String email2) {
		email = email2;
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
		setTextFieldEvents();

		signUpButton.setOnMouseClicked(e -> {
			try {
				SceneChangingUtils.changeScene(e, "Sign Up", "view/RegisterPage.fxml");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		logInButton.setOnMouseClicked(e -> {
			try {
				SceneChangingUtils.changeScene(e, "Login", "view/LoginPage.fxml");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		passLockImageView.setOnMouseClicked(e -> {
			try {
				SceneChangingUtils.changeScene(e, "Home", "view/HomePage.fxml");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		submitButton.setOnMouseClicked(e -> {
			checkPassword(e, email);
		});
	}

	/**
	 * Check user's inputed password.
	 * 
	 * @param e     - javaFX event
	 * @param email - user to check password for
	 */
	public void checkPassword(Event e, String email) {
		String newPassword = newPasswordField.getText().toString();
		String confirmPassword = confirmPasswordField.getText().toString();
		
		if (hasEmptyTextField()) {
			alert.setAlertType(AlertType.ERROR);
			alert = printErrorMessage(alert);
			alert.show();
		} 
		else if (checkRequirement(newPassword) == false) {
			alert.setAlertType(AlertType.ERROR);
			alert.setContentText(
					"Please enter a valid password containing: 1 Uppercase, 1 Lowercase, 1 Special character, and a number.");
			newPasswordField.setStyle("-fx-text-box-border: #FF0000;");
			alert.show();
		}
		else if (newPassword.equals(confirmPassword) == false) {
			alert.setAlertType(AlertType.ERROR);
			alert.setContentText("Password and re-entered password do not match.");
			newPasswordField.setStyle("-fx-text-box-border: #FF0000;");
			confirmPasswordField.setStyle("-fx-text-box-border: #FF0000;");
			alert.show();
		} else {
			String newPasswordEncrypted = new PassUtil().encrypt(newPassword);
			DBUtils.changePassword(e, newPasswordEncrypted, email);
		}
	}

	/**
	 * Check if there exists a empty text field.
	 * 
	 * @return true if there is an empty text field
	 */
	protected boolean hasEmptyTextField() {
		if (!newPasswordField.getText().trim().isEmpty() && !confirmPasswordField.getText().trim().isEmpty())
			return false;
		return true;
	}

	/**
	 * Print alert message and change the empty text field border to red.
	 * 
	 * @param alert - alert object for message
	 * @return new alert message
	 */
	protected Alert printErrorMessage(Alert alert) {
		String message = "";
		if (newPasswordField.getText().trim().isEmpty()) {
			message += "Missing password.\n";
			newPasswordField.setStyle("-fx-text-box-border: #FF0000;");
		}
		if (confirmPasswordField.getText().trim().isEmpty()) {
			message += "Missing confirmation.\n";
			confirmPasswordField.setStyle("-fx-text-box-border: #FF0000;");
		}
		alert.setContentText(message);
		return alert;
	}

	/**
	 * Change the label color to green if it meets the requirement change the label
	 * color to red if it does not meet the requirement
	 */

	protected boolean checkRequirement(String password) {
		if (AccountRequirementsUtils.hasUpperCase(password)) {
			upperLetterLabel.setTextFill(Color.GREEN);
		} else {
			upperLetterLabel.setTextFill(Color.RED);
		}

		if (AccountRequirementsUtils.hasLowerCase(password)) {
			lowerLetterLabel.setTextFill(Color.GREEN);
		} else {
			lowerLetterLabel.setTextFill(Color.RED);
		}

		if (AccountRequirementsUtils.hasSpecial(password)) {
			specialLetterLabel.setTextFill(Color.GREEN);
		} else {
			specialLetterLabel.setTextFill(Color.RED);
		}

		if (AccountRequirementsUtils.hasNumber(password)) {
			numberLabel.setTextFill(Color.GREEN);
		} else {
			numberLabel.setTextFill(Color.RED);
		}

		if (password.length() >= 8) {
			minLengthLabel.setTextFill(Color.GREEN);
		} else {
			minLengthLabel.setTextFill(Color.RED);
		}
		return (AccountRequirementsUtils.hasUpperCase(password) && AccountRequirementsUtils.hasLowerCase(password)
				&& AccountRequirementsUtils.hasSpecial(password) && AccountRequirementsUtils.hasNumber(password)
				&& password.length() >= 8);
	}

	/**
	 * Handles Text field events, calls checkPassword, checkRequirement on enter for
	 * respective field.
	 */
	protected void setTextFieldEvents() {

		newPasswordField.setOnMouseClicked(e -> {
			newPasswordField.setStyle("-fx-text-box-border: lightgray;");
		});

		newPasswordField.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.TAB))
				newPasswordField.setStyle("-fx-text-box-border: lightgray;");
			if (e.getCode().equals(KeyCode.ENTER)) {
				checkPassword(e, email);
			}
		});

		newPasswordField.setOnKeyReleased(e -> {
			String password = newPasswordField.getText().toString();
			if (password != null) {
				checkRequirement(password);
			}
		});

		confirmPasswordField.setOnMouseClicked(e -> {
			confirmPasswordField.setStyle("-fx-text-box-border: lightgray;");
		});

		confirmPasswordField.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.TAB))
				confirmPasswordField.setStyle("-fx-text-box-border: lightgray;");
			if (e.getCode().equals(KeyCode.ENTER)) {
				checkPassword(e, email);
			}
		});

	}
}
