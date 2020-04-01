import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Memory{
    private ArrayList<Word> realMemory = new ArrayList<Word>();

    public void PrintAll(){
       for(int i = 0; i < this.realMemory.size();i++){
           System.out.println(i+Word.wordToString(this.realMemory.get(i)));
       }
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
                    String info = scanner.nextLine();
                    
                    //System.out.println("final" + Word.wordToString(Word.stringToWord("ABCD")));
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