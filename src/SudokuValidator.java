import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class SudokuValidator {
    private static final int ROWS = 9;
    private static final int COLUMNS = 9;

    private static int[][] grid0 = {
            {9,1,2,3,4,5,6,7,8},
            {1,2,3,4,5,6,7,8,9},
            {3,2,5,6,5,7,8,9,1},
            {2,3,4,5,6,8,7,9,1},
            {9,5,4,6,7,8,9,1,2},
            {3,2,5,6,5,7,8,9,1},
            {3,2,5,6,5,7,8,9,1},
            {3,2,5,6,5,7,8,9,1},
            {3,2,5,6,5,7,8,9,1}
    };

    static int[][] grid1 = {
            {5,3,4,6,7,8,9,1,2},
            {6,7,2,1,9,5,3,4,8},
            {1,9,8,3,4,2,5,6,7},
            {8,5,9,7,6,1,4,2,3},
            {4,2,6,8,5,3,7,9,1},
            {7,1,3,9,2,4,8,5,6},
            {9,6,1,5,3,7,2,8,4},
            {2,8,7,4,1,9,6,3,5},
            {3,4,5,2,8,6,1,7,9}
    };

    static int[][] grid2 = {{1 ,2 ,3, 4, 5, 6, 7, 8 ,9},
            {4 ,5, 6, 7 ,8 ,9 ,1 ,2 ,3},
            {7, 8, 9 ,1, 2, 3 ,4 ,5 ,6},
            {2, 1 ,4, 3 ,6 ,5 ,8, 9 ,7},
            {3 ,6, 5 ,8, 9 ,7, 2 ,1, 4},
            {8, 9 ,7, 2 ,1 ,4 ,3, 6 ,5},
            {5, 3, 1 ,6 ,4 ,2, 9 ,7 ,8},
            {6 ,4 ,8 ,9 ,7 ,1, 5 ,3, 2},
            {9 ,7 ,2, 5 ,3 ,8, 6, 4 ,1}};



    public static void main(String[] args) {
        try {
            solve(grid0);
            solve(grid1);
            solve(grid2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static void solve(int[][] curGrid) throws InterruptedException {
        GridDemo newGD = new GridDemo(curGrid);
        long startTime = System.nanoTime();
        boolean res = multi_thread_validate(newGD);
        if (res) System.out.println("Sudoku Valid!!");
        else System.out.println("Sudoku Unvalid!");
        long endTime = System.nanoTime();
        System.out.println("Multi Threading running time: " + (endTime-startTime) + "ns");

        long startTime1 = System.nanoTime();
        boolean res1 = single_thread_validate(curGrid);
        if (res1) System.out.println("Sudoku Valid!");
        else System.out.println("Sudoku Unvalid!");
        long endTime1 = System.nanoTime();
        System.out.println("Single Threading running time: " + (endTime1-startTime1) + "ns");

    }

    /**
     * validate Sudoku using multi-threading
     * create 9 threads for rows checking, 9 threads for columns checking
     * 9 threads for sub-grid checking
     * wait until all threads exit and check the result array(row[], col[], sub[])
     */
    private static boolean multi_thread_validate(GridDemo newGD) throws InterruptedException{
        CountDownLatch latch = new CountDownLatch(27);
        for (int i = 0; i < 3*9; i++) {
            Thread t;
            if (i < 9) {
                t = new Thread(new WorkerThread("row", i, newGD, latch));
            }
            else if ( i < 18) {
                t = new Thread(new WorkerThread("col", i-9, newGD, latch));
            }
            else {
                t = new Thread(new WorkerThread("sub", i-18, newGD, latch));
            }
            t.start();
        }
        // Wait until all threads exit
        latch.await();
        // System.out.println("Finished all threads");
        // newGD.printGrid();

        // check result arrays
        for (int i = 0; i < 9; i++) {
            if (newGD.row[i] == 0) return false;
            if (newGD.col[i] == 0) return false;
            if (newGD.sub[i] == 0) return false;
        }
        return true;
    }

    /**
     *check rows, cols, and sub-grids one by one to determine the validation
     */
    private static boolean single_thread_validate(int[][] grid) {
        boolean flag = true;
        for (int i = 0; i < ROWS; i++) {
            if (!process_row_or_col("row", i, grid)) flag = false;
            if (!process_row_or_col("col", i, grid)) flag = false;
            if (!process_subgrid(i, grid)) flag = false;
        }
        return flag;
    }

    private static boolean process_row_or_col(String command, int idx, int[][] grid) {
        HashSet hs = new HashSet();
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            int cur;
            if(Objects.equals(command, "row")) cur = grid[idx][i];
            else cur = grid[i][idx];
            if (cur <= 0 || cur > 9) return false;
            if (hs.contains(cur)) return false;
            else {
                hs.add(cur);
                sum += cur;
            }
        }
        if (sum == 45) return true;
        else return false;
    }

    private static boolean process_subgrid(int idx, int[][] grid) {
        HashSet hs = new HashSet();
        int sum = 0;
        int[][] sub_index = {{0, 0}, {0, 3}, {0, 6}, {3, 0}, {3, 3}, {3, 6}, {6, 0}, {6, 3}, {6, 6}};
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int di = sub_index[idx][0] + i;
                int dj = sub_index[idx][1] + j;
                int cur = grid[di][dj];
                if (cur <= 0 || cur > 9) return false;
                if (hs.contains(cur)) return false;
                else {
                    hs.add(cur);
                    sum += cur;
                }
            }
        }
        if (sum == 45) return true;
        else return false;
    }
}

class GridDemo {
    int[][] grid;
    // validation results for each row, col, sub-grid
    int row[];
    int col[];
    int sub[];

    GridDemo(int[][] newGrid) {
        grid = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                grid[i][j] = newGrid[i][j];
            }
        }
        row = new int[9];
        Arrays.fill(row, -1);
        col = new int[9];
        Arrays.fill(col, -1);
        sub = new int[9];
        Arrays.fill(sub, -1);
    }

    void printGrid() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
        System.out.print("row: ");
        for(int i = 0; i < 9; i++) System.out.print(row[i] + " ");
        System.out.println();
        System.out.print("col: ");
        for(int i = 0; i < 9; i++) System.out.print(col[i] + " ");
        System.out.println();
        System.out.print("sub: ");
        for(int i = 0; i < 9; i++) System.out.print(sub[i] + " ");
        System.out.println();
    }
}