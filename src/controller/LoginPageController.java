package controller;
import java.awt.Button;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.DBUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class LoginPageController implements Initializable{
	
	@FXML 
	private Button LoginButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		 LoginButton.setOnMouseClicked(e -> {
			try {
				DBUtils.changeScene(e, "Login", "view/LoginPage.fxml");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});;
		
	}
	

	
	
}
