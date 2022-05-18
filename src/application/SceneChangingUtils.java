package application;

import java.io.IOException;

import controller.AddAccountPageController;
import controller.MainPageController;
import controller.ModifyPageController;
import controller.ResetPassword_Page_2_Controller;
import controller.ResetPassword_Page_3_Controller;
import controller.SettingPageController;
import controller.View_Page_1_Controller;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SceneChangingUtils {

	/**
	 * Go to the last reset password page and pass email as data.
	 * 
	 * @param e     - javaFx event
	 * @param title - title of new scene
	 * @param email - email
	 * @throws IOException
	 */
	public static void toLastReset(Event e, String title, String email) throws IOException {
		Parent root = null;
		FXMLLoader loader = new FXMLLoader(
				SceneChangingUtils.class.getClassLoader().getResource("view/ResetPassword_page_3.fxml"));
		root = loader.load();

		ResetPassword_Page_3_Controller controller = (ResetPassword_Page_3_Controller) loader.getController();
		controller.setEmail(email);

		Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setTitle(title);
		stage.setScene(scene);
		stage.show();
		stage.centerOnScreen();
	}

	/**
	 * Go to the second reset password page and pass email as data.
	 * 
	 * @param e     - javaFx event
	 * @param title - Page title
	 * @param email - user email
	 * @throws IOException
	 */
	public static void toNextReset(Event e, String title, String email) throws IOException {
		Parent root = null;
		FXMLLoader loader = new FXMLLoader(
				SceneChangingUtils.class.getClassLoader().getResource("view/ResetPassword_page_2.fxml"));
		root = loader.load();

		ResetPassword_Page_2_Controller controller = (ResetPassword_Page_2_Controller) loader.getController();
		controller.setEmail(email);
		controller.setQuestion(email);

		Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setTitle(title);
		stage.setScene(scene);
		stage.show();
		stage.centerOnScreen();
	}

	/**
	 * Go to the add account page and pass user(author as data).
	 * 
	 * @param e     - javaFx event
	 * @param title - page title
	 * @param user  - current user
	 * @throws IOException
	 */
	public static void toAddAccountPage(Event e, String title, String user) throws IOException {
		Parent root = null;
		FXMLLoader loader = new FXMLLoader(
				SceneChangingUtils.class.getClassLoader().getResource("view/AddAccountPage.fxml"));
		root = loader.load();

		AddAccountPageController controller = (AddAccountPageController) loader.getController();
		controller.setUser(user);

		Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setTitle(title);
		stage.setScene(scene);
		stage.show();
		stage.centerOnScreen();
	}

	/**
	 * Go to the Main page and pass user(author) as data.
	 * 
	 * @param e     - javFx event
	 * @param title - page title
	 * @param user  - current user
	 * @throws IOException
	 */
	public static void toMainPage(Event e, String title, String user) throws IOException {
		Parent root = null;
		FXMLLoader loader = new FXMLLoader(SceneChangingUtils.class.getClassLoader().getResource("view/MainPage.fxml"));
		root = loader.load();
		
		MainPageController controller = (MainPageController) loader.getController();
		controller.setUser(user);

		ObservableList<Account> list = DBUtils.showList(user);//was ArrayList
		controller.setList(list);

		Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setTitle(title);
		stage.setScene(scene);
		stage.show();
		stage.centerOnScreen();
	}
	
	/**
	 * Go to the Main page and pass user(author) as data.
	 * 
	 * @param e     - javFx event
	 * @param title - page title
	 * @param user  - current user
	 * @throws IOException
	 */
	public static void toMainPage(Event e, String title, String user, ListView<Account> accounts) throws IOException {
		Parent root = null;
		FXMLLoader loader = new FXMLLoader(SceneChangingUtils.class.getClassLoader().getResource("view/MainPage.fxml"));
		root = loader.load();
		
		MainPageController controller = (MainPageController) loader.getController();
		controller.setUser(user);

		ObservableList<Account> list = accounts.getItems();
		controller.setList(list);

		Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setTitle(title);
		stage.setScene(scene);
		stage.show();
		stage.centerOnScreen();
	}

	/**
	 * Change to any scenes for an event (no data passing).
	 * 
	 * @param e     - javaFx event
	 * @param title - page title
	 * @param file  - FXML page
	 * @throws IOException
	 */
	public static void changeScene(Event e, String title, String file) throws IOException {
		Parent root = null;
		FXMLLoader loader = new FXMLLoader(SceneChangingUtils.class.getClassLoader().getResource(file));
		root = loader.load();

		Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setTitle(title);
		stage.setScene(scene);
		stage.show();
		stage.centerOnScreen();
	}

	/**
	 * Change to any scenes when something being clicked (no data passing).
	 * 
	 * @param e     - javaFx event
	 * @param title - page title
	 * @param file  - FXML page
	 * @throws IOException
	 */
	public static void changeScene(MouseEvent e, String title, String file) throws IOException {
		Parent root = null;
		FXMLLoader loader = new FXMLLoader(SceneChangingUtils.class.getClassLoader().getResource(file));
		root = loader.load();

		Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setTitle(title);
		stage.setScene(scene);
		stage.show();
		stage.centerOnScreen();
	}

	/**
	 * Change to View_Page_1.fxml
	 * 
	 *  @param e 		- javaFx event
	 *  @param title	- page title
	 *  @param author	- user
	 *  @param account	- Account class that contains all information of a account
	 *  @throws IOException
	 *  @throws InterruptedException
	 */
	public static void toViewPage1(MouseEvent e, String title, String author, Account account) throws IOException, InterruptedException {
		Parent root = null;
		FXMLLoader loader = new FXMLLoader(SceneChangingUtils.class.getClassLoader().getResource("view/View_Page_1.fxml"));
		
		View_Page_1_Controller controller = new View_Page_1_Controller();
		controller.setAccount(account);
		controller.setAuthor(author);
		
		loader.setController(controller);
		root = loader.load();
		Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setTitle(title);
		stage.setScene(scene);
		stage.show();
		stage.centerOnScreen();
	}

	/**
	 * Change to SettingPage.fxml
	 * 
	 * @param e			- javaFX event
	 * @param title		- page title
	 * @param author	- user
	 * @throws IOException
	 */
	public static void toSettingPage(MouseEvent e, String title, String author) throws IOException {
		Parent root = null;
		FXMLLoader loader = new FXMLLoader(SceneChangingUtils.class.getClassLoader().getResource("view/SettingPage.fxml"));

		SettingPageController controller = new SettingPageController();
		controller.setAuthor(author);

		loader.setController(controller);
		root = loader.load();
		Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setTitle(title);
		stage.setScene(scene);
		stage.show();
		stage.centerOnScreen();
	}

	/**
	 * Change to ModifyPage.fxml
	 * 
	 * @param e			- JavaFx event
	 * @param title		- page title
	 * @param author	- user
	 * @param account	- the account that is being selected by user
	 * @throws IOException
	 */
	public static void toModifyPage(MouseEvent e, String title, String author, Account account) throws IOException {
		Parent root = null;
		FXMLLoader loader = new FXMLLoader(SceneChangingUtils.class.getClassLoader().getResource("view/ModifyPage.fxml"));
		
		ModifyPageController controller = new ModifyPageController();
		controller.setAuthor(author);
		controller.setAccount(account);
		
		loader.setController(controller);
		root = loader.load();
		Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setTitle(title);
		stage.setScene(scene);
		stage.show();
		stage.centerOnScreen();
	}
	

}
