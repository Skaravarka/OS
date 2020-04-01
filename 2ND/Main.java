public class Main {

    /*
    TODO:
        

    */
    public static void main(String[] args) {
        System.out.println("Hello, World");

        VirtualMachine virtualMachine = new VirtualMachine(0, 0, 0, 0, 0, 0);

        virtualMachine.loadToMemory("2ND/PROG1.txt");
        virtualMachine.printVirtualMemory();
        
        System.out.println("Helllooo, Welcum to out computah");
        Memory mem = new Memory();
        System.out.println(mem.loadToMemory("2ND/PROGURAMUUWU.txt"));
        
        mem.loadToMemory("2ND/PROGURAMUUWU.txt");

        while(true){
            
        }
    }
    public void printHelp(){
        System.out.println("#####################");
        System.out.println("To quit press: x");
    }

}