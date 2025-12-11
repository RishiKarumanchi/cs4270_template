public class SudokuGrid {
   private int[][] grid;


   public SudokuGrid(int[][] grid) {
       this.grid = grid;
   }


   public int[][] getGrid() {
       return grid;
   }


   public void setGrid(int[][] newGrid) {
       this.grid = newGrid;
   }


   /**
    * Example utility: fillCell modifies a single cell in a grid
    * if the position is valid. You can expand this later.
    */
   public static void fillCell(int[][] grid, int row, int col, int value) {
       if (row < 0 || row > 8 || col < 0 || col > 8) return;
       if (value < 0 || value > 9) return; // 0 means blank
       grid[row][col] = value;
   }
}

