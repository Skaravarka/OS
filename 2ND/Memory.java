import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Memory{
    private static ArrayList<Word> Memory = new ArrayList<Word>();

    public Memory(){
        for(int i = 0; i < 256; i++){
            Word word = new Word();
            word = Word.stringToWord("0000");
            Memory.add(word);
        }
    }

    public void PrintAll(){
       for(int i = 0; i < Memory.size();i++){
           System.out.println(i+" "+Word.wordToString(Memory.get(i)));
       }
    }

    public static Word getInstruction(int Cc){
        return Memory.get(Cc);
    }

    public static int getInstructionCount(){
        return Memory.size()-1;
    }

    public int loadToMemory(String fileName) {
        File file = new File(fileName);
        int cc = 0;
        Scanner scanner;
        int segmentFlag = 0; //DATA CODE HALT
        try {
            scanner = new Scanner(file);
            while(scanner.hasNextLine()){
                String data = scanner.nextLine();
                data.toUpperCase();
                if(data.equals("DATA")){
                    cc++;
                    segmentFlag = 1;
                    continue;
                }
                if(data.equals("CODE")){
                    cc++;
                    segmentFlag = 2;
                    continue;
                }
                if(data.equals("HALT")){
                    scanner.close();
                    return -1;
                }
                if(segmentFlag == 1){
                    //System.out.println(data);
                    cc++;
                    // String[] parts = data.split(" ");
                    
                    // while(parts[1].length() != 4){
                    //     parts[1] = " " + parts[1];
                    // }
                    // Memory.set(Integer.parseInt(parts[0]), Word.stringToWord(parts[1]));
                    // //System.out.println("final" + Word.wordToString(Word.stringToWord("ABCD")));
                    Memory.add(Word.stringToWord(data));
                }
                if(segmentFlag == 2){
                    Memory.add(Word.stringToWord(data));
                }
            }
            scanner.close();
            return cc;

        } catch (FileNotFoundException e) {
            
            e.printStackTrace();
        }
        return -1;
    }
}
