import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class VirtualMachine {

    private int ax;
    private int bx;
    private int cc;
    private int sf;
    private int mp;
    private Memory memory = new Memory();
    
    
    public VirtualMachine(Memory memory, int ax, int bx, int cc, int sf, int mp) {
        this.ax = ax;
        this.bx = bx;
        this.cc = cc;
        this.sf = sf;
        this.mp = mp;
        this.memory = memory;
    }

    private boolean isRegister(String reg){
        if(reg.equals("AX") || reg.equals("BX")){
            return true;
        }
        return false;
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

    public void setAx(int ax){
        this.ax = ax;
    }
    public void setBx(int bx){
        this.bx = bx;
    }
    public void setCc(int cc){
        this.cc = cc;
    }
    public void incCc(){
        this.cc = this.cc +1;
    }
    public void setSf(int sf){
        this.sf = sf;
    }
    public void setMp(int mp){
        this.mp = mp;
    }

    public boolean isFinished(){
        if(getCc()>0){
            if(Word.wordToString(memory.getInstruction(getCc())).trim().equals("HALT")){
                setCc(-10);
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return true;
        }
    }


    //REGRETS
    public void doStep(){
        
        if(isFinished()){
            System.out.println("No code left to execute, see yourselft out :)");
            return;
        }
        String string = Word.wordToString(memory.getInstruction(getCc())).trim();
        incCc();
        setSf(0);

        if (string.equals("ADD")){
            string = Word.wordToString(memory.getInstruction(getCc()));
            incCc();
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
                        setAx(getAx()+Integer.parseInt(Word.wordToString(memory.getInstruction(Integer.parseInt(Rside, 16)))));
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
                        setBx(getBx()+Integer.parseInt(Word.wordToString(memory.getInstruction(Integer.parseInt(Rside, 16)))));
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
            string = Word.wordToString(memory.getInstruction(getCc()));
            incCc();
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
        if (string.equals("SUB")){
            string = Word.wordToString(memory.getInstruction(getCc()));
            incCc();
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
                        setAx(getAx()-Integer.parseInt(Word.wordToString(memory.getInstruction(Integer.parseInt(Rside, 16)))));
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
                        setBx(getBx()-Integer.parseInt(Word.wordToString(memory.getInstruction(Integer.parseInt(Rside, 16)))));
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
            string = Word.wordToString(memory.getInstruction(getCc()));
            incCc();
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
        if (string.contains("MOR")){
            string = Word.wordToString(memory.getInstruction(getCc()));
            incCc();
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
        if (string.contains("EQL")){
            string = Word.wordToString(memory.getInstruction(getCc()));
            incCc();
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
        if (string.equals("LEA")){
            string = Word.wordToString(memory.getInstruction(getCc()));
            incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            System.out.println("Vykdoma LEA "+Lside+", "+Rside);
            if(isRegister(Lside)){
                if(Lside.equals("AX")){
                    if(isRegister(Rside)){
                        if(Rside.equals("AX")){
                            //Do nuffin cuz LEA AX, AX
                        }
                        else{
                            //LEA AX, BX
                            setAx(getBx());
                        }
                    }
                    else{
                        //LEA AX, [hex]
                        //Setina ax i int value of cell value of hex number kill me :)
                        setAx(Integer.parseInt(Word.wordToString(memory.getInstruction(Integer.parseInt(Rside,16)))));
                    }
                }
                else{
                    //Lside is BX
                    if(isRegister(Rside)){
                        if(Rside.equals("AX")){
                            //LEA BX, AX
                            setBx(getAx());
                        }
                        else{
                            //Do nuffin cuz LEA BX, BX
                        }
                    }
                    else{
                        //LEA BX, [hex]
                        //I BX ideda [hex] vietoje esanti cello int value
                        setBx(Integer.parseInt(Word.wordToString(memory.getInstruction(Integer.parseInt(Rside, 16)))));
                    }
                }
            }
            else{
                //Lside not register
                if(isRegister(Rside)){
                    if(Rside.equals("AX")){
                        //LEA [hex], AX
                        memory.set(Integer.parseInt(Lside, 16), Word.stringToWord(Integer.toString(getAx())));
                    }
                    else{
                        //LEA [hex], BX
                        memory.set(Integer.parseInt(Lside, 16), Word.stringToWord(Integer.toString(getBx())));
                    }
                }
                else{
                    //LEA [hex], [hex]
                    //I atminties vieta i kuria rodo [hex1] idedama atvinties vieta i kuria rodo [hex2]
                    memory.set(Integer.parseInt(Lside, 16), memory.getInstruction(Integer.parseInt(Rside, 16)));
                    
                }
            }
            return;
        }
        if(string.equals("MOV")){
            string = Word.wordToString(memory.getInstruction(getCc()));
            incCc();
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
                memory.set(Integer.parseInt(Lside, 16), Word.stringToWord(Rside));
            }
            return;
        }
        //TODO:GET REWRITE BITCH
        if (string.contains("GET")){
            //GET value, returns pointer in AX 1<value<16
            string = Word.wordToString(memory.getInstruction(getCc()));
            incCc();
            System.out.println("Vykdoma MOV "+string);
            if(Integer.parseInt(string)<1 || Integer.parseInt(string)>16){
                System.out.println("GET out of bounds, should be [1, 16] you donkey");
            }
            else{
                setSf(Integer.parseInt(string));
            }
            return;
        }
        if (string.contains("PRR")){
            string = Word.wordToString(memory.getInstruction(getCc())).trim();
            incCc();
            System.out.println("Vykdoma PRR "+string);
            if(isRegister(string)){
                if(string.equals("AX")){
                    //PRR AX
                    setSf(39);
                }
                else{
                    //PRR BX
                    setSf(78);
                }
            }
            else
                System.out.println("Turejai registra ivest tu asilo berete");
            return;
        }
        if (string.contains("PRS")){
            //Isspausdina pasirinkta atmienties bloka PRS reg , kur reg-AX/BX
            string = Word.wordToString(memory.getInstruction(getCc())).trim();
            incCc();
            System.out.println("Vykdoma PRS "+string);
            if(isRegister(string)){
                if(string.equals("AX")){
                    //PRS AX
                    setSf(20);
                }
                else{
                    //PRS BX
                    setSf(21);
                }
            }
            else{
                System.out.println("Should be a register you mofo");
            }
            return;
        }
        if (string.contains("WGD")){
            //Nusikelia po OS
            return;
        }
        if (string.contains("RGD")){
            //Nusikelia po OS
            return;
        }
        if (string.contains("LGD")){
            //Nusikelia po OS
            return;
        }
        if (string.contains("UGD")){
            //Nusikelia po OS
            return;
        }
        if (string.equals("JMP")){
            string = Word.wordToString(memory.getInstruction(getCc()));
            incCc();
            System.out.println("Vykdoma JMP "+string);
            setCc(Integer.parseInt(string));
            return;
        }
        if (string.equals("JEZ")){
            string = Word.wordToString(memory.getInstruction(getCc()));
            incCc();
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
                            setCc(Integer.parseInt(Lside, 16));
                        }
                    }
                    else{
                        //JEZ [hex], BX
                        if(getBx()==0){
                            setCc(Integer.parseInt(Lside, 16));
                        }
                    }
                }
                else{
                    System.out.println("Rside should be a register");
                }
            }
            return;
        }
        if (string.equals("JNZ")){
            string = Word.wordToString(memory.getInstruction(getCc()));
            incCc();
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
                            setCc(Integer.parseInt(Lside, 16));
                        }
                    }
                    else{
                        //JEZ [hex], BX
                        if(getBx()!=0){
                            setCc(Integer.parseInt(Lside, 16));
                        }
                    }
                }
                else{
                    System.out.println("Rside should be a register");
                }
            }
            return;
        }
        if (string.equals("JGZ")){
            string = Word.wordToString(memory.getInstruction(getCc()));
            incCc();
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
                            setCc(Integer.parseInt(Lside, 16));
                        }
                    }
                    else{
                        //JEZ [hex], BX
                        if(getBx()>0){
                            setCc(Integer.parseInt(Lside, 16));
                        }
                    }
                }
                else{
                    System.out.println("Rside should be a register");
                }
            }
            return;
        }
        if (string.equals("JLZ")){
            string = Word.wordToString(memory.getInstruction(getCc()));
            incCc();
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
                            setCc(Integer.parseInt(Lside, 16));
                        }
                    }
                    else{
                        //JEZ [hex], BX
                        if(getBx()<0){
                            setCc(Integer.parseInt(Lside, 16));
                        }
                    }
                }
                else{
                    System.out.println("Rside should be a register");
                }
            }
            return;
            }
            if (string.contains("END")){
                //END();
                return;
            }
            System.out.println("Kaska neto ivedei");
    }
}