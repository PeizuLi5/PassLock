package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
//import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * PassLock
 * @version 1.0
 * @author Lilai Yang
 * @author Nam Van
 * @author Peizu Li
 * Order of authors only based on alphabetical order of first name
 */
public class Main extends Application {
	/**
	 * The main entry point for all JavaFx applications, called after init method
	 * has returned, and after the system is ready for the application to begin
	 * running.
	 * 
	 * @param primaryStage - the primary stage for this application, onto which the
	 *                     application scene can be set.
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			// set home page as the first page
			GridPane root = (GridPane) FXMLLoader.load(getClass().getClassLoader().getResource("view/HomePage.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getClassLoader().getResource("css/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("PassLock");
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Main, launches application.
	 * 
	 * @param args - default of main
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
