import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class VirtualMachine {

    private ArrayList<Word> virtualMemory = new ArrayList<Word>();

    private int loaded;
    private int ax;
    private int bx;
    private int cc;
    private int sf;
    private int mp;

    public VirtualMachine(int loaded, int ax, int bx, int cc, int sf, int mp) {
        setClearMemory();

        this.loaded = loaded;
        this.ax = ax;
        this.bx = bx;
        this.cc = cc;
        this.sf = sf;
        this.mp = mp;
    }
    public void printVirtualMemory(){
        for(int i = 0; i < 256; i++){
            System.out.println(i + " " + Word.wordToString(virtualMemory.get(i)));
        }
    }
    private Word getMemoryCell(int index){
        return virtualMemory.get(index);
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

    private void setClearMemory(){
        for(int i = 0; i < 256; i++){
            Word word = new Word();
            word = Word.stringToWord("0000");
            virtualMemory.add(word);
        }
    }

    public void DoNextInstruction(){
        String string = Word.wordToString(Memory.getNext());

        if (string.contains("ADD ")){
            int[] arr = Memory.getNumbersFromWord();
            System.out.println("Adding "+arr[0]+"+"+arr[1]+"="+ADD(arr[0], arr[1]));
            return;
        }
        if (string.contains("SUB ")){
            int[] arr = Memory.getNumbersFromWord();
            System.out.println("Sub "+arr[0]+"-"+arr[1]+"="+SUB(arr[0], arr[1]));
            return;
        }
        if (string.contains("MOR ")){
            //MOR();
            return;
        }
        if (string.contains("EQL ")){
            //EQL();
            return;
        }
        if (string.contains("MOV ")){
            MOV();
            return;
        }
        if (string.contains("LEA ")){
            LEA();
            return;
        }
        if (string.contains("GET ")){
            GET();
            return;
        }
        if (string.contains("PRR ")){
            PRR();
            return;
        }
        if (string.contains("PRS ")){
            PRS();
            return;
        }
        if (string.contains("WGD ")){
            WGD();
            return;
        }
        if (string.contains("RGD ")){
            RGD();
            return;
        }
        if (string.contains("LGD ")){
            LGD();
            return;
        }
        if (string.contains("UGD ")){
            UGD();
            return;
        }
        if (string.contains("JMP ")){
            JMP();
            //setCc(cc - arr);
            return;
        }
        if (string.contains("JEZ ")){
            JEZ();
            return;
        }
        if (string.contains("JNZ ")){
            JNZ();
            return;
        }
        if (string.contains("JGZ ")){
            JGZ();
            return;
        }
        if (string.contains("JLZ ")){
            JLZ();
            return;
        }
        if (string.contains("END ")){
            END();
            return;
        }
        System.out.println("Kaska neto ivedei");
    }

    public int getLoaded(){
        return loaded;
    }
    public int getAx(){
        return ax;
    }
    public int getBx(){
        return bx;
    }
    public int getCc(){
        return cc;
    }
    public int getSf(){
        return sf;
    }
    public int getMp(){
        return mp;
    }
    public void setLoaded(int loaded){
        this.loaded = loaded;
    }
    public void setAx(int ax){
        this.ax = ax;
    }
    public void setBx(int bx){
        this.bx = bx;
    }
    public void setCc(int cc){
        this.cc = cc;
    }
    public void setSf(int sf){
        this.sf = sf;
    }
    public void setMp(int mp){
        this.mp = mp;
    }

    //Aritmetines komandos
    private int ADD(int x1, int x2){
        return x1+x2;
    }

    private int SUB(int x1, int x2){
        return x1-x2;
    }
    //Palyginimo komandos
    private boolean MOR(int x1, int x2){
        if(x1>x2)
            return true;
        else
            return false;
    }

    private boolean EQL(int x1, int x2){
        if(x1==x2)
            return true;
        else
            return false;
    }
    //Darbui su atmintimi komandos
    private void MOV(){

    }

    private void LEA(){

    }
    //Ivedimo/Isvedimo komandos
    private void GET(){

    }

    private void PRR(){

    }

    private void PRS(){

    }
    //Darbo su bendra atminties sritimi komandos
    private void WGD(){

    }

    private void RGD(){

    }
    //Semaforu komandos
    private void LGD(){

    }

    private void UGD(){

    }
    //Salyginiai ir besalyginiai valdymo perdavimai komandos
    private void JMP(){

    }

    private void JEZ(){

    }

    private void JNZ(){

    }

    private void JGZ(){

    }

    private void JLZ(){

    }
    //Besalyginio programos sustabdymo komandos
    private void END(){

    }
}