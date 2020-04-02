import java.io.IOException;
import java.util.ArrayList;

public class RealMachine implements Runnable {

    ArrayList<Memory> allMemory = new ArrayList<Memory>();
    ArrayList<VirtualMachine> VMList = new ArrayList<VirtualMachine>();
    private Thread consoleInputsThread;
    private ConsoleInputs consoleInputs;
    private int DEFAULTTI = 50;

    private int TI = 0;
    private int pgr = 0;

    public void printHelp() {
        System.out.println("#####################");
        System.out.println("To quit press: x");
        System.out.println("To create VM press 1");
        System.out.println("To run VM press 2");
        System.out.println("To run VM till to completion 3");
    }
    public void createMemory(){
        for(int i = 0; i < 16; i++){
            allMemory.add(new Memory());
        }
    }

    private void addVirtualMachine() {
        Memory tempMem = allMemory.get(pgr);
        pgr++;
        int cc = tempMem.loadToMemory("2ND/PROGURAMUUWU.txt");

        VMList.add(new VirtualMachine(tempMem, 0, 0, cc, 0, 0));
    }

    private void waitABit() {
        try {
            wait(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        createMemory();
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
                    System.out.println("added A Virtual Machine");
                }
                if(command.equals("2")){
                    System.out.println("");
                    runVirtualMachines();
                }
                if(command.equals("3")){
                    runVirtualMachineTillCompletion();
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
        System.out.println("oneStep");
    }
    private void runVirtualMachineTillCompletion(){
        for(int t = DEFAULTTI; t > 0; t++){
            for (int i = 0; i < VMList.size(); i++){
                if(!VMList.get(i).isFinished()){
                    VMList.get(i).doStep();
                    interuptManagement(VMList.get(i).getSf());
                }
            }
        }
        System.out.println("finished");
    }
    private void interuptManagement(int flag){
        switch(flag){
            case 39:
                break;
            case 78:
                break;
                
        }
    }
}