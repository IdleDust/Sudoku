import java.util.HashSet;
import java.util.concurrent.CountDownLatch;

public class WorkerThread implements Runnable{

    GridDemo myGridDemo;
    private String command;
    private int idx;
    private CountDownLatch cdl;

    public WorkerThread(String s, int index, GridDemo newGD, CountDownLatch cdl){
        this.cdl = cdl;
        this.command = s;
        this.idx = index;
        this.myGridDemo = newGD;
    }

    @Override
    public void run() {
        //System.out.println(Thread.currentThread().getName() + " Start. Command = " + command+" idx=" + this.idx);
        processCommand();
        cdl.countDown();
        //System.out.println(Thread.currentThread().getName() + " End.");
    }

    private void processCommand() {

        if (command.equals("row")) myGridDemo.row[idx] = processRowOrCol();
        else if (command.equals("col")) myGridDemo.col[idx] = processRowOrCol();
        else if (command.equals("sub")) myGridDemo.sub[idx] = processSub();
    }

    /*
     * Row and column validation function
     */
    private int processRowOrCol() {
        HashSet hs = new HashSet();
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            int cur;
            if(command == "row") cur = myGridDemo.grid[idx][i];
            else cur = myGridDemo.grid[i][idx];
            if (cur <= 0 || cur > 9) return 0;
            if (hs.contains(cur)) return 0;
            else {
                hs.add(cur);
                sum += cur;
            }
        }
        if (sum == 45) return 1;
        else return 0;
    }
    /*
     * Sub-grid validation function
     */
    private int processSub() {
        HashSet hs = new HashSet();
        int sum = 0;
        int[][] sub_index= {{0,0}, {0,3},{0,6},{3,0},{3,3},{3,6},{6,0},{6,3},{6,6}};
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int di = sub_index[idx][0] + i;
                int dj = sub_index[idx][1] + j;
                int cur = myGridDemo.grid[di][dj];
                if (cur <= 0 || cur > 9) return 0;
                if (hs.contains(cur)) return 0;
                else {
                    hs.add(cur);
                    sum += cur;
                }
            }
        }
        if (sum == 45) return 1;
        else return 0;
    }

    @Override
    public String toString(){
        return this.command;
    }
}
