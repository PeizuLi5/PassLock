package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.DBUtils;
import application.SceneChangingUtils;
import javafx.fxml.FXML;
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.fxml.Initializable;

public class ResetPassword_Page_2_Controller implements Initializable {

	@FXML
	private ImageView passLockImageView;

	@FXML
	private Button signUpButton;

	@FXML
	private Button logInButton;

	@FXML
	private Button submitButton;

	@FXML
	private Label questionLabel;

	@FXML
	private TextField answerTextField;

	@FXML
	private Button aboutPassLockButton;

	private String email = new String();

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
			checkSecurityQuestion(e, email);
		});
	}

	/**
	 * Check user's answer.
	 * 
	 * @param e     - javaFX event
	 * @param email - user to check security question for
	 */
	protected void checkSecurityQuestion(Event e, String email) {
		String answer = answerTextField.getText().toString();
		if (hasEmptyTextField()) {
			alert.setAlertType(AlertType.ERROR);
			alert.setContentText("Please answer the question.");
			alert.show();
		} else if (validateAnswer(e, answer, email) == false) {
			alert.setAlertType(AlertType.ERROR);
			alert.setContentText("Incorrect answer");
			answerTextField.setStyle("-fx-text-box-border: #FF0000;");
			alert.show();
		} else {
			try {
				SceneChangingUtils.toLastReset(e, "Reset Password", email);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	/**
	 * Validate user answer with database.
	 * 
	 * @param e      - javaFX event
	 * @param answer - answer to check
	 * @param email  - user to check answer for
	 * @return true if answer is same as database
	 */
	protected boolean validateAnswer(Event e, String answer, String email) {
		if (answer.equalsIgnoreCase(DBUtils.getAnswer(email)))
			return true;
		return false;
	}

	/**
	 * Check if there exists a empty text field.
	 * 
	 * @return true if there is an empty text field
	 */
	protected boolean hasEmptyTextField() {
		if (!answerTextField.getText().trim().isEmpty())
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
		if (answerTextField.getText().trim().isEmpty()) {
			message += "Missing first name.\n";
			answerTextField.setStyle("-fx-text-box-border: #FF0000;");
		}
		alert.setContentText(message);
		return alert;

	}

	/**
	 * Handles Text field events, call the checkSecurityQuestion method when user
	 * press enter.
	 */
	private void setTextFieldEvents() {
		answerTextField.setOnMouseClicked(e -> {
			answerTextField.setStyle("-fx-text-box-border: lightgray;");
		});

		answerTextField.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.TAB))
				answerTextField.setStyle("-fx-text-box-border: lightgray;");
			if (e.getCode().equals(KeyCode.ENTER)) {
				checkSecurityQuestion(e, email);
			}
		});

	}

	/**
	 * Get and set question from database.
	 * 
	 * @param email2 - user to get question for
	 */
	public void setQuestion(String email2) {
		String question = DBUtils.getQuestion(email);
		questionLabel.setText(question);
	}
}
