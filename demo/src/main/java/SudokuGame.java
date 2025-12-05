import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.text.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.scene.paint.Color;
import javafx.scene.Node;

public class SudokuGame extends Application{
    @Override
    public void start(Stage primaryStage) {
        

      GridPane sudokuGrid = new GridPane();
      sudokuGrid.setGridLinesVisible(true);
      sudokuGrid.setAlignment(Pos.CENTER);
      sudokuGrid.setHgap(0);
      sudokuGrid.setVgap(0);
      String contents = "";

      try {
        contents = Files.readString(Paths.get("demo\\src\\main\\java\\clues.txt"));
      } catch (IOException e) {
        System.out.println(e);
        e.printStackTrace();
        System.exit(1);
      }

      String[] cluesArray = contents.split("\n");

      for (int row = 0; row < 9; row++) {
          for (int col = 0; col < 9; col++) {
            TextField cell = new TextField();
            cell.setAlignment(Pos.CENTER);
            // Build CSS for the borders
            StringBuilder style = new StringBuilder();
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
        newGame.setOnAction(e -> newBoard(sudokuGrid, cluesArray));
        Button options = new Button("Options");
        options.setOnAction(e -> System.out.println("2"));
        Button help = new Button("Help");
        help.setOnAction(e -> System.out.println("3"));

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
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static TextField getCell(GridPane grid, int row, int col) {
      TextField cell = null;
      for (Node n : grid.getChildren()) {
        if (GridPane.getRowIndex(n) == null || GridPane.getColumnIndex(n) == null) continue;
        if (GridPane.getRowIndex(n) == row && GridPane.getColumnIndex(n) == col && n instanceof TextField) {
          cell = (TextField) n;
        }
      }
      return cell;
    }

    public static void newBoard(GridPane sudokuGrid, String[] cluesArray) {
      String cluesString = cluesArray[(int) (cluesArray.length * Math.random())];

      for (int row = 0; row < 9; row++) {
          for (int col = 0; col < 9; col++) {
            int index = row*9 + col;
            String clue = cluesString.substring(index, index+1);
            TextField cell = getCell(sudokuGrid, row, col);
            cell.setAlignment(Pos.CENTER);

            if (clue.equals("0")) {
              Color c = Color.WHITE;
              BackgroundFill backgroundFill = new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY);
              Background background = new Background(backgroundFill);
              cell.setBackground(background);
              cell.setText("");
              cell.setEditable(true);
            }
            else {
              cell.setText(clue);
              cell.setEditable(false);
              Color c = Color.rgb(223, 223, 223, 1);
              BackgroundFill backgroundFill = new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY);
              Background background = new Background(backgroundFill);
              cell.setBackground(background);
            }
            }
        }
    }
}
