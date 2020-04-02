import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Memory{
    private static ArrayList<Word> realMemory = new ArrayList<Word>();

    public void PrintAll(){
       for(int i = 0; i < realMemory.size();i++){
           System.out.println(i+" "+Word.wordToString(realMemory.get(i)));
       }
    }

    public static Word getInstruction(int Cc){
        return realMemory.get(Cc);
    }

    public static int getInstructionCount(){
        return realMemory.size()-1;
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
    public int loadToMemory(String fileName) {
        File file = new File(fileName);
        Scanner scanner;
        int segmentFlag = 0; //DATA CODE HALT
        try {
            scanner = new Scanner(file);
            while(scanner.hasNextLine()){
                String data = scanner.nextLine();
                data.toUpperCase();
                //System.out.println(data);
                if(data.equals("DATA")){
                    segmentFlag = 1;
                    continue;
                }
                if(data.equals("CODE")){
                    segmentFlag = 2;
                    continue;
                }
                if(data.equals("HALT")){
                    scanner.close();
                    return -1;
                }
                if(segmentFlag == 1){
                    //System.out.println(data);
                    String[] parts = data.split(" ");
                    while(parts[1].length() != 4){
                        parts[1] = " " + parts[1];
                    }
                    virtualMemory.set(Integer.parseInt(parts[0]), Word.stringToWord(parts[1]));
                    //System.out.println("final" + Word.wordToString(Word.stringToWord("ABCD")));
                }
                if(segmentFlag == 2){

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
