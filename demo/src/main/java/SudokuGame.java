import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.UnaryOperator;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;


/**
* SudokuGame
*
* - Loads puzzles from boards.txt (each line is an 81-char string, '0' = blank).
* - New Puzzle loads a random board and computes/keeps its solution.
* - Solve Board fills the grid with solution values.
* - Check Answers compares user inputs to the stored solution and shows a popup
*   if incorrect entries are found (or a success popup if all correct).
*
* Keep boards.txt in the project working directory (same place you run the app).
*/
public class SudokuGame extends Application {
   // Stores current puzzle and its solved solution
   private static int[][] currentSolution = null;
   private static int[][] currentPuzzle = null;


   // Main grid reference so handlers can access it
   private GridPane sudokuGrid;

   // Turns on when Create Puzzle is clicked
   private boolean makingPuzzle = false;

   // Timer
   private int secondsElapsed = 0;
   private Timeline timeline;
   private Label timerLabel = new Label("Time: 0:00");
  
   // Number of clues for difficulty
   private Label clues = new Label("Clues: 0 (N/A)");

   // Constantly checks if grid is solved
   private Timeline checker;


   @Override
   public void start(Stage primaryStage) {
       sudokuGrid = new GridPane();
       sudokuGrid.setGridLinesVisible(true);
       sudokuGrid.setAlignment(Pos.CENTER);
       sudokuGrid.setHgap(0);
       sudokuGrid.setVgap(0);


       // Build an empty 9x9 layout (cells will be replaced by generatePuzzle)
       for (int row = 0; row < 9; row++) {
           for (int col = 0; col < 9; col++) {
               TextField tf = new TextField();
               tf.setAlignment(Pos.CENTER);
               // borders
               int top = 1, right = 1, bottom = 1, left = 1;
               if (row == 0) top = 3;
               if (row == 8) bottom = 3;
               if (col == 0) left = 3;
               if (col == 8) right = 3;
               if (row % 3 == 0) top = 3;
               if (col % 3 == 0) left = 3;
               tf.setStyle(tf.getStyle() + String.format("-fx-border-width: %d %d %d %d;", top, right, bottom, left));
               singleDigit(tf);
               sudokuGrid.add(tf, col, row);
           }
       }


       // Buttons
       Button loadPuzzle = new Button("Load Puzzle");
       Button createPuzzle = new Button("Create Puzzle");
       Button hint = new Button("Hint");
       Button check = new Button("Check");
       Button solve = new Button("Solve");
       Button reset = new Button("Reset Board");
       reset.setDisable(true);
       hint.setDisable(true);

       VBox vbox = new VBox(10.0, loadPuzzle, createPuzzle, hint, check, solve, reset);
       vbox.setAlignment(Pos.CENTER);


       HBox hbox = new HBox(10.0, sudokuGrid, vbox);
       hbox.setAlignment(Pos.CENTER);
       hbox.setFillHeight(true);

       HBox labels = new HBox(10.0, timerLabel, clues);

       Color c = Color.rgb(103, 189, 247, 0.59);
       BackgroundFill backgroundFill = new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY);
       Background background = new Background(backgroundFill);


       BorderPane borderPane = new BorderPane();
       borderPane.setCenter(hbox);
       borderPane.setTop(labels);
       BorderPane.setAlignment(labels, Pos.CENTER);
       borderPane.setBackground(background);


       Scene scene = new Scene(borderPane);
       scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
       primaryStage.setScene(scene);
       primaryStage.setTitle("Sudoku Game");
       primaryStage.show();

