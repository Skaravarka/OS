
public class VirtualMachine {

    private int ax;
    private int bx;
    private int cc;
    private int sf;
    private int mp;
    private int ptr;
    private Memory memory = new Memory();
    
    
    public VirtualMachine(Memory memory, int ax, int bx, int cc, int sf, int mp) {
        this.ax = ax;
        this.bx = bx;
        this.cc = cc;
        this.sf = sf;
        this.mp = mp;
        this.memory = memory;
    }
    public VirtualMachine() {
        this.ax = 0;
        this.bx = 0;
        this.cc = 0;
        this.sf = 0;
        this.mp = 0;
        this.memory = null;
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
    public int getPtr(){
        return ptr;
    }

    public void printAllRegisters(){
        System.out.println("AX: "+this.ax);
        System.out.println("BX: "+this.bx);
        System.out.println("CC: "+this.cc);
        System.out.println("SF: "+this.sf);
        System.out.println("MP: "+this.mp);
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
    public void setPtr(int ptr){
        this.ptr = ptr;
    }
    public void setMemory(Memory memory){
        this.memory = memory;
    }

}