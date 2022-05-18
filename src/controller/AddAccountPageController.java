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
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

public class AddAccountPageController implements Initializable {

	@FXML
	private TextField websiteNameTextField;

	@FXML
	private TextField urlTextField;

	@FXML
	private TextField emailTextField;

	@FXML
	private TextField usernameTextField;

	@FXML
	private TextField passwordTextField;

	@FXML
	private TextField creationDateTextField;

	@FXML
	private DatePicker expirationDatePicker;

	@FXML
	private Button backButton;

	@FXML
	private Button addButton;

	@FXML
	private Button addTodayButton;
	
	@FXML
	private Button generatePasswordButton;

	@FXML
	private ImageView calendarImageView;

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
	private PassUtil passUtil = new PassUtil();

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
		setTestFieldEvents();

		// go back to Main page if the user click back button
		backButton.setOnMouseClicked(e -> {
			try {
				//SceneChangingUtils.changeScene(e, "Main", "view/MainPage.fxml");
				SceneChangingUtils.toMainPage(e, "Welcome " + author, author);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		// add an account if the user click the add button
		addButton.setOnMouseClicked(e -> {
			addAccount(e);
		});

		// add today's date on the creationDate text field
		addTodayButton.setOnMouseClicked(e -> {
			Date today = Date.valueOf(LocalDate.now());
			creationDateTextField.setText(today.toString());
		});
		
		//put the generated password to text field 
		generatePasswordButton.setOnMouseClicked(e -> {
            String password = generatePassword();
            if(password != null) {
            	passwordTextField.setText(password);
            }
        });

	}

	/**
	 * Handles Text field events, call the addAccount method when user press enter.
	 */
	protected void setTestFieldEvents() {
		websiteNameTextField.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				addAccount(e);
			}
		});

		urlTextField.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				addAccount(e);
			}
		});

		emailTextField.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				addAccount(e);
			}
		});

		usernameTextField.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				addAccount(e);
			}
		});

		passwordTextField.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				addAccount(e);
			}
		});

		creationDateTextField.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				addAccount(e);
			}
		});

		expirationDatePicker.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				addAccount(e);
			}
		});

		lengthTextField.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				addAccount(e);
			}
		});
	}

	/**
	 * Verifies fields and adds account to database.
	 * 
	 * @param e - javaFX events
	 */
	private void addAccount(Event e) {
		// initialize alert
		Alert alert = new Alert(AlertType.NONE);

		// give error when one of the text field besides email and username is empty
		if (websiteNameTextField.getText().isEmpty() || urlTextField.getText().isEmpty()
				|| passwordTextField.getText().isEmpty() || creationDateTextField.getText().isEmpty() // ||
		/* expirationDateTextField.getText().isEmpty() */) {
			alert.setAlertType(AlertType.ERROR);
			alert.setContentText("Please fill the empty text field");
			alert.show();
		}
		// give error if both email and username are empty
		else if (usernameTextField.getText().isEmpty() && emailTextField.getText().isEmpty()) {
			alert.setAlertType(AlertType.ERROR);
			alert.setContentText("Please enter either a username or a email");
			alert.show();
		}
		// get informations and call database
		else {
			String websiteName = websiteNameTextField.getText().toString();
			String url = urlTextField.getText().toString();

			String email;
			// get the email if the username text field is empty
			if (emailTextField.getText().isEmpty()) {
				email = null;
			} else {
				email = emailTextField.getText().toString();
			}

			// get the username if the email text field is empty
			String username;
			if (usernameTextField.getText().isEmpty()) {
				username = null;
			} else {
				username = usernameTextField.getText().toString();
			}

			String password = passwordTextField.getText().toString();
			String passwordEncrypted = passUtil.encrypt(password);

			Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
			String create = creationDateTextField.getText().toString();

			// check if the string pattern from user input is valid or not (yyyy-mm-dd)
			if (pattern.matcher(create).matches()) {
				DateTimeFormatter format = DateTimeFormatter.ISO_LOCAL_DATE;

				LocalDate creation = LocalDate.parse(create, format);
				Date creationDate = Date.valueOf(creation);

				String expire = expirationDatePicker.getValue().format(format);
				LocalDate expiration = LocalDate.parse(expire);
				Date expirationDate = Date.valueOf(expiration);

				DBUtils.addInfo(e, author, websiteName, url, email, username, passwordEncrypted, creationDate, expirationDate);
			} else {
				alert.setAlertType(AlertType.ERROR);
				alert.setContentText("Please use the following pattern for date (yyyy-mm-dd)");
				alert.show();
			}

		}
	}

	/**
	 * Setter for author variable
	 * 
	 * @param user - current user
	 */
	public void setUser(String user) {
		author = user;
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
}
