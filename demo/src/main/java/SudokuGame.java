import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;

public class SudokuGame extends Application{
    @Override
    public void start(Stage primaryStage) {
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your name");

        Button greetButton = new Button("Greet");
        Label greetingLabel = new Label();

        greetButton.setOnAction(e -> {
        String name = nameField.getText();
        greetingLabel.setText("Hello, " + name + "!");
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(nameField, greetButton, greetingLabel);
        
        Color c = Color.rgb(139, 0, 0);
        BackgroundFill backgroundFill = new BackgroundFill(c, CornerRadii.EMPTY, 0.);

        Scene scene = new Scene(layout, 300, 200);
        scene.setFill(c);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}