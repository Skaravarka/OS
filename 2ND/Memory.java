import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Memory{
    private volatile ArrayList<Word> mem = new ArrayList<Word>();

    public Memory(){
        for(int i = 0; i < 256; i++){
            Word word = new Word();
            word = Word.stringToWord("0000");
            mem.add(word);
        }
    }

    public void PrintAll(){
       for(int i = 0; i < mem.size();i++){
           System.out.println(i +" "+Word.wordToString(mem.get(i)));
       }
    }

    public Word getInstruction(int Cc){
        return mem.get(Cc);
    }

    public void set(int value, Word word){
        mem.set(value, word);
    }

    public int loadToMemory(String fileName) {
        File file = new File(fileName);
        int cc = 0;
        int i = 0;
        Scanner scanner;
        int segmentFlag = 0; //DATA CODE HALT
        try {
            scanner = new Scanner(file);
            while(scanner.hasNextLine()){
                String data = scanner.nextLine();
                data = data.toUpperCase();
                
                if(data.equals("DATA")){
                    //cc++;
                    segmentFlag = 1;
                    continue;
                }
                if(data.equals("CODE")){
                    //cc++;
                    segmentFlag = 2;
                    continue;
                }
                if(data.equals("HALT")){
                    scanner.close();
                    return cc;
                }
                if(segmentFlag == 1){
                    cc++;
                    mem.set(i, Word.stringToWord(data));
                    i++;
                }
                if(segmentFlag == 2){
                    
                    mem.set(i, Word.stringToWord(data));
                    i++;
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