       // Sets up timer
       timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
        secondsElapsed++;
        int minutes = secondsElapsed / 60;
        int seconds = secondsElapsed % 60;
        timerLabel.setText(String.format("Time: %d:%02d", minutes, seconds));
       })
      );
      timeline.setCycleCount(Animation.INDEFINITE);

       // Button actions
       loadPuzzle.setOnAction(e -> {
           try {
               loadRandomPuzzleFromFile();
               reset.setDisable(false);
               hint.setDisable(false);
               resetTimer();
           } catch (IOException ex) {
               showErrorAlert("Error loading a new puzzle: " + ex.getMessage());
               ex.printStackTrace();
           }
       });

       createPuzzle.setOnAction(e -> {
        if (!makingPuzzle) {
          makingPuzzle = true;
          createPuzzle.setText("Finish Puzzle");
          check.setDisable(true);
          solve.setDisable(true);
          reset.setDisable(true);
          hint.setDisable(true);
          sudokuGrid.getChildren().clear();
          resetTimer();

          // creates new textField
          for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextField tf = new TextField();
                tf.setAlignment(Pos.CENTER);

                // borders
                int top = 1, right = 1, bottom = 1, left = 1;
                if (row % 3 == 0) top = 3;
                if (col % 3 == 0) left = 3;
                if (row == 8) bottom = 3;
                if (col == 8) right = 3;
                tf.setStyle(String.format("-fx-border-width: %d %d %d %d;", top, right, bottom, left));
                singleDigit(tf);
                sudokuGrid.add(tf, col, row);
            }
          }
        } else {
          int[][] userBoard = getBoardFromGrid(sudokuGrid);
          reset.setDisable(false);
          hint.setDisable(false);
          // Resets cell backgrounds
          for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
              TextField tf = getTextFieldByRowColumn(row, col, sudokuGrid);
              tf.setStyle(tf.getStyle().replaceAll("-fx-background-color: #[a-f0-9][6];", ""));
            }
          }

          if (hasConflicts(userBoard)) {
            showErrorAlert("Invalid entries. Try checking for conflicts between clues.");
            
            // Highlights conflicts
            for (int row = 0; row < 9; row ++) {
              for (int col = 0; col < 9; col++) {
                int value = userBoard[row][col];
                if (value == 0) continue;

                userBoard[row][col] = 0;
                if (!isValidPlacement(userBoard, row, col, value)) {
                  TextField tf = getTextFieldByRowColumn(row, col, sudokuGrid);
                  tf.setStyle(tf.getStyle() + "-fx-background-color: #ffcccc");
                }
                userBoard[row][col] = value;
              }
            }

            makingPuzzle = true;
            createPuzzle.setText("Finish Puzzle");
            check.setDisable(true);
            solve.setDisable(true);
            return;
          }

          // Copy board to solve
          userBoard = getBoardFromGrid(sudokuGrid);
          int[][] solvedBoard = new int[9][9];
          for (int r = 0; r < 9; r++) System.arraycopy(userBoard[r], 0, solvedBoard[r], 0, 9);

          if (!solveSudoku(solvedBoard)) {
            showErrorAlert("Puzzle is not solvable. Please adjust your clues.");
            makingPuzzle = true;
            createPuzzle.setText("Finish Puzzle");
            check.setDisable(true);
            solve.setDisable(true);
            return;
          }

          currentPuzzle = userBoard;
          currentSolution = solvedBoard;
          populateGridFromPuzzle(currentPuzzle, sudokuGrid);
          computeDifficulty();

          makingPuzzle = false;
          createPuzzle.setText("Create Puzzle");
          check.setDisable(false);
          solve.setDisable(false);
          resetTimer();
        }
       });

       hint.setOnAction(e -> {
        if (currentPuzzle == null || currentSolution == null) {
          showErrorAlert("No puzzle loaded.");
          return;
        }
        for (int row = 0; row < 9; row++) {
          for (int col = 0; col < 9; col++) {
            TextField tf = getTextFieldByRowColumn(row, col, sudokuGrid);
            if (tf.getText().isEmpty()) {
              tf.setText(Integer.toString(currentSolution[row][col]));
              tf.setEditable(false);
              tf.setStyle(tf.getStyle() + "-fx-background-color: #ccffcc");
              return;
            }
          }
        }
        showErrorAlert("No cells remaining!");
       });

       check.setOnAction(e -> {
           if (currentSolution == null) {
               showErrorAlert("No puzzle loaded to check.");
               return;
           }
           int[][] userBoard = getBoardFromGrid(sudokuGrid);
           if (userBoard == null) {
               showErrorAlert("Invalid entries found. Try checking for conflicts between clues.");
               return;
           }
           boolean anyWrong = false;
           // compare only positions that are blanks in original puzzle
           for (int row = 0; row < 9; row++) {
               for (int col = 0; col < 9; col++) {
                   if (currentPuzzle[row][col] == 0) {
                       int userVal = userBoard[row][col];
                       int correctVal = currentSolution[row][col];
                       TextField tf = getTextFieldByRowColumn(row, col, sudokuGrid);
                       if (userVal != correctVal) {
                        anyWrong = true;
                        tf.setStyle(tf.getStyle() + "-fx-background-color: #ffcccc");
                       } else {
                        tf.setStyle(tf.getStyle() + "-fx-background-color: #ccffcc");
                       }
                   }
               }
           }

           Alert alert = new Alert(Alert.AlertType.INFORMATION, "Incorrect entries found.");
           if (!anyWrong)  {
            alert.setContentText("All entries correct!");
            timeline.stop();
           }
           alert.setHeaderText(null);
           alert.showAndWait();
       });

       solve.setOnAction(e -> {
           if (currentSolution == null) {
               showErrorAlert("No puzzle loaded / solution not computed.");
               return;
           }
           applySolutionToGrid(currentSolution, sudokuGrid);
       });

       reset.setOnAction(e -> {
        populateGridFromPuzzle(currentPuzzle, sudokuGrid);
        resetTimer();
       });

       // Checks if all cells are correct
       checker = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
       boolean isSolved = true;
       int[][] board = getBoardFromGrid(sudokuGrid);
        if (currentSolution == null || board == null) return;
        outer:
        for (int row = 0; row < 9; row++) {
          for (int col = 0; col < 9; col++) {
            int value = board[row][col];
            if (value != currentSolution[row][col]) {
              isSolved = false;
              break outer;
            }
          }
        }

        if (isSolved) {
          checker.stop();
          Alert alert = new Alert(Alert.AlertType.INFORMATION, "All entries correct!");
          alert.setHeaderText(null);
          alert.show();
        }
       })
      );
      checker.setCycleCount(Animation.INDEFINITE);
      checker.play();
   }


   public static void main(String[] args) {
       launch(args);
   }


   // File load / puzzle handling
   private void loadRandomPuzzleFromFile() throws IOException {
      // Reads lines from file
      String[] lines = Files.readString(Paths.get("demo\\boards.txt")).split("\n");
      if (lines.length == 0) throw new IOException("boards.txt is empty.");

      String chosenPuzzle = null;
      for (String line : lines) {
        int index = (int) (Math.random() * lines.length);
        String candidate = lines[index].trim();
        if (candidate.length() == 81) {
          chosenPuzzle = candidate;
          break;
        }
      }
       if (chosenPuzzle == null) {
        for (String line : lines) {
          if (line.trim().length() == 81) { 
            chosenPuzzle = line.trim();
            break;
          }
        }
        throw new IOException("No valid 81-character lines found.");
      }

       int[][] puzzle = new int[9][9];
       for (int i = 0; i < 81; i++) {
           char c = chosenPuzzle.charAt(i);
           puzzle[i/9][i%9] = (c >= '1' && c <= '9') ? c - '0' : 0;
       }


       int[][] solution = new int[9][9];
       for (int r = 0; r < 9; r++) System.arraycopy(puzzle[r], 0, solution[r], 0, 9);

       if (!solveSudoku(solution)) throw new IOException("chosenPuzzle puzzle appears unsolvable.");

       currentPuzzle = puzzle;
       currentSolution = solution;
       populateGridFromPuzzle(puzzle, sudokuGrid);
       computeDifficulty();
   }


   private void populateGridFromPuzzle(int[][] puzzle, GridPane grid) {
       grid.getChildren().clear();


       for (int row = 0; row < 9; row++) {
           for (int col = 0; col < 9; col++) {
               int top = 1, right = 1, bottom = 1, left = 1;
               if (row == 0) top = 3;
               if (row == 8) bottom = 3;
               if (col == 0) left = 3;
               if (col == 8) right = 3;
               if (row % 3 == 0) top = 3;
               if (col % 3 == 0) left = 3;


               String style = String.format("-fx-border-width: %d %d %d %d;", top, right, bottom, left);


               if (puzzle[row][col] != 0) {
                   TextField tf = new TextField(Integer.toString(puzzle[row][col]));
                   tf.setEditable(false);
                   tf.setAlignment(Pos.CENTER);
                   tf.setStyle(tf.getStyle() + style + "-fx-background-color: transparent;");
                   grid.add(tf, col, row);
               } else {
                   TextField tf = new TextField();
                   tf.setAlignment(Pos.CENTER);
                   tf.setStyle(tf.getStyle() + style);
                   grid.add(tf, col, row);
                   singleDigit(tf);
               }
           }
       }
   }


   private int[][] getBoardFromGrid(GridPane grid) {
       int[][] board = new int[9][9];
       for (int row = 0; row < 9; row++) {
           for (int col = 0; col < 9; col++) {
               TextField tf = getTextFieldByRowColumn(row, col, grid);
               String txt = tf.getText().trim();


               if (txt.isEmpty()) board[row][col] = 0;
               else {
                   try {
                       int v = Integer.parseInt(txt);
                       if (v < 1 || v > 9) return null;
                       board[row][col] = v;
                   } catch (NumberFormatException ex) { return null; }
               }
           }
       }
       return board;
   }


   private TextField getTextFieldByRowColumn(int row, int col, GridPane grid) {
       for (Node node : grid.getChildren()) {
           Integer r = GridPane.getRowIndex(node);
           Integer c = GridPane.getColumnIndex(node);
           if (r == null) r = 0;
           if (c == null) c = 0;
           if (r == row && c == col) return (TextField) node;
       }
       return null;
   }


   // Solver (backtracking)
   private final int maxRecursionCount = 100000;
   private int recursionCount = 0;
   private boolean solveSudoku(int[][] board) {
    recursionCount = 0;
    return sudokuSolver(board);
   }

   private boolean sudokuSolver(int[][] board) {
    recursionCount++;
    if (recursionCount > maxRecursionCount) return false;
       int[] empty = findEmpty(board);
       if (empty == null) return true;
       int row = empty[0], col = empty[1];


       for (int num = 1; num <= 9; num++) {
           if (isValidPlacement(board, row, col, num)) {
               board[row][col] = num;
               if (sudokuSolver(board)) return true;
               board[row][col] = 0;
           }
       }
       return false;
   }


   private int[] findEmpty(int[][] board) {
       for (int r = 0; r < 9; r++) for (int c = 0; c < 9; c++) if (board[r][c] == 0) return new int[]{r,c};
       return null;
   }

   
   private boolean isValidPlacement(int[][] board, int row, int col, int num) {
       for (int i = 0; i < 9; i++) {
           if (board[row][i] == num) return false;
           if (board[i][col] == num) return false;
       }
       int br = row - row % 3, bc = col - col % 3;
       for (int r = br; r < br + 3; r++) for (int c = bc; c < bc + 3; c++) if (board[r][c] == num) return false;
       return true;
   }

   private void applySolutionToGrid(int[][] board, GridPane grid) {
       for (int row = 0; row < 9; row++) {
          for (int col = 0; col < 9; col++) {
           TextField tf = getTextFieldByRowColumn(row, col, grid);
           String text = Integer.toString(board[row][col]);
           tf.setText(text);
           if (tf.isEditable() == true) tf.setDisable(true);
           tf.setEditable(false);
           tf.setAlignment(Pos.CENTER);            
          }
       }
   }


   private void showErrorAlert(String message) {
       Alert alert = new Alert(Alert.AlertType.ERROR, message);
       alert.setHeaderText(null);
       alert.showAndWait();
   }

   private boolean hasConflicts(int[][] board) {
    for (int row = 0; row < 9; row++) {
      for (int col = 0; col < 9; col++) {
        int value = board[row][col];
        if (value == 0) continue;

        board[row][col] = 0;
        if (!isValidPlacement(board, row, col, value)) {
          board[row][col] = value;
          return true;
        }
        board[row][col] = value;
      }
    }
    return false;
   }

   private void resetTimer() {
    secondsElapsed = 0;
    timerLabel.setText("Time: 0:00");
    timeline.stop();
    timeline.play();
   }
   
   private void singleDigit(TextField tf) {
    UnaryOperator<TextFormatter.Change> filter = change -> {
    String enteredText = change.getControlNewText();
    if (enteredText.matches("[1-9]?")) return change;
      return null;
    };
    TextFormatter<String> singleDigit = new TextFormatter<>(filter);
    tf.setTextFormatter(singleDigit);
   }

   private void computeDifficulty() {
    if (currentPuzzle == null) {
      clues.setText("Clues: 0 (N/A)");
      return;
    }

    int count = 0;
    for (int row = 0; row < 9; row++) {
      for (int col = 0; col < 9; col++) {
        if (currentPuzzle[row][col] != 0) count++;
      }
    }
    String difficulty = "Easy";
    if (count < 36) difficulty = "Medium";
    else if (count < 27) difficulty = "Hard";
    else if (count < 19) difficulty = "Very Hard";

    clues.setText("Clues: " + count + " (" + difficulty + ")");
   }


}