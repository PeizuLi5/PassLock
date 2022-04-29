package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
		Statement create = null;
		PreparedStatement checkExists = null;
		ResultSet table = null;
		ResultSet result = null;
		
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/team-02-07", "peizuli", "CS151termproject@");
			//create a table if the table does not exists
			table = connection.getMetaData().getTables(null, null, "users", new String[] {"TABLE"});
			if(table.next() == false) {
				create = connection.createStatement();
				String sql = "CREATE TABLE users ("
						+ "username VARCHAR(40) NOT NULL, "
						+ "email VARCHAR(255) NOT NULL, "
						+ "first_name VARCHAR(45) NOT NULL, "
						+ "last_name VARCHAR(45) NOT NULL, "
						+ "password VARCHAR(40) NOT NULL, "
						+ "security_question VARCHAR(255) NOT NULL, "
						+ "security_answer VARCHAR(255) NOT NULL, "
						+ "PRIMARY KEY (username), "
						+ "UNIQUE INDEX username_UNIQUE (username ASC) VISIBLE, "
						+ "UNIQUE INDEX email_UNIQUE (email ASC) VISIBLE)";
				create.executeUpdate(sql);
			}
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
			e1.printStackTrace();
		}
		//close all the preparedStatements, result set, and connection
		finally {
			if(result != null) {
				try {
					result.close();
				}
				catch(SQLException exception) {
					exception.printStackTrace();
				}
			}
			if(table != null) {
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
			if(create != null) {
				try {
					create.close();
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

	public static void loginAccount(Event e, String username, String password) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		String dBPassword = "";
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/team-02-07", "peizuli", "CS151termproject@");
			preparedStatement = connection.prepareStatement("SELECT password FROM users WHERE username = ?");
			preparedStatement.setString(1, username);
			result = preparedStatement.executeQuery();
			
			if(!result.isBeforeFirst()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("User not found. Please try again.");
				alert.show();
			}
			else {
				while(result.next()) {
					dBPassword = result.getString("password");
				}
				if(password.equals(dBPassword)) {
					changeScene(e, "Main", "view/MainPage.fxml");
				}
				else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setContentText("Password does not match. Please try again.");
					alert.show();
				}
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		finally {
			if(result != null) {
				try {
					result.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
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
		stage.centerOnScreen();
	}
	
	public static void changeScene(MouseEvent e, String title, String file) throws IOException {
		Parent root = FXMLLoader.load(DBUtils.class.getClassLoader().getResource(file));
		Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setTitle(title);
		stage.setScene(scene);
		stage.show();
		stage.centerOnScreen();
	}
}
