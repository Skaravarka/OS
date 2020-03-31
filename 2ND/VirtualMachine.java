public class VirtualMachine {

    private int loaded;
    private int ax;
    private int bx;
    private int cc;
    private int sf;
    private int mp;

    public VirtualMachine(int loaded, int pc, int sp, int ptr){

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