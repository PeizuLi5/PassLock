package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import application.Account;
import application.DBUtils;
import application.SceneChangingUtils;
import edu.sjsu.yazdankhah.crypto.util.PassUtil;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class ModifyPageController implements Initializable {

    @FXML
    private Label websiteNameLabel;

    @FXML
    private TextField websiteNameTextField;

    @FXML
    private Label urlLabel;

    @FXML
    private TextField urlTextField;

    @FXML
    private Label usernameLabel;

    @FXML
    private TextField usernameTextField;

    @FXML
    private Label emailLabel;

    @FXML
    private TextField emailTextField;

    @FXML
    private Label creationDateLabel;

    @FXML
    private TextField creationDateTextField;

    @FXML
    private Button backButton;

    @FXML
    private Button modifyButton;

    @FXML
    private Button todayDateButton;

    @FXML
    private Label expirationDateLabel;
    
    @FXML
    private DatePicker expirationDatePicker;

    @FXML
    private Label passwordLabel;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Button generatePasswordButton;

    @FXML
    private CheckBox hasCapitalCB;

    @FXML
    private CheckBox hasLowerCB;

    @FXML
    private CheckBox hasSpecialCB;

    @FXML
    private CheckBox hasDigitCB;

    @FXML
    private TextField lengthTextField;
    
    private String author = new String();
    private Account account;
    
    /**
     * setter for author
     * 
     * @param author	- user
     */
    public void setAuthor(String author) {
    	this.author = author;
    }
    
    /**
     * setter for account
     * 
     * @param account	- account that is selected by user
     */
    public void setAccount(Account account) {
    	this.account = new Account(account);
    }
    
    /**
     * getter for account
     * 
     * @return the account information user is selected
     */
    public Account getAccount() {
    	return account;
    }

    /**
     * The override method from Initializable interface that setup all default setting for the user
     */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initialLabels();
		initialTextFields();
		backButton.setOnMouseClicked(e -> {
			try {
				SceneChangingUtils.toMainPage(e, "Welcome " + author, author);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		
		todayDateButton.setOnMouseClicked(e -> {
			Date today = Date.valueOf(LocalDate.now());
			creationDateTextField.setText(today.toString());
		});
		
		generatePasswordButton.setOnMouseClicked(e -> {
			String password = generatePassword();
            if(password != null) {
            	passwordTextField.setText(password);
            }
		});
		
		modifyButton.setOnMouseClicked(e -> {
			updateAccount(e);
		});
	}

	/**
	 * gather information user enter in the ModifyPage, then change the account based on what user is entered
	 * 
	 * @param e		- JavaFX event
	 */
	private void updateAccount(Event e) {
		Alert alert = new Alert(AlertType.NONE);
		
		if(websiteNameTextField.getText().isEmpty() &&
		   urlTextField.getText().isEmpty() &&
		   emailTextField.getText().isEmpty() &&
		   usernameTextField.getText().isEmpty() &&
		   creationDateTextField.getText().isEmpty() &&
		   expirationDatePicker.getValue() == null &&
		   passwordTextField.getText().isEmpty()) {
				alert = new Alert(AlertType.ERROR);
				alert.setContentText("Please enter at least one field or click back button");
				alert.show();
		}
		else {
			String websiteName = websiteNameTextField.getText().toString();
			String url = urlTextField.getText().toString();
			String email = emailTextField.getText().toString();
			String username = usernameTextField.getText().toString();
			String password = passwordTextField.getText().toString();
			String passwordEncrypted = null;
			Date creationDate = null;
			Date expirationDate = null;
			
			if(websiteNameTextField.getText().isEmpty()) {
				websiteName = account.getWebsite_name();
			}
			
			if(urlTextField.getText().isEmpty()) {
				url = account.getUrl();
			}
			
			if(emailTextField.getText().isEmpty()) {
				email = account.getEmail();
			}
			
			if(usernameTextField.getText().isEmpty()) {
				username = account.getUsername();
			}
			
			if(!passwordTextField.getText().isEmpty()) {
				PassUtil passUtil = new PassUtil();
				passwordEncrypted = passUtil.encrypt(password);
			}
			else {
				passwordEncrypted = account.getEncryptedPassword();
			}

			if(creationDateTextField.getText().isEmpty()) {
				creationDate = Date.valueOf(account.getCreationDate());
			}
			
			if(expirationDatePicker.getValue() == null) {
				expirationDate = Date.valueOf(account.getExpirationDate());
			}
			
			Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
			String create = creationDateTextField.getText().toString();
			
			DateTimeFormatter format = DateTimeFormatter.ISO_LOCAL_DATE;	
			if(!creationDateTextField.getText().isEmpty()) {
				// check if the string pattern from user input is valid or not (yyyy-mm-dd)
				if (pattern.matcher(create).matches()) {
					LocalDate creation = LocalDate.parse(create, format);
					creationDate = Date.valueOf(creation);
				} else {
					alert.setAlertType(AlertType.ERROR);
					alert.setContentText("Please use the following pattern for date (yyyy-mm-dd)");
					alert.show();
					return;
				}
			}
			
			String expire = null;
			if(!(expirationDatePicker.getValue() == null)) {
				expire = expirationDatePicker.getValue().format(format);
				LocalDate expiration = LocalDate.parse(expire);
				expirationDate = Date.valueOf(expiration);
			}
			
			DBUtils.updateInfo(e, author, websiteName, url, email, username, passwordEncrypted, creationDate, expirationDate);
		}
	}
	

	/**
	 * auto-generate password for accounts that depends on the setting
	 * 
	 * @return the randomize generated password for account.
	 */
	public String generatePassword() {
        String capitalCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String specialChar = "!@#$%^&*()_+-|`~.";
        String numbers = "0123456789";
        
        List<Character> list = new ArrayList<>();
        final char CAPITAL = 'C';
        final char LOWER = 'L';
        final char SPECIAL = 'S';
        final char NUMBER = 'N';
        
        boolean hasCapital = false;
        boolean hasLowerCase = false;
        boolean hasSpecialChar = false;
        boolean hasNumber = false;
        int length = 0;
        
        if(!hasCapitalCB.isSelected() && 
           !hasLowerCB.isSelected() && 
           !hasSpecialCB.isSelected() && 
           !hasDigitCB.isSelected() && 
           lengthTextField.getText().isEmpty()) {
        	hasCapital = DBUtils.getHasCapital(author);
            hasLowerCase = DBUtils.getHasLowerCase(author);
            hasSpecialChar = DBUtils.getHasSpecialChar(author);
            hasNumber = DBUtils.getHasNumber(author);
            length = DBUtils.getLength(author);
        }
        else {
        	hasCapital = hasCapitalCB.isSelected();
        	hasLowerCase = hasLowerCB.isSelected();
        	hasSpecialChar = hasSpecialCB.isSelected();
        	hasNumber = hasDigitCB.isSelected();
        	
        	String size = lengthTextField.getText().toString();
        	if(size.isEmpty()) {
        		Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("Please enter a length");
				alert.show();
				return null;
        	}
        	boolean noError = true;
        	for(int i = 0; i < size.length(); i++) {
				if(!Character.isDigit(size.charAt(i))) {
					noError = false;
					break;
				}
			}
        	if(noError) {
        		length = Integer.parseInt(size);
        		if(length < 1 || length > 30) {
        			Alert alert = new Alert(AlertType.ERROR);
					alert.setContentText("Please enter length in the range between 1 to 30");
					alert.show();
					return null;
        		}
        	}
        	else {
        		Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("Please enter digit only");
				alert.show();
				return null;
        	}
        }
        
        if(hasCapital && !hasLowerCase && !hasSpecialChar && !hasNumber) {
        	list.add(CAPITAL);
        }
        else if(!hasCapital && hasLowerCase && !hasSpecialChar && !hasNumber) {
        	list.add(LOWER);
        }
        else if(!hasCapital && !hasLowerCase && hasSpecialChar && !hasNumber) {
        	list.add(SPECIAL);
        }
        else if(!hasCapital && !hasLowerCase && !hasSpecialChar && hasNumber) {
        	list.add(NUMBER);
        }
        else if(hasCapital && hasLowerCase && !hasSpecialChar && !hasNumber) {
        	list.add(CAPITAL);
        	list.add(LOWER);
        }
        else if(hasCapital && !hasLowerCase && hasSpecialChar && !hasNumber) {
        	list.add(CAPITAL);
        	list.add(SPECIAL);
        }
        else if(hasCapital && !hasLowerCase && !hasSpecialChar && hasNumber) {
        	list.add(CAPITAL);
        	list.add(NUMBER);
        }
        else if(!hasCapital && hasLowerCase && hasSpecialChar && !hasNumber) {
        	list.add(LOWER);
        	list.add(SPECIAL);
        }
        else if(!hasCapital && hasLowerCase && !hasSpecialChar && hasNumber) {
        	list.add(LOWER);
        	list.add(NUMBER);
        }
        else if(!hasCapital && !hasLowerCase && hasSpecialChar && hasNumber) {
        	list.add(SPECIAL);
        	list.add(NUMBER);
        }
        else if(hasCapital && hasLowerCase && hasSpecialChar && !hasNumber) {
        	list.add(CAPITAL);
        	list.add(LOWER);
        	list.add(SPECIAL);
        }
        else if(hasCapital && hasLowerCase && !hasSpecialChar && hasNumber) {
        	list.add(CAPITAL);
        	list.add(LOWER);
        	list.add(NUMBER);
        }
        else if(!hasCapital && hasLowerCase && hasSpecialChar && hasNumber) {
        	list.add(LOWER);
        	list.add(SPECIAL);
        	list.add(NUMBER);
        }
        else {
        	list.add(CAPITAL);
        	list.add(LOWER);
        	list.add(SPECIAL);
        	list.add(NUMBER);
        }
        
        Random random = new Random();
        String pwd = "";
        for(int i = 1; i <= length; i++) {
        	int rand = random.nextInt(list.size());
        	char randChar = list.get(rand);
        	switch(randChar) {
        		case LOWER:
        			pwd += lowerCase.charAt(random.nextInt(lowerCase.length()));
        			break;
        		case CAPITAL:
        			pwd += capitalCase.charAt(random.nextInt(capitalCase.length()));
        			break;
        		case SPECIAL:
        			pwd += specialChar.charAt(random.nextInt(specialChar.length()));
        			break;
        		case NUMBER:
        			pwd += numbers.charAt(random.nextInt(numbers.length()));
        			break;
        		default:
        	}
        }
        return pwd;
  }

	/**
	 * Handles Text field events, call the updateAccount method when user press enter.
	 */
	private void initialTextFields() {
		websiteNameTextField.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				updateAccount(e);
			}
		});

		urlTextField.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				updateAccount(e);
			}
		});

		emailTextField.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				updateAccount(e);
			}
		});

		usernameTextField.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				updateAccount(e);
			}
		});

		passwordTextField.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				updateAccount(e);
			}
		});

		creationDateTextField.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				updateAccount(e);
			}
		});

		expirationDatePicker.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				updateAccount(e);
			}
		});
		
		lengthTextField.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				updateAccount(e);
			}
		});
	}
	
	/**
	 * Initialize all labels to the current account's information
	 */
	private void initialLabels() {
		websiteNameLabel.setText("Current website name: " + account.getWebsite_name());
		urlLabel.setText("Current URL: " + account.getUrl());
		usernameLabel.setText("Current username: " + account.getUsername());
		emailLabel.setText("Current email: " + account.getEmail());
		passwordLabel.setText("Current password: " + account.getPassword());
		creationDateLabel.setText("Current creation date: " + account.getCreationDate());
		expirationDateLabel.setText("Current expiration date: " + account.getExpirationDate());
		
	}

}
