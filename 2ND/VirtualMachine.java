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

    private boolean isRegister(String reg){
        if(reg.equals("AX") || reg.equals("BX")){
            return true;
        }
        return false;
    }

    public void DoNextInstruction(){
        String string = Word.wordToString(Memory.getNext());

        if (string.equals("ADD ")){
            string = Word.wordToString(Memory.getNext());
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            System.out.println("Vykdoma: ADD "+Lside+", "+Rside);
            if(isRegister(Lside)){
                if(Lside.equals("AX")){
                    if(isRegister(Rside)){
                        if(Rside.equals("AX")){
                            //ADD AX, AX
                            setAx(getAx()+getAx());
                        }
                        else{
                            //ADD AX, BX
                            setAx(getAx()+getBx());
                        }
                    }
                    else{
                        //ADD AX, [hex]
                        setAx(getAx()+Integer.parseInt(Word.wordToString(virtualMemory.get(Integer.parseInt(Rside, 16)))));
                    }
                }
                else{
                    //Lside is BX
                    if(isRegister(Rside)){
                        if(Rside.equals("AX")){
                            //ADD BX, AX
                            setBx(getBx()+getAx());
                        }
                        else{
                            //ADD BX, BX
                            setBx(getBx()+getBx());
                        }
                    }
                    else{
                        //ADD BX, [hex]
                        setBx(getBx()+Integer.parseInt(Word.wordToString(virtualMemory.get(Integer.parseInt(Rside, 16)))));
                    }
                }
            }
            else{
                System.out.println("Left side is not a register, refer to docs");
            }
            return;
        }
        else if(string.equals("ADDV")){
            //ADD REG, value
            string = Word.wordToString(Memory.getNext());
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            System.out.println("Vykdoma: ADDV "+Lside+", "+Rside);
            if(isRegister(Lside)){
                if(Lside.equals("AX")){
                    //ADD AX, value
                    setAx(getAx()+Integer.parseInt(Rside));
                }
                else{
                    //ADD BX, value
                    setBx(getBx()+Integer.parseInt(Rside));
                }
            }
            else{
                System.out.println("Left side is not a register, refer to docs");
            }
            return;
        }
        if (string.equals("SUB ")){
            string = Word.wordToString(Memory.getNext());
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            System.out.println("Vykdoma: SUB "+Lside+", "+Rside);
            if(isRegister(Lside)){
                if(Lside.equals("AX")){
                    if(isRegister(Rside)){
                        if(Rside.equals("AX")){
                            //SUB AX, AX
                            setAx(0);
                        }
                        else{
                            //SUB AX, BX
                            setAx(getAx()-getBx());
                        }
                    }
                    else{
                        //SUB AX, [hex]
                        setAx(getAx()-Integer.parseInt(Word.wordToString(virtualMemory.get(Integer.parseInt(Rside, 16)))));
                    }
                }
                else{
                    //Lside is BX
                    if(isRegister(Rside)){
                        if(Rside.equals("AX")){
                            //SUB BX, AX
                            setBx(getBx()-getAx());
                        }
                        else{
                            //SUB BX, BX
                            setBx(0);
                        }
                    }
                    else{
                        //SUB BX, [hex]
                        setBx(getBx()-Integer.parseInt(Word.wordToString(virtualMemory.get(Integer.parseInt(Rside, 16)))));
                    }
                }
            }
            else{
                System.out.println("Left side is not a register, refer to docs");
            }
            return;
        }
        else if(string.equals("SUBV")){
            //ADD REG, value
            string = Word.wordToString(Memory.getNext());
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            System.out.println("Vykdoma: SUBV "+Lside+", "+Rside);
            if(isRegister(Lside)){
                if(Lside.equals("AX")){
                    //ADD AX, value
                    setAx(getAx()-Integer.parseInt(Rside));
                }
                else{
                    //ADD BX, value
                    setBx(getBx()-Integer.parseInt(Rside));
                }
            }
            else{
                System.out.println("Left side is not a register, refer to docs");
            }
            return;
        }
        if (string.contains("MOR ")){
            string = Word.wordToString(Memory.getNext());
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            System.out.println("Vykdoma MOR "+Lside+", "+Rside);
            if(isRegister(Lside)&&isRegister(Rside)){
                if(Lside.equals("AX")){
                    if(Rside.equals("AX")){
                        //MOR AX, AX
                        setSf(0);
                    }
                    else{
                        //MOX AX, BX
                        if(getAx()>getBx())
                            setSf(1);
                        else
                            setSf(0);
                    }
                }
                else{
                    if(Rside.equals("AX")){
                        //MOR BX, AX
                        if(getBx()>getAx())
                            setSf(1);
                        else
                            setSf(0);
                    }
                    else{
                        //MOR BX, BX
                        setSf(0);
                    }

                }
            }
            else{
                System.out.println("One of the operands are not registers AX or BX");
            }
            return;
        }
        if (string.contains("EQL ")){
            string = Word.wordToString(Memory.getNext());
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            System.out.println("Vykdoma MOR "+Lside+", "+Rside);
            if(isRegister(Lside)&&isRegister(Rside)){
                if(Lside.equals("AX")){
                    if(Rside.equals("AX")){
                        //EQL AX, AX
                        setSf(1);
                    }
                    else{
                        //EQL AX, BX
                        if(getAx()==getBx())
                            setSf(1);
                        else
                            setSf(0);
                    }
                }
                else{
                    if(Rside.equals("AX")){
                        //EQL BX, AX
                        if(getBx()==getAx())
                            setSf(1);
                        else
                            setSf(0);
                    }
                    else{
                        //EQL BX, BX
                        setSf(1);
                    }

                }
            }
            else{
                System.out.println("One of the operands are not registers AX or BX");
            }
            return;
        }
        if (string.equals("LEA ")){
            string = Word.wordToString(Memory.getNext());
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            System.out.println("Vykdoma LEA "+Lside+", "+Rside);
            if(isRegister(Lside)){
                if(Lside.equals("AX")){
                    if(isRegister(Rside)){
                        if(Rside.equals("AX")){
                            //Do nuffin cuz MOV AX, AX
                        }
                        else{
                            //MOV AX, BX
                            setAx(getBx());
                        }
                    }
                    else{
                        //MOV AX, [hex]
                        //Setina ax i int value of cell value of hex number kill me :)
                        setAx(Integer.parseInt(Word.wordToString(getMemoryCell(Integer.parseInt(Rside,16)))));
                    }
                }
                else{
                    //Lside is BX
                    if(isRegister(Rside)){
                        if(Rside.equals("AX")){
                            //MOV BX, AX
                            setBx(getAx());
                        }
                        else{
                            //Do nuffin cuz MOV BX, BX
                        }
                    }
                    else{
                        //MOV BX, [hex]
                        //I BX ideda [hex] vietoje esanti cello int value
                        setBx(Integer.parseInt(Word.wordToString(virtualMemory.get(Integer.parseInt(Rside, 16)))));
                    }
                }
            }
            else{
                //Lside not register
                if(isRegister(Rside)){
                    if(Rside.equals("AX")){
                        //MOV [hex], AX
                        virtualMemory.set(Integer.parseInt(Lside, 16), Word.stringToWord(Integer.toString(getAx())));
                    }
                    else{
                        //MOV [hex], BX
                        virtualMemory.set(Integer.parseInt(Lside, 16), Word.stringToWord(Integer.toString(getBx())));
                    }
                }
                else{
                    //MOV [hex], [hex]
                    //I atminties vieta i kuria rodo [hex1] idedama atvinties vieta i kuria rodo [hex2]
                    virtualMemory.set(Integer.parseInt(Lside, 16), virtualMemory.get(Integer.parseInt(Rside, 16)));
                    
                }
            }
            return;
        }
        if(string.equals("MOV ")){
            string = Word.wordToString(Memory.getNext());
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            System.out.println("Vykdoma MOV "+Lside+", "+Rside);
            if(isRegister(Lside)){
                if(Lside.equals("AX")){
                    //MOV AX, value
                    setAx(Integer.parseInt(Rside));
                }
                else{
                    //MOV BX, value
                    setBx(Integer.parseInt(Rside));
                }
            }
            else{
                //MOV [hex], values
                //TODO: TEST THIS
                virtualMemory.set(Integer.parseInt(Lside, 16), Word.stringToWord(Rside));
                
            }
        }
        if (string.contains("GET ")){
            //Nusikelia po OS
            GET();
            return;
        }
        if (string.contains("PRR ")){
            //Nusikelia po OS
            PRR();
            return;
        }
        if (string.contains("PRS ")){
            //Nusikelia po OS
            PRS();
            return;
        }
        if (string.contains("WGD ")){
            //Nusikelia po OS
            WGD();
            return;
        }
        if (string.contains("RGD ")){
            //Nusikelia po OS
            RGD();
            return;
        }
        if (string.contains("LGD ")){
            //Nusikelia po OS
            LGD();
            return;
        }
        if (string.contains("UGD ")){
            //Nusikelia po OS
            UGD();
            return;
        }
        if (string.contains("JMP ")){
            string = Word.wordToString(Memory.getNext()).trim();
            System.out.println("Vykdoma JMP "+string);
            Memory.setMemPointer(Integer.parseInt(string));
            return;
        }
        if (string.contains("JEZ ")){
            string = Word.wordToString(Memory.getNext());
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            System.out.println("Vykdoma JEZ "+Lside+", "+Rside);
            if(isRegister(Lside)){
                System.out.println("Lside should not be a register");
            }
            else{
                if(isRegister(Rside)){
                    if(Rside.equals("AX")){
                        //JEZ [hex], AX
                        if(getAx()==0){
                            Memory.setMemPointer(Integer.parseInt(Lside, 16));
                        }
                    }
                    else{
                        //JEZ [hex], BX
                        if(getBx()==0){
                            Memory.setMemPointer(Integer.parseInt(Lside, 16));
                        }
                    }
                }
                else{
                    System.out.println("Rside should be a register");
                }
            }
            return;
        }
        if (string.contains("JNZ ")){
            string = Word.wordToString(Memory.getNext());
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            System.out.println("Vykdoma JNZ "+Lside+", "+Rside);
            if(isRegister(Lside)){
                System.out.println("Lside should not be a register");
            }
            else{
                if(isRegister(Rside)){
                    if(Rside.equals("AX")){
                        //JEZ [hex], AX
                        if(getAx()!=0){
                            Memory.setMemPointer(Integer.parseInt(Lside, 16));
                        }
                    }
                    else{
                        //JEZ [hex], BX
                        if(getBx()!=0){
                            Memory.setMemPointer(Integer.parseInt(Lside, 16));
                        }
                    }
                }
                else{
                    System.out.println("Rside should be a register");
                }
            }
            return;
        }
        if (string.contains("JGZ ")){
            string = Word.wordToString(Memory.getNext());
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            System.out.println("Vykdoma JGZ "+Lside+", "+Rside);
            if(isRegister(Lside)){
                System.out.println("Lside should not be a register");
            }
            else{
                if(isRegister(Rside)){
                    if(Rside.equals("AX")){
                        //JEZ [hex], AX
                        if(getAx()>0){
                            Memory.setMemPointer(Integer.parseInt(Lside, 16));
                        }
                    }
                    else{
                        //JEZ [hex], BX
                        if(getBx()>0){
                            Memory.setMemPointer(Integer.parseInt(Lside, 16));
                        }
                    }
                }
                else{
                    System.out.println("Rside should be a register");
                }
            }
            return;
        }
        if (string.contains("JLZ ")){
            string = Word.wordToString(Memory.getNext());
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            System.out.println("Vykdoma JLZ "+Lside+", "+Rside);
            if(isRegister(Lside)){
                System.out.println("Lside should not be a register");
            }
            else{
                if(isRegister(Rside)){
                    if(Rside.equals("AX")){
                        //JEZ [hex], AX
                        if(getAx()<0){
                            Memory.setMemPointer(Integer.parseInt(Lside, 16));
                        }
                    }
                    else{
                        //JEZ [hex], BX
                        if(getBx()<0){
                            Memory.setMemPointer(Integer.parseInt(Lside, 16));
                        }
                    }
                }
                else{
                    System.out.println("Rside should be a register");
                }
            }
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