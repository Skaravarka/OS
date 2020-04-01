import java.util.Scanner;

public class Main {

    /*
    TODO:
        

    */
    public static void main(String[] args) {
        System.out.println("Hello, World");

        VirtualMachine virtualMachine = new VirtualMachine(0, 0, 0, 0, 0, 0);

        //virtualMachine.loadToMemory("2ND/PROG1.txt");
        //virtualMachine.printVirtualMemory();
        
        System.out.println("Helllooo, Welcum to our computah");
        //System.out.println("Helllooo, Welcum to our computah");
        Memory mem = new Memory();
        //System.out.println(mem.loadToMemory("2ND/PROGURAMUUWU.txt"));
        
        mem.loadToMemory("2ND/PROGURAMUUWU.txt");
        mem.PrintAll();
        virtualMachine.DoNextInstruction();
        virtualMachine.DoNextInstruction();



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