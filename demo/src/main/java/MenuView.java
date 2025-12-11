public class MenuView {
   public static boolean solve(int[][] board) {
       for (int row = 0; row < 9; row++) {
           for (int col = 0; col < 9; col++) {
               if (board[row][col] == 0){
                   for (int num = 1; num <= 9; num++){
                       if (isValid(board, row, col, num)){
                           board[row][col] = num;


                           if (solve(board)) {
                               return true;
                           }


                           board[row][col] = 0;
                       }
                   }


                   return false;
               }
           }
       }
       return true;
   }


   public static boolean isValid(int[][] board, int row, int col, int num) {
       for (int i = 0; i < 9; i++) {
           if (board[row][i] == num) return false;
       }
       for (int i = 0; i < 9; i++) {
           if (board[i][col] == num) return false;
       }


       int boxRow = row - row%3;
       int boxCol = col - col%3;


       for (int r = boxRow ; r < boxRow + 3; r++){
           for (int c = boxCol; c < boxCol + 3; c++){
               if (board[r][c] == num) return false;
           }
       }


       return true;
   }




   public static void printBoard(int[][] board){
       for (int r = 0; r < 9; r++){
           for (int c = 0; c < 9; c++){
               System.out.print(board[r][c] + " ");
           }
           System.out.println();
       }
   }
}



