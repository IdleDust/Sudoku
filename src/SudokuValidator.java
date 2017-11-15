/**
 * Created by juanchen on 11/15/17.
 */

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class SudokuValidator {
    private static int[][] newGrid = { {9, 1, 2, 3, 4, 5, 6, 7, 8},
            {1,2,3,4,5,6,7,8,9},
            {3,2,5,6,5,7,8,9,1},
            {2,3,4,5,6,8,7,9,1},
            {9,5,4,6,7,8,9,1,2},
            {3,2,5,6,5,7,8,9,1},
            {3,2,5,6,5,7,8,9,1},
            {3,2,5,6,5,7,8,9,1},
            {3,2,5,6,5,7,8,9,1}};

    public static void main(String[] args) throws InterruptedException{
        CountDownLatch latch = new CountDownLatch(27);
        GridDemo newDemo = new GridDemo(newGrid);
        newDemo.printGrid();
        for (int i = 0; i < 3*9; i++) {
            Thread t;
            if (i < 9) {
                t = new Thread(new WorkerThread("row", i, newDemo, latch));
            }
            else if ( i < 18) {
                t = new Thread(new WorkerThread("col", i-9, newDemo, latch));
            }
            else {
                t = new Thread(new WorkerThread("sub", i-18, newDemo, latch));
            }
            t.start();
        }
        latch.await();
        System.out.println("After: ");
        newDemo.printGrid();
        System.out.println("Finished all threads");
    }
}

class GridDemo {
    int[][] grid;
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