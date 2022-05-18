package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import edu.sjsu.yazdankhah.crypto.util.PassUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class DBUtils {

	/**
	 * Handles all SQLite connections, creates database if missing.
	 * 
	 * @return connection object
	 */
	private static Connection getConnection() {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:Passlock.sqlite");
			return conn;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	/**
	 * Register the account with inputed user data.
	 * 
	 * @param e                - javaFX event
	 * @param firstname        - user's first name
	 * @param lastname         - user's last name
	 * @param username         - username for account
	 * @param email            - user's email
	 * @param password         - account's password
	 * @param securityQuestion - user's security question
	 * @param securityAnswer   - user's security answer
	 * @exception SQLException
	 * @exception IOException
	 */
	public static void registerAccount(Event e, String firstname, String lastname, String username, String email,
			String password, String securityQuestion, String securityAnswer) {
		// initialize those connections and statements
		Connection connection = null;
		PreparedStatement insert = null;
		Statement create = null;
		Statement passwordTable = null;
		PreparedStatement createPasswordRequire = null;
		PreparedStatement checkExists = null;
		ResultSet result = null;

		try {
			// try to connect to the database, which user and password should be unique
			connection = getConnection();

			create = connection.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS users(" + "username VARCHAR(40) NOT NULL UNIQUE,"
					+ "    email VARCHAR(255) NOT NULL UNIQUE," + "    first_name VARCHAR(45) NOT NULL,"
					+ "    last_name VARCHAR(45) NOT NULL," + "    password VARCHAR(40) NOT NULL,"
					+ "    security_question VARCHAR(255) NOT NULL," + "    security_answer VARCHAR(255) NOT NULL,"
					+ "    PRIMARY KEY (username))";
			create.executeUpdate(sql);

			// check if there exists an account with the same username or email
			checkExists = connection.prepareStatement("SELECT * FROM users WHERE username = ? OR email = ?");
			checkExists.setString(1, username);
			checkExists.setString(2, email);
			result = checkExists.executeQuery();

			// gives error message if exist either one
			if (result.isBeforeFirst()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("Username or email already exists! Please try another username.");
				alert.show();
			}
			// insert all datas to the database for new user
			else {
				insert = connection.prepareStatement(
						"INSERT INTO users (username, email, first_name, last_name, password, security_question, security_answer) "
								+ "VALUES (?, ?, ?, ?, ?, ?, ?)");
				insert.setString(1, username);
				insert.setString(2, email);
				insert.setString(3, firstname);
				insert.setString(4, lastname);
				insert.setString(5, password);
				insert.setString(6, securityQuestion);
				insert.setString(7, securityAnswer);
				insert.executeUpdate();

				passwordTable = connection.createStatement();
				String tableSql = "CREATE TABLE IF NOT EXISTS passwords(" + "user VARCHAR(40) NOT NULL UNIQUE,"
						+ "    has_capital INTEGER NOT NULL," + "    has_lowercase INTEGER NOT NULL,"
						+ "    has_special INTEGER NOT NULL," + "    has_number INTEGER NOT NULL,"
						+ "    length INTEGER NOT NULL,"
						+ "    PRIMARY KEY (user))";
				passwordTable.executeUpdate(tableSql);
				
				createPasswordRequire = connection.prepareStatement(
						"INSERT INTO passwords (user, has_capital, has_lowercase, has_special, has_number, length) "
						+ "VALUES (?, ?, ?, ?, ?, ?)");
				createPasswordRequire.setString(1, username);
				createPasswordRequire.setInt(2, 1);
				createPasswordRequire.setInt(3, 1);
				createPasswordRequire.setInt(4, 1);
				createPasswordRequire.setInt(5, 1);
				createPasswordRequire.setInt(6, 8);
				createPasswordRequire.executeUpdate();
				
				// change scene to Login
				SceneChangingUtils.changeScene(e, "Login", "view/LoginPage.fxml");
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// close all the preparedStatements, result set, and connection
		finally {
			if (result != null) {
				try {
					result.close();
				} catch (SQLException exception) {
					exception.printStackTrace();
				}
			}
			if(createPasswordRequire != null) {
				try {
					createPasswordRequire.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (checkExists != null) {
				try {
					checkExists.close();
				} catch (SQLException exception) {
					exception.printStackTrace();
				}
			}
			if (insert != null) {
				try {
					insert.close();
				} catch (SQLException exception) {
					exception.printStackTrace();
				}
			}
			if (create != null) {
				try {
					create.close();
				} catch (SQLException exception) {
					exception.printStackTrace();
				}
			}
			if(passwordTable != null) {
				try {
					passwordTable.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException exception) {
					exception.printStackTrace();
				}
			}
		}

	}

	/**
	 * Login to the account with inputed username and password.
	 * 
	 * @param e        - javaFX event
	 * @param username - inputed username
	 * @param password - inputed password
	 */
	public static void loginAccount(Event e, String username, String password) {
		// Initialization
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		Statement create = null;
		ResultSet result = null;
		String dBPassword = "";
		String passwordDecrypted = "";
		String user = new String();
		PassUtil passUtil = new PassUtil();
		try {
			// connect to database
			connection = getConnection();

			// create database if missing
			create = connection.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS users(" + "username VARCHAR(40) NOT NULL UNIQUE,"
					+ "    email VARCHAR(255) NOT NULL UNIQUE," + "    first_name VARCHAR(45) NOT NULL,"
					+ "    last_name VARCHAR(45) NOT NULL," + "    password VARCHAR(40) NOT NULL,"
					+ "    security_question VARCHAR(255) NOT NULL," + "    security_answer VARCHAR(255) NOT NULL,"
					+ "    PRIMARY KEY (username))";
			create.executeUpdate(sql);
			
			// find exists user
			preparedStatement = connection.prepareStatement("SELECT username, password FROM users WHERE username = ?");
			preparedStatement.setString(1, username);
			result = preparedStatement.executeQuery();

			// if no user exists give error
			if (!result.isBeforeFirst()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("User not found. Please try again.");
				alert.show();
			}
			// if user exists
			else {
				while (result.next()) {
					dBPassword = result.getString("password");
					passwordDecrypted = passUtil.decrypt(dBPassword);
					user = result.getString("username");
				}
				// check the password is the same as what it being stored in database
				if (password.equals(passwordDecrypted)) {
					SceneChangingUtils.toMainPage(e, "Welcome " + user, user);
					checkExpiredPassword(connection, user);
				}
				// if not give error
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
		// close all database related variables
		finally {
			if (result != null) {
				try {
					result.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (create != null) {
				try {
					create.close();
				} catch (SQLException exception) {
					exception.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	/**
	 * Checks if users has any passwords that have expired.
	 * 
	 * @param connection - database connection
	 * @param user       - user to check
	 */
	private static void checkExpiredPassword(Connection connection, String user) {
		PreparedStatement preparedStatement = null;
		ResultSet table = null;

		try {
			preparedStatement = connection
					.prepareStatement("SELECT website_name FROM accounts where user = ? AND expiration_date <= ?");
			preparedStatement.setString(1, user);
			Date today = Date.valueOf(LocalDate.now());
			preparedStatement.setDate(2, today);
			table = preparedStatement.executeQuery();

			if (table.isBeforeFirst()) {
				String warning = "You have those expired passwords: \n";
				Alert alert = new Alert(AlertType.WARNING);
				while (table.next()) {
					warning += "   " + table.getString("website_name") + "\n";
				}
				alert.setContentText(warning);
				alert.show();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (table != null) {
				try {
					table.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * Add information into account with inputed data.
	 * 
	 * @param e           - javaFX event
	 * @param author      - Current user
	 * @param websiteName - website's name
	 * @param url         - website's url
	 * @param email       - email for website
	 * @param username    - website's username
	 * @param password    - password for website
	 * @param creation    - date created password
	 * @param expiration  - date password will expire
	 */
	public static void addInfo(Event e, String author, String websiteName, String url, String email, String username,
			String password, Date creation, Date expiration) {
		// initialzation
		Connection connection = null;
		PreparedStatement insert = null;

		try {
			// connection
			connection = getConnection();

			// insert information
			insert = connection.prepareStatement(
					"INSERT INTO accounts (user, website_name, url, email, username, password, creation_date, expiration_date) "
							+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			insert.setString(1, author);
			insert.setString(2, websiteName);
			insert.setString(3, url);
			insert.setString(4, email);
			insert.setString(5, username);
			insert.setString(6, password);
			insert.setDate(7, creation);
			insert.setDate(8, expiration);
			insert.executeUpdate();

			// go to main page, and pass out username of the user for later usage
			SceneChangingUtils.toMainPage(e, "Welcome " + author, author);
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// close database
		finally {
			if (insert != null) {
				try {
					insert.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	/**
	 * Display a list of websites when the user create those informations.
	 * 
	 * @param user - current user
	 * @return list of websites
	 */
	public static ObservableList<Account> showList(String user) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		ResultSet table = null;
		Statement create = null;// fix
		ObservableList<Account> list = FXCollections.observableArrayList ();//was ArrayList

		try {
			connection = getConnection();
			// create a table if the table does not exists
			table = connection.getMetaData().getTables(null, null, "accounts", new String[] { "TABLE" });
			if (table.next() == false) {
				create = connection.createStatement();
				String sql = "CREATE TABLE accounts (" + "ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, "
						+ "user VARCHAR(40) NOT NULL, " + "website_name VARCHAR(45) NOT NULL, "
						+ "url VARCHAR(45) NOT NULL, " + "email VARCHAR(255) NULL, " + "username VARCHAR(45) NULL, "
						+ "password VARCHAR(45) NOT NULL, " + "creation_date DATE NOT NULL, "
						+ "expiration_date DATE NOT NULL)";
				create.executeUpdate(sql);
			} else {
				preparedStatement = connection.prepareStatement("SELECT * FROM accounts WHERE user = ?");
				preparedStatement.setString(1, user);
				result = preparedStatement.executeQuery();
				while (result.next()) {
					String website_name = result.getString("website_name");
					String url = result.getString("url");
					String username = result.getString("username");
					String email = result.getString("email");
					String password = result.getString("password");
					PassUtil pass = new PassUtil();
					String passwordDecrypted = pass.decrypt(password);

					if (username == null) {
						username = "";
					}
					if (email == null) {
						email = "";
					}

					Date creation = result.getDate("creation_date");
					Date expiration = result.getDate("expiration_date");
					String creation_date = creation.toString();
					String expiration_date = expiration.toString();
					Account account = new Account(website_name, url, username, email, passwordDecrypted, password, creation_date,
							expiration_date);
					list.add(account);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (table != null) {
				try {
					table.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (create != null) {
				try {
					create.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	/**
	 * Get security question when resetting the password.
	 * 
	 * @param email - user's email to check
	 * @return security question
	 */
	public static String getQuestion(String email) {
		// init
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		String userQuestion = new String();
		
		try {
			// connect to database
			connection = getConnection();
			// get the question based on the email
			preparedStatement = connection.prepareStatement("SELECT security_question FROM users WHERE email = ?");
			preparedStatement.setString(1, email);
			result = preparedStatement.executeQuery();
			while (result.next()) {
				userQuestion = result.getString("security_question");
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		// close database
		finally {
			if (result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return userQuestion;
	}

	/**
	 * Get the security answer for checking.
	 * 
	 * @param email - email of user to check
	 * @return security answer
	 */
	public static String getAnswer(String email) {
		// init
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		String userAnswer = "";
		
		try {
			// connect to database
			connection = getConnection();
			// get the answer from database based on email
			preparedStatement = connection.prepareStatement("SELECT security_answer FROM users WHERE email = ?");
			preparedStatement.setString(1, email);
			result = preparedStatement.executeQuery();
			while (result.next()) {
				userAnswer = result.getString("security_answer");
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		// close the database
		finally {
			if (result != null) {
				try {
					result.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		// return the answer for checking
		return userAnswer;
	}

	/**
	 * Check if email exists in database.
	 * 
	 * @param e     - javaFX event
	 * @param email - email to check in database
	 */
	public static void check_email(Event e, String email) {
		// init
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		
		try {
			// connect to database
			connection = getConnection();
			// find if the email exists
			preparedStatement = connection.prepareStatement("SELECT email FROM users WHERE email = ?");
			preparedStatement.setString(1, email);
			result = preparedStatement.executeQuery();

			// give error when not exists
			if (!result.isBeforeFirst()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("Email not found. Please try again.");
				alert.show();
			}
			// go to next scene when exists, and keep the data of the email for later usage
			else {
				SceneChangingUtils.toNextReset(e, "Security Question Checking", email);
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// close the database
		finally {
			if (result != null) {
				try {
					result.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	/**
	 * Updates password when user goes to reset.
	 * 
	 * @param e2          - javaFX event
	 * @param newPassword - password to replace old
	 * @param email       - user's email to check database
	 */
	public static void changePassword(Event e2, String newPassword, String email) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement("UPDATE users SET password = ? WHERE email = ?");
			preparedStatement.setString(1, newPassword);
			preparedStatement.setString(2, email);
			preparedStatement.executeUpdate();

			SceneChangingUtils.changeScene(e2, "Home", "view/HomePage.fxml");

		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * delete the account from database and from display the new list
	 * 
	 * @param e				- javaFX event
	 * @param author		- user that modify delete
	 * @param websiteName	- the name of the website
	 * @param url			- the url of the website
	 * @param password		- the password of the website
	 * @param creation		- the creation date of the website
	 * @param expiration	- the expiration date of the website
	 */
	public static void delete(Event e, String author, String websiteName,String url, String password, Date creation, Date expiration) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;       
        
        try {
            connection = getConnection();
            
            preparedStatement = connection.prepareStatement("DELETE FROM accounts WHERE user = ? AND website_name = ? "
                    + "AND url = ? "
                    + "AND password = ? AND creation_date = ? AND expiration_date = ?" );
            preparedStatement.setString(1, author);
            preparedStatement.setString(2, websiteName);
            preparedStatement.setString(3, url);
            preparedStatement.setString(4, password);
            preparedStatement.setDate(5, creation);
            preparedStatement.setDate(6, expiration);

            preparedStatement.executeUpdate();

            SceneChangingUtils.toMainPage(e, "Welcome" + author, author);

        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e2) {
                    e2.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e3) {
                    e3.printStackTrace();
                }
            }
        }
    }
	
	/**
	 * Checks if user enter the correct master password.
	 * @param username - user to check
	 * @param password - input to be checked
	 * @return true if correct master password, false otherwise
	 */
	public static boolean viewPassword(String username, String password) {
		//init
		boolean isCorrect = false;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		Statement create = null;
		ResultSet table = null;
		ResultSet result = null;
		String dBPassword = "";

		try {
			// connect to database
			connection = getConnection();

			// find exists user
			preparedStatement = connection.prepareStatement("SELECT password FROM users WHERE username = ?");
			preparedStatement.setString(1, username);
			result = preparedStatement.executeQuery();

			// if no user exists give error
			if (!result.isBeforeFirst()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("User not found. Please try again.");
				alert.show();
			}
			// if user exists
			else {
				PassUtil passUtil = new PassUtil();
				while (result.next()) {
					dBPassword = passUtil.decrypt(result.getString("password"));
				}
				// check the password is the same as what it being stored in database
				if (password.equals(dBPassword)) {
					isCorrect = true;
				}
				// if not give error
				else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setContentText("Password does not match. Please try again.");
					alert.show();
				}
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		} 
		// close all database related variables
		finally {
			if (result != null) {
				try {
					result.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (table != null) {
				try {
					result.close();
				} catch (SQLException exception) {
					exception.printStackTrace();
				}
			}
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (create != null) {
				try {
					create.close();
				} catch (SQLException exception) {
					exception.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return isCorrect;
	}
	
	/**
	 * add or update the auto generate password requirements setting
	 * 
	 * @param author		- the user that modify setting
	 * @param hasCapital	- check box that represent contains capital letter or not
	 * @param hasLowercase	- check box that represent contains lower case letter or not
	 * @param hasSpecial	- check box that represent contains special letter or not
	 * @param hasNumber		- check box that represent contains number or not
	 * @param length		- length of the password
	 */
	public static void generatePasswordRequirement(String author, boolean hasCapital, boolean hasLowercase,
			boolean hasSpecial, boolean hasNumber, int length) {
		//init
		Connection connection = null;
		PreparedStatement update = null;
		ResultSet result = null;
		
		try {
			connection = getConnection();
			
			update = connection.prepareStatement("UPDATE passwords SET has_capital = ?, " 
					+ "has_lowercase = ?, " + "has_special = ?, " + "has_number = ?, " + "length = ? "
					+ "WHERE user = ?");
			
			if(hasCapital == true)
				update.setInt(1, 1);
			else
				update.setInt(1, 0);
				
			if(hasLowercase == true)
				update.setInt(2, 1);
			else
				update.setInt(2, 0);
				
			if(hasSpecial == true)
				update.setInt(3, 1);
			else
				update.setInt(3, 0);
				
			if(hasNumber == true)
				update.setInt(4, 1);
			else
				update.setInt(4, 0);				
			update.setInt(5, length);
			update.setString(6, author);
			update.executeUpdate();
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			if(result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(update != null) {
				try {
					update.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	/**
	 * check if we need capital letter to generate password
	 * 
	 * @param author	- the user
	 * @return true if the check box in setting that says "has capital letter" is checked
	 */
	public static boolean getHasCapital(String author) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		boolean hasCapital = false;
			
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement("SELECT has_capital FROM passwords WHERE user = ?");
			preparedStatement.setString(1, author);
			result = preparedStatement.executeQuery();
			
			while(result.next()) {
				int capitcal = result.getInt("has_capital");
				if(capitcal == 1) {
					hasCapital = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if(result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hasCapital;
	}

	/**
	 * check if we need lower case letter to generate password
	 * 
	 * @param author	- the user
	 * @return true if the check box in setting that says "has lower case letter" is checked
	 */
	public static boolean getHasLowerCase(String author) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		boolean has_lowercase = false;
			
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement("SELECT has_lowercase FROM passwords WHERE user = ?");
			preparedStatement.setString(1, author);
			result = preparedStatement.executeQuery();
			
			while(result.next()) {
				int capitcal = result.getInt("has_lowercase");
				if(capitcal == 1) {
					has_lowercase = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if(result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return has_lowercase;
	}

	/**
	 * check if we need special character to generate password
	 * 
	 * @param author	- the user
	 * @return true if the check box in setting that says "has special character" is checked
	 */
	public static boolean getHasSpecialChar(String author) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		boolean has_special = false;
			
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement("SELECT has_special FROM passwords WHERE user = ?");
			preparedStatement.setString(1, author);
			result = preparedStatement.executeQuery();
			
			while(result.next()) {
				int capitcal = result.getInt("has_special");
				if(capitcal == 1) {
					has_special = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if(result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return has_special;
	}

	/**
	 * check if we need number to generate password
	 * 
	 * @param author	- the user
	 * @return true if the check box in setting that says "has number" is checked
	 */
	public static boolean getHasNumber(String author) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		boolean has_number = false;
			
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement("SELECT has_number FROM passwords WHERE user = ?");
			preparedStatement.setString(1, author);
			result = preparedStatement.executeQuery();
			
			while(result.next()) {
				int capitcal = result.getInt("has_number");
				if(capitcal == 1) {
					has_number = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if(result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return has_number;
	}

	/**
	 * search for the require length of the password
	 * 
	 * @param author	- the user
	 * @return the length that the user set.
	 */
	public static int getLength(String author) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		int length = 0;
			
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement("SELECT length FROM passwords WHERE user = ?");
			preparedStatement.setString(1, author);
			result = preparedStatement.executeQuery();
			
			while(result.next()) {
				length = result.getInt("length");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if(result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return length;
	}

	/**
	 * update the account information user select
	 * 
	 * @param e						- JavaFx event
	 * @param author				- user
	 * @param websiteName			- name of the website
	 * @param url					- url of the website
	 * @param email					- email user use for the account
	 * @param username				- username user use for the account
	 * @param passwordEncrypted		- encrypted password that is going to store to database
	 * @param creationDate			- new creation date
	 * @param expirationDate		- new expiration date
	 */
	public static void updateInfo(Event e, String author, String websiteName, String url, String email, String username,
			String passwordEncrypted, Date creationDate, Date expirationDate) {
		// initialzation
		Connection connection = null;
		PreparedStatement update = null;
		
		try {
			connection = getConnection();
			
			update = connection.prepareStatement("UPDATE accounts SET website_name = ?, url = ?, email = ?, username = ?, "
					+ "password = ?, creation_date = ?, expiration_date = ? WHERE user = ?");
			
			update.setString(1, websiteName);
			update.setString(2, url);
			update.setString(3, email);
			update.setString(4, username);
			update.setString(5, passwordEncrypted);
			update.setDate(6, creationDate);
			update.setDate(7, expirationDate);
			update.setString(8, author);
			
			update.executeUpdate();
			SceneChangingUtils.toMainPage(e, "Welcome " + author, author);
		} catch(SQLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		finally {
			if(update != null) {
				try {
					update.close();
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

	
}
