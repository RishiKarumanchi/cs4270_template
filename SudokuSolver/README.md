**Program Overview:**
SudokuGame is a sudoku app that supports solving both pre-loaded and user-created puzzles. Users can click "Load Puzzle" to load a puzzle from boards.txt, a text file with lists of 81 characters with numbers for clues and 0s for blank squares. Upon doing so, a puzzle is generated in the Sudoku board on screen. It keeps its solution such that the user can click the "Solve" button to solve the puzzle or "Check" to check their works. "Solve" fills the grid with solution values. The program solves puzzles through backtracking. Users can also create their own puzzles by clicking "Create Puzzle", entering their own clues, and clicking "Finish Puzzle." Users can click Reset Board at any time to reset the board back to its original state with clues and click the Hint button to fill in one cell with the correct solution. The program also features a timer and a difficulty measure so users can compare their puzzle difficulties and times. The program uses a JavaFX GUI to display the Sudoku board. The target audience here is the hundreds of thousands of people who still play Sudoku, as well as those who want to get into this amazing game. For those who enjoy mental challenges, our project is a real game-changer. 

**Feature List:**
● Display a 9x9 Sudoku grid with JavaFX
● Allow users to click on any cell and enter numbers
● Prevent entering anything other than 1-9
● Check the puzzle constantly to see if they get it right (like NYT times)
● “Reset board” button to set the board back to the original state
● Generate a limited number of Sudoku puzzles with different levels of difficulty
● Rule-based algorithm to solve ANY valid Sudoku board as our “intelligent solver”
option
● Basic “hint” feature that puts in one correct number for the user
● The app can classify Sudokus by difficulty based on number of clues
● The app can time the user on how long it takes to solve the Sudoku themselves

The only feature from our proposal somewhat left out was that while solving a puzzle, if a user accidentally enters a number that conflicts with a clue, they cannot automatically see. Instead, they have to click the check button. However, our program does account for conflicts between clues for user-created puzzles and will display if there is a direct conflict or clues leading to an unsolvable puzzle. Additionally, we do not have many puzzles in boards.txt for now, but users can add any set of clues they want for more difficulty options or a wider variety of puzzles.

**Known Bugs/Limitations:**
boards.txt has to be kept in the same place where the app is run, not the resources folder. The code is a little messy as of now, but polish could be added if the project was to be worked on in the future. If the code was to be refactored in the future, multiple classes could be utilized to ease additions to the code.

**Step-By-Step User Guide:**
Put boards.txt in the same place where the project is run, then run the SudokuGame.java file. The JavaFX GUI should pop up. The user can click the "Load Puzzle" or "Create Puzzle" button to either load a new puzzle from a file or create one of their own. If loading a puzzle, the user can click on the text fields and input what they think is the correct solution, either checking their own solution with the "Check" button or using the "Solve" button for the program to solve the Sudoku for them. The user can also always reset the board with the "Reset" button. If creating their own puzzle, once the user is done entering clues, they can click the "Finish Puzzle" button. Then, the user-created puzzle acts just like a loaded puzzle and can be solved, checked, or auto-solved, or reset.