import java.util.Scanner;

public class Main {

    /*
    TODO:

    Padarytos komandos:
    ADD & ADDV
    SUB & SUBV
    MOV & MOVV
    MOR
    EQL


    */
    public static void main(String[] args) {
        VirtualMachine virtualMachine = new VirtualMachine(0, 0, 0, 0, 0, 0);

        // virtualMachine.loadToMemory("2ND/PROG1.txt");
        // virtualMachine.printVirtualMemory();
        
        System.out.println("Helllooo, Welcum to our computah");
        //System.out.println("Helllooo, Welcum to our computah");
        Memory mem = new Memory();
        //System.out.println(mem.loadToMemory("2ND/PROGURAMUUWU.txt"));
        
        mem.loadToMemory("2ND/PROGURAMUUWU.txt");
        virtualMachine.DoNextInstruction();
        virtualMachine.DoNextInstruction();
        virtualMachine.DoNextInstruction();

        System.out.println("AX:"+virtualMachine.getAx());
        System.out.println("BX:"+virtualMachine.getBx());



        printHelp();
        Scanner myS = new Scanner(System.in);
        while(true){
            String command = myS.nextLine();
            if(command.equals("x")){
                break;
            }
        }
        myS.close();
    }
    public static void printHelp(){
        System.out.println("#####################");
        System.out.println("To quit press: x");
    }

}