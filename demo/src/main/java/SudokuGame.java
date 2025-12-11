import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.function.UnaryOperator;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
   // Stored current puzzle and its solved solution
   private static int[][] currentSolution = null;
   private static int[][] currentPuzzle = null;


   // Main grid reference so handlers can access it
   private GridPane sudokuGrid;


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
               sudokuGrid.add(tf, col, row);
           }
       }


       // Buttons
       Button newGameBtn = new Button("New Puzzle");
       Button solveBtn = new Button("Solve");
       Button checkBtn = new Button("Check");
       Button help = new Button("Help");
       help.setOnAction(e -> System.out.println("goon3"));


       VBox vbox = new VBox(10.0, newGameBtn, solveBtn, checkBtn, help);
       vbox.setAlignment(Pos.CENTER);


       HBox hbox = new HBox(10.0, sudokuGrid, vbox);
       hbox.setAlignment(Pos.CENTER);


       Color c = Color.rgb(103, 189, 247, 0.59);
       BackgroundFill backgroundFill = new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY);
       Background background = new Background(backgroundFill);


       BorderPane borderPane = new BorderPane();
       borderPane.setCenter(hbox);
       borderPane.setBackground(background);


       Scene scene = new Scene(borderPane, 700, 420);
       scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
       primaryStage.setScene(scene);
       primaryStage.setTitle("Sudoku Game");
       primaryStage.show();


       // Load initial puzzle
       try {
           loadRandomPuzzleFromFile();
       } catch (IOException ex) {
           showErrorAlert("Error reading boards.txt: " + ex.getMessage());
           ex.printStackTrace();
       }


       // Button actions
       newGameBtn.setOnAction(e -> {
           try {
               loadRandomPuzzleFromFile();
           } catch (IOException ex) {
               showErrorAlert("Error loading a new puzzle: " + ex.getMessage());
               ex.printStackTrace();
           }
       });


       solveBtn.setOnAction(e -> {
           if (currentSolution == null) {
               showErrorAlert("No puzzle loaded / solution not computed.");
               return;
           }
           applySolutionToGrid(currentSolution, sudokuGrid);
       });


       checkBtn.setOnAction(e -> {
           if (currentSolution == null) {
               showErrorAlert("No puzzle loaded to check.");
               return;
           }
           int[][] userBoard = getBoardFromGrid(sudokuGrid);
           if (userBoard == null) {
               showErrorAlert("Invalid entries found. Only blanks or digits 1-9 are allowed.");
               return;
           }
           boolean anyWrong = false;
           // compare only positions that are blanks in original puzzle
           for (int row = 0; row < 9; row++) {
               for (int col = 0; col < 9; col++) {  // renamed from 'c'
                   if (currentPuzzle[row][col] == 0) {
                       int userVal = userBoard[row][col];
                       int correctVal = currentSolution[row][col];
                       Node node = getTextFieldByRowColumn(row, col, sudokuGrid);
                       if (userVal != correctVal) {
                           anyWrong = true;
                           if (node instanceof TextField tf) {
                               tf.setStyle(tf.getStyle() + "-fx-background-color: #ffcccc;");
                           }
                       } else {
                           if (node instanceof TextField tf) {
                               tf.setStyle(tf.getStyle() + "-fx-background-color: #ccffcc;");
                           }
                       }
                   }
               }
           }


           if (anyWrong) {
               Alert alert = new Alert(Alert.AlertType.INFORMATION, "Incorrect entries found.");
               alert.setHeaderText(null);
               alert.showAndWait();
           } else {
               Alert alert = new Alert(Alert.AlertType.INFORMATION, "All entries correct!");
               alert.setHeaderText(null);
               alert.showAndWait();
           }
       });
   }


   public static void main(String[] args) {
       launch(args);
   }


   // File load / puzzle handling
   private void loadRandomPuzzleFromFile() throws IOException {
       String content = Files.readString(Paths.get("demo\\boards.txt"));
       String[] lines = content.split("\n");
       if (lines.length == 0) throw new IOException("boards.txt is empty.");


       Random rnd = new Random();
       String chosen = null;
       for (int tries = 0; tries < lines.length; tries++) {
           String candidate = lines[rnd.nextInt(lines.length)].trim();
           if (candidate.length() == 81) {
               chosen = candidate;
               break;
           }
       }
       if (chosen == null) {
           for (String s : lines) {
               if (s.trim().length() == 81) { chosen = s.trim(); break; }
           }
       }
       if (chosen == null) throw new IOException("No valid 81-character lines found.");


       int[][] puzzle = new int[9][9];
       for (int i = 0; i < 81; i++) {
           char ch = chosen.charAt(i);
           puzzle[i/9][i%9] = (ch >= '1' && ch <= '9') ? ch - '0' : 0;
       }


       int[][] copyForSolution = new int[9][9];
       for (int r = 0; r < 9; r++) System.arraycopy(puzzle[r], 0, copyForSolution[r], 0, 9);


       boolean solved = solveSudoku(copyForSolution);
       if (!solved) throw new IOException("Chosen puzzle appears unsolvable.");


       currentPuzzle = puzzle;
       currentSolution = copyForSolution;


       populateGridFromPuzzle(puzzle, sudokuGrid);
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
                   UnaryOperator<TextFormatter.Change> filter = change -> {
                       String enteredText = change.getControlNewText();
                       if (enteredText.matches("[1-9]?")) return change;
                       return null;
                   };
                   TextFormatter<String> singleDigit = new TextFormatter<>(filter);
                   tf.setTextFormatter(singleDigit);
               }
           }
       }
   }


   private int[][] getBoardFromGrid(GridPane grid) {
       int[][] board = new int[9][9];
       for (int row = 0; row < 9; row++) {
           for (int col = 0; col < 9; col++) {
               Node node = getTextFieldByRowColumn(row, col, grid);
               String txt = "";
               if (node instanceof TextField tf) txt = tf.getText().trim();


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


   // -----------------------------
   // Solver (backtracking)
   // -----------------------------
   private boolean solveSudoku(int[][] board) {
       int[] empty = findEmpty(board);
       if (empty == null) return true;
       int row = empty[0], col = empty[1];


       for (int num = 1; num <= 9; num++) {
           if (isValidPlacement(board, row, col, num)) {
               board[row][col] = num;
               if (solveSudoku(board)) return true;
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

   // TODO: make checked clue cells not disabled
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
}
















