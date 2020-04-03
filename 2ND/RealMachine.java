import java.io.IOException;
import java.util.ArrayList;

public class RealMachine implements Runnable {

    ArrayList<Memory> allMemory = new ArrayList<Memory>();
    ArrayList<VirtualMachine> VMList = new ArrayList<VirtualMachine>();
    private Thread consoleInputsThread;
    private ConsoleInputs consoleInputs;
    private int DEFAULTTI = 50;

    private int TI = 0;
    private int[] ptr = new int[4];
    private int ax = 0;

    public void printHelp() {
        System.out.println("RM:#####################");
        System.out.println("RM:To quit press: x");
        System.out.println("RM:To create VM press 1");
        System.out.println("RM:To run VM press 2");
        System.out.println("RM:To run VM till to completion 3");
    }
    public void createMemory(){
        for(int i = 0; i < 8; i++){
            allMemory.add(new Memory());
        }
    }

    private void addVirtualMachine() {
        Memory tempMem = new Memory();
        int cc = 0;

        System.out.println("RM:Program name:");
        String command = "";
        while(command == "" || command == null){
            command = consoleInputs.getLastCommand();
        }
        if(command.equals(" ")){
            command = "PROGURAMUUWU.txt";
        }
        command = "2ND/" + command;

        for(int i = 0; i < 4; i++){
            if(ptr[i] == -1){
                ptr[i] = i;
                cc = allMemory.get(ptr[i]).loadToMemory(command);
                tempMem = allMemory.get(ptr[i]);
                break;
            }
        }
        // cc = allMemory.get(0).loadToMemory("2ND/PROGURAMUUWU.txt");
        // tempMem = allMemory.get(0);
        //tempMem.PrintAll();
        //System.out.println("cc" + cc);

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
        ptr[0] = -1;
        ptr[1] = -1;
        ptr[2] = -1;
        ptr[3] = -1;
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
                //System.out.println("command" + command);
                if(command.equals("x")){
                    break;
                }
                if(command.equals("0")){
                    printHelp();
                }
                if(command.equals("1")){
                    addVirtualMachine();
                    System.out.println("RM:added A Virtual Machine");
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
                System.out.println("x");
                VMList.get(i).doStep();
                interuptManagement(VMList.get(i).getSf(), ptr[i], i);

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
                    interuptManagement(VMList.get(i).getSf(), ptr[i], i);
                }
            }
        }

        System.out.println("finished");
    }
    private void interuptManagement(int flag, int pt, int i){
        switch(flag){
            case 39:
                System.out.println(VMList.get(i).getAx());
                break;
            case 78:
                System.out.println(VMList.get(i).getBx());
                break;
            case 20:
                ax = 0;
                while(!Word.wordToString(allMemory.get(pt).getInstruction(VMList.get(i).getAx() + ax)).equals("NULL")){
                    System.out.print(Word.wordToString(allMemory.get(pt).getInstruction(VMList.get(i).getAx() + ax)));
                    ax++;
                }
                break;
            case 21:
                ax = 0;
                while(!Word.wordToString(allMemory.get(pt).getInstruction(VMList.get(i).getBx() + ax)).equals("NULL")){
                    System.out.print(Word.wordToString(allMemory.get(pt).getInstruction(VMList.get(i).getBx() + ax)));
                    ax++;
                }
                break;       
        }
    }
    private Word getGeneralMemoryWord(int index){
        return allMemory.get(7).getInstruction(index + 128);
    }
}