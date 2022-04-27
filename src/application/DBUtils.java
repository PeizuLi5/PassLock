package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class DBUtils{
	
	public static void registerAccount(Event e, String firstname, String lastname, String username, 
			String email, String password, String securityQuestion, String securityAnswer) {
		Connection connection = null;
		PreparedStatement insert = null;
		PreparedStatement checkExists = null;
		ResultSet result = null;
		
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/team-02-07", "peizuli", "CS151termproject@");
			checkExists = connection.prepareStatement("SELECT * FROM users WHERE username = ? OR email = ?");
			checkExists.setString(1, username);
			checkExists.setString(2, email);	
			result = checkExists.executeQuery();
			
			if(result.isBeforeFirst()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("Username or email already exists! Please try another username.");
				alert.show();
			}
			else {
				insert = connection.prepareStatement("INSERT INTO users (username, email, first_name, last_name, password, security_question, security_answer) "
							+ "VALUES (?, ?, ?, ?, ?, ?, ?)");
				insert.setString(1, username);
				insert.setString(2, email);
				insert.setString(3, firstname);
				insert.setString(4, lastname);
				insert.setString(5, password);
				insert.setString(6, securityQuestion);
				insert.setString(7, securityAnswer);
				insert.executeUpdate();
				
				changeScene(e, "Login", "view/LoginPage.fxml");
			}
		}
		catch(SQLException exception) {
			exception.printStackTrace();
		} 
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		finally {
			if(result != null) {
				try {
					result.close();
				}
				catch(SQLException exception) {
					exception.printStackTrace();
				}
			}
			if(checkExists != null) {
				try {
					checkExists.close();
				}
				catch(SQLException exception) {
					exception.printStackTrace();
				}
			}
			if(insert != null) {
				try {
					insert.close();
				}
				catch(SQLException exception) {
					exception.printStackTrace();
				}
			}
			if(connection != null) {
				try {
					connection.close();
				}
				catch(SQLException exception) {
					exception.printStackTrace();
				}
			}
		}
		
	}

	public static void changeScene(Event e, String title, String file) throws IOException {
		Parent root = FXMLLoader.load(DBUtils.class.getClassLoader().getResource(file));
		Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setTitle(title);
		stage.setScene(scene);
		stage.show();
	}
	
	public static void changeScene(MouseEvent e, String title, String file) throws IOException {
		Parent root = FXMLLoader.load(DBUtils.class.getClassLoader().getResource(file));
		Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setTitle(title);
		stage.setScene(scene);
		stage.show();
		
	}
}
