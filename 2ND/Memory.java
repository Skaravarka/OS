import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Memory{
    private ArrayList<Word> virtualMemory = new ArrayList<Word>();

    public void PrintAll(){
       for(int i = 0; i < this.virtualMemory.size();i++){
           System.out.println(Word.wordToString(this.virtualMemory.get(i)));
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
                if(segmentFlag == 1){
                    System.out.println(data);
                    String[] parts = data.split(" ");
                    //virtualMemory.set(Integer.parseInt(parts[1]), parts[2]);
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