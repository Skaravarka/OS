import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class RealMachine implements Runnable {

    ArrayList<VirtualMachine> VMList = new ArrayList<VirtualMachine>();
    private Thread consoleInputsThread;
    private ConsoleInputs consoleInputs;
    private int DEFAULTTI = 50;

    private int TI = 0;

    public void printHelp() {
        System.out.println("#####################");
        System.out.println("To quit press: x");
        System.out.println("To create VM press 1");
        System.out.println("To run VM press 2");
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

        printHelp();
        
        while(true){
            
            String command = consoleInputs.getLastCommand();

            if(command != null){
                System.out.println("command" + command);
                if(command.equals("x")){
                    break;
                }
                if(command.equals("0")){
                    printHelp();
                }
                if(command.equals("1")){
                    addVirtualMachine();
                }
                if(command.equals("2")){
                    runVirtualMachines();
                }
            }       
        }
    }
    private void runVirtualMachines(){
        for (int i = 0; i < VMList.size(); i++){
            if(!VMList.get(i).isFinished()){
                VMList.get(i).doStep();
            }
        }
        // do finnished stuff
        System.out.println("finished");
    }
    

}