public class Supervisor{

    private boolean mode = false;
    private int TI = 0;
    private int ptr = 0; 
    private int sf = 0;
    private int ax = 0;  // darbinis
    private int bx = 0;  // darbinis
    private int cx = 0;  // darbinis
    private int dx = 0;  // darbinis
    private int chr = 0; // kanalu valdymo
    private int cc = 0;  // virtualios masinos komandu
    private int dc = 0;  // duomenu skaitliukas
    private int ii = 0; // interupt'u
    private int ei = 0; // error'u

    public Supervisor(){
        ax = 0;
        bx = 0;
        cc = 0;
        sf = 0;
        ptr = 0;
    }
    public void save(int ax, int bx, int cc, int sf, int mp, int ptr){
        this.ax = ax;
        this.bx = bx;
        this.cc = cc;
        this.sf = sf;
        this.ptr = ptr;
    }


}