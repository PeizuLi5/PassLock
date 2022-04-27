package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.DBUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

public class HomePageController implements Initializable{

    @FXML
    private Button loginButton;

    @FXML
    private Button signUpButton;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		signUpButton.setOnMouseClicked(e -> {
			try {
				DBUtils.changeScene(e, "Sign Up", "view/RegisterPage.fxml");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});;
		
	}

}

