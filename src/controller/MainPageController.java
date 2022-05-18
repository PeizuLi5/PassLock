package controller;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import application.Account;
import application.DBUtils;
import application.SceneChangingUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

public class MainPageController implements Initializable {

	@FXML
	private TextField searchTextField;

	@FXML
	private ImageView addImageView;

	@FXML
	private ImageView personImageView;

	@FXML
	private ImageView searchImageView;

	@FXML
	private Button logoutButton;

	@FXML
	private Button settingButton;

	@FXML
	private ListView<Account> list;

	private String author = new String();

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
		
		searchImageView.setOnMouseClicked(e -> {
			if (logoutButton.isVisible()) {
				logoutButton.setVisible(false);
				settingButton.setVisible(false);
				settingButton.toBack();
			}
		});
		
		// go to add account page if this image is being clicked
		addImageView.setOnMouseClicked(e -> {
			try {
				SceneChangingUtils.toAddAccountPage(e, "Add an account", author);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		// show the buttons as drop down menu when the person image is being clicked
		personImageView.setOnMouseClicked(e -> {
			if (logoutButton.isVisible()) {
				logoutButton.setVisible(false);
				settingButton.setVisible(false);
				settingButton.toBack();
			} else {
				logoutButton.setVisible(true);
				settingButton.setVisible(true);
				settingButton.toFront();
			}
		});

		// logout method (back to home page)
		logoutButton.setOnMouseClicked(e -> {
			try {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setContentText("Do you want to logout?");
				Optional<ButtonType> result = alert.showAndWait();
				if(result.get() == ButtonType.OK) {
					SceneChangingUtils.changeScene(e, "Welcome to PassLock", "view/HomePage.fxml");
				}
				else {
					logoutButton.setVisible(false);
					settingButton.setVisible(false);
					settingButton.toBack();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		
		//show view Page when this cell gets clicks
		list.setOnMouseClicked(e -> {
			if (logoutButton.isVisible()) {
				logoutButton.setVisible(false);
				settingButton.setVisible(false);
				settingButton.toBack();
			}
    		if(e.getClickCount() == 2 && list.getSelectionModel().getSelectedItem() != null) {
    			Account account = list.getSelectionModel().getSelectedItem();
    			try {
					SceneChangingUtils.toViewPage1(e, "View", author, account);
				} catch (IOException | InterruptedException e1) {
					e1.printStackTrace();
				}
    		}
    	});
		
		searchImageView.setOnMouseClicked(e ->{
			if (!hasEmptyTextField()) {
				String searchText = searchTextField.getText().trim();
				updateSuggestions(searchText);
				try {
					SceneChangingUtils.toMainPage(e, "Welcome " + author, author, list);
					searchTextField.setText(searchTextField.getText().trim());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			} else {
				try {
					SceneChangingUtils.toMainPage(e, author, author);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		settingButton.setOnMouseClicked(e -> {
			try {
				SceneChangingUtils.toSettingPage(e, "Setting (For general password generation)", author);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}

	/**
	 * Adds a string to list.
	 * 
	 * @param s - string to be added
	 */
	public void addToList(Account a) {
		list.getItems().add(a);
	}

	/**
	 * Setter for author
	 * 
	 * @param user - current user
	 */
	public void setUser(String user) {
		author = user;
	}

	public void clearList() {
		list.getItems().clear();
	}
	
	/**
	 * Appends new list to list.
	 * 
	 * @param list2 - new list to be added to list
	 */
	public void setList(ObservableList<Account> list2) {
		for (Account s : list2) {
			addToList(s);
		}
	}

	/**
	 * Check if there exists an empty text field.
	 * 
	 * @return true if there is an empty text field
	 */
	protected boolean hasEmptyTextField() {
		if (!searchTextField.getText().trim().isEmpty())
			return false;
		return true;
	}
	
	/**
	 * Handles text field events, updates searchSuggestions list by calling
	 * checkAccounts.
	 */
	private void setTextFieldEvents() {
		searchTextField.setOnKeyReleased(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				if (!hasEmptyTextField()) {
					String searchText = searchTextField.getText().trim();
					updateSuggestions(searchText);
					try {
						SceneChangingUtils.toMainPage(e, author, author, list);
						searchTextField.setText(searchTextField.getText().trim());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
				} else {
					try {
						SceneChangingUtils.toMainPage(e, author, author);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
	}
	
	/**
	 * Clears list view and shows only items we are searching for.
	 * 
	 * @param searchKey - string to search for
	 */
	private void updateSuggestions(String searchKey) {
		list.getItems().clear();
		//list.getItems().addAll(searchAccounts(searchTextField.getText()));
		list.getItems().addAll(searchAccounts(searchKey));
	}

	/**
	 * Generates list based off of search key.
	 * 
	 * @param searchKey - key to search accounts by
	 * @return list of accounts matching search criteria
	 */
	protected ObservableList<Account> searchAccounts(String searchKey) {
		List<String> keys = Arrays.asList(searchKey.trim());
		//List<Account> accounts = list.getItems();
		ObservableList<Account> accounts = DBUtils.showList(author);
		List<Account> result = accounts.stream().filter(input -> {
			return keys.stream().allMatch(word -> input.getWebsite_name().length() > word.length() ? 
					input.getWebsite_name().toLowerCase().substring(0, word.length()).equals(word.toLowerCase()) : 
						input.getWebsite_name().toLowerCase().equals(word.toLowerCase()));
		}).collect(Collectors.toList());
		
		return FXCollections.observableList(result);
	}
	
}
