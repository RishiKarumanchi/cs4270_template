import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.scene.paint.Color;

public class SudokuGame extends Application{
    @Override
    public void start(Stage primaryStage) {
        

        GridPane sudokuGrid = new GridPane();
        sudokuGrid.setGridLinesVisible(true);
        sudokuGrid.setAlignment(Pos.CENTER);
        sudokuGrid.setHgap(0);
        sudokuGrid.setVgap(0);

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {

                TextField cell = new TextField();
                cell.setPrefSize(40, 40);
                cell.setAlignment(Pos.CENTER);

                // Build CSS for the borders
                StringBuilder style = new StringBuilder();
                style.append("-fx-border-color: black;");
                style.append("-fx-border-style: solid;");

                // Default thin borders
                int top = 1, right = 1, bottom = 1, left = 1;

                // Thicker borders at 3x3 boundaries
                if (row == 0)
                  top = 3;
                if (row == 8)
                  bottom = 3;
                if (col == 0)
                  left = 3;
                if (col == 8)
                  right = 3;

                if (row % 3 == 0)
                  top = 3;
                if (col % 3 == 0)
                  left = 3;

                // Apply border widths
                style.append(String.format(
                    "-fx-border-width: %d %d %d %d;",
                    top, right, bottom, left));

                cell.setStyle(style.toString());
                sudokuGrid.add(cell, col, row);
            }
        }

        Button newGame = new Button("New Board");
        newGame.setOnAction(e -> System.out.println("goon1"));
        Button options = new Button("Options");
        options.setOnAction(e -> System.out.println("goon2"));
        Button help = new Button("Help");
        help.setOnAction(e -> System.out.println("goon3"));
        VBox vbox = new VBox(10.0, newGame, options, help);
        newGame.setPrefSize(100,50);
        options.setPrefSize(100, 50);
        help.setPrefSize(100, 50);
        vbox.setAlignment(Pos.CENTER);
        HBox hbox = new HBox(10.0, sudokuGrid, vbox);
        Color c = Color.rgb(139, 0, 0);
        BackgroundFill backgroundFill = new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(backgroundFill);

        BorderPane borderPane = new BorderPane(sudokuGrid, null, hbox, null, null);
        borderPane.setPadding(new Insets(0, 100, 0, 0 ));
        borderPane.setBackground(background);
        Scene scene = new Scene(borderPane, 300, 200);
        scene.setFill(c);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void fillCell(int row, int column, int number) {
      Label label = new Label(Integer.toString(number));
      }
}
