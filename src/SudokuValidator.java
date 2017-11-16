/**
 * Created by juanchen on 11/15/17.
 */

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class SudokuValidator {
    private static final int ROWS = 9;
    private static final int COLUMNS = 9;

    private static int[][] newGrid = {
            {9,1,2,3,4,5,6,7,8},
            {1,2,3,4,5,6,7,8,9},
            {3,2,5,6,5,7,8,9,1},
            {2,3,4,5,6,8,7,9,1},
            {9,5,4,6,7,8,9,1,2},
            {3,2,5,6,5,7,8,9,1},
            {3,2,5,6,5,7,8,9,1},
            {3,2,5,6,5,7,8,9,1},
            {3,2,5,6,5,7,8,9,1}};

    static int[][] sMatrix={
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

    public static void main(String[] args) throws InterruptedException {
        GridDemo newGD = new GridDemo(sMatrix);
        long startTime = System.nanoTime();
        boolean res = validate_multiThread(newGD);
        if (res) System.out.println("Succeed!");
        else System.out.println("Fail!");
        long endTime = System.nanoTime();
        System.out.println("Multi Threading running time: " + (endTime-startTime) + "ns");

        long startTime1 = System.nanoTime();
        boolean res1 = validate_singleThread(sMatrix);
        if (res1) System.out.println("Succeed!");
        else System.out.println("Fail!");
        long endTime1 = System.nanoTime();
        System.out.println("Single Threading running time: " + (endTime1-startTime1) + "ns");
    }

    private static boolean validate_multiThread(GridDemo newGD) throws InterruptedException{
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
        System.out.println("Finished all threads");
        //newGD.printGrid();

        // loop through results array
        for (int i = 0; i < 9; i++) {
            if (newGD.row[i] == 0) return false;
            if (newGD.col[i] == 0) return false;
            if (newGD.sub[i] == 0) return false;
        }
        return true;
    }

    private static boolean processRowOrCol(String command, int idx, int[][] grid) {
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

    private static boolean processSubGrid(int idx, int[][] grid) {
        HashSet hs = new HashSet();
        int sum = 0;
        int[][] sub_index= {{0,0}, {0,3},{0,6},{3,0},{3,3},{3,6},{6,0},{6,3},{6,6}};
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
    private static boolean validate_singleThread(int[][] grid) {
        boolean flag = true;
        for (int i = 0; i < ROWS; i++) {
            if (!processRowOrCol("row", i, grid)) flag = false;
            if (!processRowOrCol("col", i, grid)) flag = false;
            if (!processSubGrid(i, grid)) flag = false;
        }
        return flag;
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