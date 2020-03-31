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

    public void loadToMemory(String fileName) {
        File file = new File(fileName);
        Scanner scanner;
        int segmentFlag = 0; //DATA CODE HALT
        try {
            scanner = new Scanner(file);
            while(scanner.hasNextLine()){
                String data = scanner.nextLine();
                data.toUpperCase();
                
                if(data == "DATA"){
                    segmentFlag = 1;
                    continue;
                }
                if(data == "CODE"){
                    segmentFlag = 2;
                    continue;
                }
                if(data == "HALT"){
                    scanner.close();
                    return;
                }
                if(segmentFlag == 1){

                }
                if(segmentFlag == 2){

                }
                

            }
            scanner.close();
        } catch (FileNotFoundException e) {
            
            e.printStackTrace();
        }
    }

    private void setClearMemory(){
        for(int i = 0; i < 256; i++){
            virtualMemory.add(new Word());
        }
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
    private void ADD(){

    }

    private void SUB(){

    }
    //Palyginimo komandos
    private void MOR(){

    }

    private void EQL(){

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