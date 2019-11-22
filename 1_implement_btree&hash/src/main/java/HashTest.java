import hash.*;
import util.*;
import pair.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

public class HashTest {
    private List<Pair<Integer, Integer>> data;   // store  index <-> [value]
    private HashMap<Integer, Integer> db_chaining;  // store [key, index]

    public HashTest(String path, BufferedWriter tableWriter) throws Exception {
        db_chaining = new HashMap(HashMap.Type.CHAINING);
        data = new ArrayList<>();
        readFile(path, tableWriter);
    }

    public void readFile(String path, BufferedWriter tableWriter) throws Exception {
        File file = new File(path);

        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        int index = 0;
        while ((st = br.readLine()) != null) {
            String[] strs = st.split("\\|"); // use escape character!
            try {
                int key = Integer.valueOf(strs[0]);
                int val = Integer.valueOf(strs[1]);
                data.add(new Pair(key, val));
                db_chaining.put(key, index);
                index++;
            } catch (Exception e) {  // handles abnormal lines e.g. the 1st (column names)
                continue;
            }
        }

        tableWriter.write("\n=========================================\n"
                + "   INITIAL SIZE OF DATABASE IS " + db_chaining.size() +
                "\n=========================================\n");
        tableWriter.newLine();
    }

    public void printSize(BufferedWriter tableWriter) throws IOException {
        tableWriter.write("Current number of items in HashMap DB is: " + db_chaining.size());
        tableWriter.newLine();
    }

    public void insert(Integer key, Integer value, BufferedWriter tableWriter) throws IOException{
        int ind;
        if(db_chaining.contains(key)) {
            ind = db_chaining.get(key);

            if(data.get(ind).deleted()) {
                data.get(ind).update(value);
                tableWriter.write("\nKEY:" + key + " VALUE:" + value + " inserted.");
                tableWriter.newLine();
            }
            else{
                int old = data.get(ind).getValue();
                data.get(ind).update(value);
                tableWriter.write("\nVALUE of KEY:" + key + " updated from " + old + " to " + value);
                tableWriter.newLine();
            }
        }else {
            ind = data.size();
            db_chaining.put(key, ind);
            data.add(new Pair(key, value));
            tableWriter.write("\nKEY:" + key + " VALUE:" + value + " inserted.");
            tableWriter.newLine();
        }
    }

    public boolean delete(Integer key, BufferedWriter tableWriter) throws IOException{
        Integer ind = db_chaining.remove(key);
        // case 1: key doesn't exist or has already been deleted
        if(ind == null || data.get(ind).deleted()) {
            tableWriter.write("\nThe KEY:" + key + " doesn't exist in the database.");
            tableWriter.newLine();
            return false;
        }
        // case 2: key present and successfully deleted
        if(data.get(ind).remove()){
            tableWriter.write("\nKEY: " + key + " deleted!");
            tableWriter.newLine();
            return true;
        }else {
            tableWriter.write("\nERROR while deleting!");
            tableWriter.newLine();
            return false;
        }
    }

    public void search(Integer key, BufferedWriter tableWriter) throws IOException{
        Integer ind = db_chaining.get(key);
        if(ind == null || data.get(ind).deleted()) {
            tableWriter.write("\nThe KEY:" + key + " doesn't exist in the database.");
            tableWriter.newLine();
        }
        else{
            tableWriter.write("\nVALUE of the given KEY:" + key + " is: " + data.get(ind).getValue());
            tableWriter.newLine();
        }
        return;
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
                printSize(tableWriter);
                endTime = System.nanoTime();
                timeElapsed = endTime - startTime;
                timingWriter.write("hash insert costs: " + timeElapsed + " nanoseconds.\n");
                timingWriter.newLine();
            } else if (del_m.find()) {
                startTime = System.nanoTime();
                if(delete(numbers[0], tableWriter)){
                    // if delete was successful, print out the updated hashmap
                    printSize(tableWriter);
                    endTime = System.nanoTime();
                    timeElapsed = endTime - startTime;
                    timingWriter.write("hash delete costs: " + timeElapsed + " nanoseconds.\n");
                    timingWriter.newLine();
                }
            } else if (ser_m.find()) {
                startTime = System.nanoTime();
                search(numbers[0], tableWriter);
                endTime = System.nanoTime();
                timeElapsed = endTime - startTime;
                timingWriter.write("hash search costs: " + timeElapsed + " nanoseconds.\n");
                timingWriter.newLine();
            }
        }

        long totalendTime = System.nanoTime();
        tableWriter.write("\n=========================================\n"
                + "TOTAL TIME ELAPSED: " + (totalendTime - totalstartTime) + " nanoseconds");

        tableWriter.write("\n=========================================\n"
                + "RESULTING DATABASE SIZE: " + db_chaining.size() +
                        "\n=========================================\n");

        timingWriter.write("\n=========================================\n"
                + "TOTAL TIME ELAPSED: " + (totalendTime - totalstartTime) + " nanoseconds" +
                "\n=========================================\n");

        String[] entries = db_chaining.toString().split(",");
        for(String e: entries) {
            try{
                String[] strs = e.trim().split("=");
                int key = Integer.valueOf(strs[0]);
                int ind = Integer.valueOf(strs[1]);
                tableWriter.write("Key: " + key + " Value: " + data.get(ind).getValue());
                tableWriter.newLine();
            }catch(Exception ex){
                continue;
            }
        }
    }

    public static void main(String[] args) throws Exception{
        String inPath = args[0];
        String commandsPath = args[1];
        BufferedWriter tableWriter = new BufferedWriter(new FileWriter("../../../output/hash_resultTable.txt"));
        BufferedWriter timingWriter = new BufferedWriter(new FileWriter("../../../output/hash_timing.txt"));
        HashTest t = new HashTest(inPath, tableWriter);

        t.readCommands(commandsPath, timingWriter, tableWriter);
        tableWriter.close();

        timingWriter.close();
    }

}
