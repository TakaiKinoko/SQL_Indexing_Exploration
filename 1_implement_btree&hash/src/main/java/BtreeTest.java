import btree.*;
import util.*;
import pair.*;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Description: This class contains all the test cases for BTree
 */
public class BtreeTest {
    /**
     * Main Entry for the test
     *
     * @param args
     */
    private List<Pair<Integer, Integer>> data;   // store  index <-> [key, value]
    private BTree<Integer, Integer> db;    // store [key, index]
    private BTTestIteratorImpl<Integer, Integer> iter;

    /**
     * Constructor
     */
    public BtreeTest(String path, BufferedWriter tableWriter) throws Exception{
        db = new BTree<Integer, Integer>();
        data = new ArrayList<>();
        iter = new BTTestIteratorImpl<Integer, Integer>();
        readFile(path, tableWriter);
    }

    public void listItems(BufferedWriter tableWriter) throws IOException {
        iter = new BTTestIteratorImpl<Integer, Integer>();
        db.list(iter, data, tableWriter);
    }

    public void printSize(BufferedWriter tableWriter) throws  IOException{
        tableWriter.write("Current number of items in B-tree DB is: " + db.size());
        tableWriter.newLine();
    }

    public void readFile(String path, BufferedWriter tableWriter) throws Exception {
        File file = new File(path);

        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        int index = 0;
        while ((st = br.readLine()) != null){
            String[] strs = st.split("\\|"); // use escape character!
            try {
                int key = Integer.valueOf(strs[0]);
                int val = Integer.valueOf(strs[1]);
                data.add(new Pair<Integer, Integer>(key, val));
                this.db.insert(key, index);
                index++;
            }catch(Exception e){  // handles abnormal lines e.g. the 1st (column names)
                continue;
            }
        }

        tableWriter.write("\n=========================================\n"
                + "   INITIAL SIZE OF DATABASE IS " + db.size() +
                "\n=========================================\n");
    }

    public void insert(Integer key, Integer value, BufferedWriter tableWriter)throws IOException{
        int ind;
        Integer pos = db.search(key);
        // key doesn't exist
        if(pos == null){
            ind = data.size();
            db.insert(key, ind);
            data.add(new Pair<Integer, Integer>(key, value));
            tableWriter.write("\nKEY:" + key + " VALUE:" + value + " inserted.");
            tableWriter.newLine();
        }
        // insert when key has previously been inserted
        else if(data.get(pos).deleted()){
            data.get(pos).update(value);
            tableWriter.write("\nKEY:" + key + " VALUE:" + value + " inserted.");
            tableWriter.newLine();
        }
        // upsert
        else{
            int oldval = data.get(pos).getValue();
            data.get(pos).update(value);
            tableWriter.write("\nVALUE of KEY:" + key + " updated from " + oldval + " to " + value);
            tableWriter.newLine();
        }
        printSize(tableWriter);
    }

    public void delete(Integer key, BufferedWriter tableWriter)throws IOException{
        Integer ind = db.delete(key);
        if(ind == null || data.get(ind).deleted()) {
            tableWriter.write("\nThe KEY:" + key + " doesn't exist in the database.");
            tableWriter.newLine();
        }
        else if(data.get(ind).remove()){
            // successfully mark the entry in data array as deleted
            tableWriter.write("\nKEY: " + key + " deleted!");
            tableWriter.newLine();
        }else {
            tableWriter.write("\nERROR while deleting!");
            tableWriter.newLine();
        }
        return;
    }

    public void search(Integer key, BufferedWriter tableWriter) throws IOException{
        Integer ind = db.search(key);
        if(ind == null || data.get(ind).deleted()) {
            tableWriter.write("\nThe KEY:" + key + " doesn't exist in the database.");
            tableWriter.newLine();
        }
        else {
            tableWriter.write("\nVALUE of the given KEY:" + key + " is: " + data.get(ind).getValue());
            tableWriter.newLine();
        }
        return;
    }

