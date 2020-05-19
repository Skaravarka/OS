public class Supervisor{

    private boolean mode = false;
    private int TI = 0;
    private int ptr = 0;
    private int sf = 0;
    private int ax = 0; // darbinis
    private int bx = 0; // darbinis
    private int cx = 0; // darbinis
    private int dx = 0; // darbinis
    private int chr = 0; // kanalu valdymo
    private int cc = 0; // virtualios masinos komandu
    private int dc = 0; // duomenu skaitliukas
    private int ii = 0; // interupt'u
    private int ei = 0; // error'u
    private int vmp = 0;

    public boolean isMode() {
        return this.mode;
    }

    public void setMode(boolean mode) {
        this.mode = mode;
    }

    public int getTI() {
        return this.TI;
    }

    public void setTI(int TI) {
        this.TI = TI;
    }

    public int getPtr() {
        return this.ptr;
    }

    public void setPtr(int ptr) {
        this.ptr = ptr;
    }

    public int getSf() {
        return this.sf;
    }

    public void setSf(int sf) {
        this.sf = sf;
    }

    public int getAx() {
        return this.ax;
    }

    public void setAx(int ax) {
        this.ax = ax;
    }

    public int getBx() {
        return this.bx;
    }

    public void setBx(int bx) {
        this.bx = bx;
    }

    public int getCx() {
        return this.cx;
    }

    public void setCx(int cx) {
        this.cx = cx;
    }

    public int getDx() {
        return this.dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getChr() {
        return this.chr;
    }

    public void setChr(int chr) {
        this.chr = chr;
    }

    public int getCc() {
        return this.cc;
    }

    public void setCc(int cc) {
        this.cc = cc;
    }

    public int getDc() {
        return this.dc;
    }

    public void setDc(int dc) {
        this.dc = dc;
    }

    public int getIi() {
        return this.ii;
    }

    public void setIi(int ii) {
        this.ii = ii;
    }

    public int getEi() {
        return this.ei;
    }

    public void setEi(int ei) {
        this.ei = ei;
    }

    public void setVmp(int vmp){
        this.vmp = vmp;
    }

    public int getVmp() {
        return vmp;
    }
}