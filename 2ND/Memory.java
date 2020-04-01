import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Memory{
    private static ArrayList<Word> realMemory = new ArrayList<Word>();

    static int pointer=0;

    public void PrintAll(){
       for(int i = 0; i < realMemory.size();i++){
           System.out.println(i+" "+Word.wordToString(realMemory.get(i)));
       }
    }

    public static Word getNext(){
        pointer = pointer +1;
        return realMemory.get(pointer-1);
    }

    public static int[] getNumbersFromWord(){
        String string = Word.wordToString(getNext());
        String x1 = string.substring(0,2);
        String x2 = string.substring(2,4);
        int[] arr = {Integer.parseInt(x1),Integer.parseInt(x2)};
        return arr;
    }

    public int loadToMemory(String fileName) {
        File file = new File(fileName);
        Scanner scanner;
        int segmentFlag = 0; //DATA CODE HALT
        try {
            scanner = new Scanner(file);
            while(scanner.hasNextLine()){
                String data = scanner.nextLine();
                data.toUpperCase();
                if(data.equals("CODE")){
                    segmentFlag = 2;
                    continue;
                }
                if(data.equals("HALT")){
                    scanner.close();
                    return -1;
                }
                if(segmentFlag == 2){
                    realMemory.add(Word.stringToWord(data));
                }
            }
            scanner.close();
            return -1;

        } catch (FileNotFoundException e) {
            
            e.printStackTrace();
        }
        return -1;
    }
}