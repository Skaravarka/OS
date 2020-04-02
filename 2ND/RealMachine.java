import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class RealMachine implements Runnable {

    // printHelp();
    ArrayList<VirtualMachine> VMList = new ArrayList<VirtualMachine>();
    private Thread consoleInputsThread;
    private ConsoleInputs consoleInputs;

    public void printHelp() {
        System.out.println("#####################");
        System.out.println("To quit press: x");
        System.out.println("To create VM press 1");
    }

    private void addVirtualMachine() {
        VMList.add(new VirtualMachine(0, 0, 0, 0, 0, 0));
    }

    public void run() {

        try {
            consoleInputs = new ConsoleInputs();
        } catch (IOException e) {
            e.printStackTrace();
        }
        consoleInputsThread = new Thread(consoleInputs);
        consoleInputsThread.start();  
        System.out.println("x"); 

        printHelp();
        
        while(true){
            String command = consoleInputs.getLastCommand();
            

            if(command != null){
                System.out.println("command" + command);
                if(command.equals("x")){
                    break;
                }
                if(command.equals("1")){
                    addVirtualMachine();
                }
            }
            runVirtualMachines();
        }
    }
    private void runVirtualMachines(){
        for (int i = 0; i < VMList.size(); i++){
            System.out.println("doing Stuff");
        }
    }
}