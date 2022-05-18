package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.SceneChangingUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

public class HomePageController implements Initializable {

	@FXML
	private Button loginButton;

	@FXML
	private Button signUpButton;

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
		signUpButton.setOnMouseClicked(e -> {
			try {
				SceneChangingUtils.changeScene(e, "Sign Up", "view/RegisterPage.fxml");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		loginButton.setOnMouseClicked(e -> {
			try {
				SceneChangingUtils.changeScene(e, "Login", "view/LoginPage.fxml");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

	}

}
