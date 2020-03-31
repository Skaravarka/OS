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
        try {
            scanner = new Scanner(file);
            while(scanner.hasNextLine()){
                String data = scanner.nextLine();
                System.out.println(data);
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
}