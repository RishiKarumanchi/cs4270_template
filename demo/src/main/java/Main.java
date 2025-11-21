import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

  private Scene scene;
  private SceneManager sceneManager;

  @Override
  public void start(Stage primaryStage) {
    sceneManager = new SceneManager();

    // Start on the main menu screen
    var initialRoot = sceneManager.createMenuView();

    // Create one Scene and reuse it
    scene = new Scene(initialRoot, 800, 600);

    // Give the Scene to the manager so it can swap roots later
    sceneManager.setScene(scene);

    primaryStage.setTitle("Scene Switching Example");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}