import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Memory{
    private volatile ArrayList<Word> mem = new ArrayList<Word>();

    public Memory(){
        for(int i = 0; i < 16; i++){
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

    public void printAllNicely(int start){
        start = start * 16;
        String temp;
       // for(int i = 0; i < 16; i++){
        temp = Integer.toHexString(start);
        while(temp.length() < 3){
            temp = "0" + temp;
        }
        System.out.print(temp + " ");
        for(int j = 0; j < 16; j++){
            System.out.print(Word.wordToString(mem.get(j)) + " ");
        }
        System.out.println("");
      //  }
    }

    public Word getInstruction(int Cc){
        return mem.get(Cc);
    }

    public void set(int value, Word word){
        mem.set(value, word);
    }

    public int getInstructionCount(String fileName){
        File file = new File(fileName);
        int cc = 0;
        Scanner scanner;
        int segmentFlag = 0; //DATA CODE HALT
        try {
            scanner = new Scanner(file);
            while(scanner.hasNextLine()){
                String data = scanner.nextLine();
                data = data.toUpperCase();

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
                    return cc;
                }
                if(segmentFlag == 1){
                    cc++;
                    continue;
                }
                if(segmentFlag == 2){
                    continue;
                }
            }
            scanner.close();
            return -1;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int loadToMemory(String fileName, int startPoint) {
        File file = new File(fileName);
        int cc = 0;
        int i = 0;
        Scanner scanner;
        int segmentFlag = 0; //DATA CODE HALT
        try {
            scanner = new Scanner(file);
            while(startPoint != 0){
                startPoint -= 1;
                String data = scanner.nextLine();
                //System.out.println(data);
                if(data.equals("DATA")){
                    segmentFlag = 1;
                    continue;
                }
                if(data.equals("CODE")){
                    segmentFlag = 2;
                    continue;
                }
            }
            while(scanner.hasNextLine() && i < 16){
                String data = scanner.nextLine();
                data = data.toUpperCase();
                
                if(data.equals("DATA")){
                    //cc++;
                    mem.set(i, Word.stringToWord(data));
                    i++;
                    segmentFlag = 1;
                    continue;
                }
                if(data.equals("CODE")){
                    //cc++;
                    mem.set(i, Word.stringToWord(data));
                    i++;
                    segmentFlag = 2;
                    continue;
                }
                if(data.equals("HALT")){
                    mem.set(i, Word.stringToWord("HALT"));
                    scanner.close();
                    return cc;
                }
                if(segmentFlag == 1){
                    cc++;
                    mem.set(i, Word.stringToWord(data));
                    i++;
                }
                if(segmentFlag == 2){
                    //System.out.println(data);
                    mem.set(i, Word.stringToWord(data));
                    i++;
                }
            }
            scanner.close();
            if(i > 15){
                return -1;
            }
            return cc;

        } catch (FileNotFoundException e) {
            
            e.printStackTrace();
        }
        return -1;
    }
    public int getSize(){
        return mem.size();
    }
}