    public void checkTree(BufferedWriter tableWriter) throws IOException {
        listItems(tableWriter);
        tableWriter.newLine();
    }

    public void readCommands(String path, BufferedWriter timingWriter, BufferedWriter tableWriter) throws Exception {
        File file = new File(path);
        Pattern ins_pattern = Pattern.compile("^insert");
        Pattern del_pattern = Pattern.compile("^delete");
        Pattern ser_pattern = Pattern.compile("^search");
        Matcher ins_m, del_m, ser_m, integer_m;
        long startTime, endTime, timeElapsed;

        BufferedReader br = new BufferedReader(new FileReader(file));

        long totalstartTime = System.nanoTime();
        String st;
        while ((st = br.readLine()) != null) {
            ins_m = ins_pattern.matcher(st);
            del_m = del_pattern.matcher(st);
            ser_m = ser_pattern.matcher(st);
            integer_m = Pattern.compile("\\d+").matcher(st);
            Integer[] numbers = new Integer[2];
            int i = 0;
            while(integer_m.find()) {
                numbers[i++] = (Integer.parseInt(integer_m.group()));
            }
            if (ins_m.find()) {
                startTime = System.nanoTime();
                insert(numbers[0], numbers[1], tableWriter);
                endTime = System.nanoTime();
                timeElapsed = endTime - startTime;
                timingWriter.write("btree insert costs: " + timeElapsed + " nanoseconds.\n");
                timingWriter.newLine();
            } else if (del_m.find()) {
                startTime = System.nanoTime();
                delete(numbers[0], tableWriter);
                    // if delete was successful, print out the updated hashmap
                printSize(tableWriter);
                endTime = System.nanoTime();
                timeElapsed = endTime - startTime;
                timingWriter.write("btree delete costs: " + timeElapsed + " nanoseconds.\n");
                timingWriter.newLine();
            } else if (ser_m.find()) {
                startTime = System.nanoTime();
                search(numbers[0], tableWriter);
                endTime = System.nanoTime();
                timeElapsed = endTime - startTime;
                timingWriter.write("btree search costs: " + timeElapsed + " nanoseconds.\n");
                timingWriter.newLine();
            }
        }

        long totalendTime = System.nanoTime();
        tableWriter.write("\n=========================================\n"
                + "TOTAL TIME ELAPSED: " + (totalendTime - totalstartTime) + " nanoseconds");


        tableWriter.write("\n=========================================\n"
                + "RESULTING DATABASE SIZE: " + db.size() +
                "\n=========================================\n");

        checkTree(tableWriter);
        timingWriter.write("\n=========================================\n"
                + "TOTAL TIME ELAPSED: " + (totalendTime - totalstartTime) + " nanoseconds" +
                "\n=========================================\n");
    }

    public static void main(String[] args) throws Exception{
        String path = args[0];
        String commandsPath = args[1];
        BufferedWriter tableWriter = new BufferedWriter(new FileWriter("../../../output/btree_resultTable.txt"));
        BufferedWriter timingWriter = new BufferedWriter(new FileWriter("../../../output/btree_timing.txt"));
        BtreeTest t = new BtreeTest(path, tableWriter);

        t.readCommands(commandsPath, timingWriter, tableWriter);
        tableWriter.close();
        timingWriter.close();
    }

    /**
     * Inner class to implement BTree iterator
     */
    class BTTestIteratorImpl<K extends Comparable, V> implements BTIteratorIF<K, V> {
        private K mCurrentKey;
        private K mPreviousKey;
        private boolean mStatus;

        public BTTestIteratorImpl() {
            reset();
        }

        @Override
        public boolean item(K key, V value) {
            mCurrentKey = key;
            if ((mPreviousKey != null) && (mPreviousKey.compareTo(key) > 0)) {
                mStatus = false;
                return false;
            }
            mPreviousKey = key;
            return true;
        }

        public boolean getStatus() {
            return mStatus;
        }

        public K getCurrentKey() {
            return mCurrentKey;
        }

        public final void reset() {
            mPreviousKey = null;
            mStatus = true;
        }
    }
}