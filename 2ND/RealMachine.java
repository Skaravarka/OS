import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class RealMachine implements Runnable {

    ArrayList<Memory> allMemory = new ArrayList<Memory>();
    ArrayList<VirtualMachine> VMList = new ArrayList<VirtualMachine>();
    private Thread consoleInputsThread;
    private ConsoleInputs consoleInputs;
    private int DEFAULTTI = 50;

    private boolean mode = false;
    private int TI = 0;
    private int[] ptr = {0, 0, 0, 0, 0, 0, 0, 0}; // puslapio trasliacija
    private int sf = 0;
    private int ax = 0; // darbinis
    private int bx = 0; // darbinis
    private int cx = 0; // darbinis
    private int dx = 0; // darbinis
    private int chr = 0; // kanalu valdymo
    private int cc = 0; // virtualios masinos komandu
    private int dc = 0; // duomenu skaitliukas
    private int mp = 0; // bendros atminties semaforas
    private int ii = 0; // interupt'u
    private int ei = 0; // error'u

    public void printHelp() {
        System.out.println("RM:#####################");
        System.out.println("RM:To quit press: x");
        System.out.println("RM:Debug: a");
        System.out.println("RM:To show help press: 0");
        System.out.println("RM:To create VM press: 1");
        System.out.println("RM:To run VM press: 2");
        System.out.println("RM:To run VM till to completion: 3");
        System.out.println("RM:To terminate VMs: 4");
        System.out.println("RM:To print memory: 5");
        System.out.println("RM:To print registers: 6");
        System.out.println("RM:Load to memory: 7");
        System.out.println("RM:To print VM list: 8");
        System.out.println("RM:To print VM memory: 9");
    }
    public void printVMlist(){
        for(int i = 0; i < VMList.size(); i++){
            System.out.print(i + " ");
        }
        System.out.println("");
    }

    public void createMemory() {
        for (int i = 0; i < 8; i++) {
            allMemory.add(new Memory());
        }
    }
    private boolean isWorkingRegister(String reg){
        if(reg.equals("AX") || reg.equals("BX") || reg.equals("CX") || reg.equals("DX")){
            return true;
        }
        return false;
    }
    private String paging(int ptr, int cc){
        //Integer.parseInt(Word.wordToString(allMemory.get(ptr).getInstruction(0)));
        return Word.wordToString(allMemory.get(Integer.parseInt(Word.wordToString(allMemory.get(ptr).getInstruction(0)).trim())).getInstruction(cc));
    
    }
    private String getConsoleCommand(){
        while(true){
            String command = consoleInputs.getLastCommand();
            if(command != null && !command.equals("")){
                return command;
            }
        }   
    }

    private int getRandomPage(){
        int count = 0;
        for(int i = 0; i < ptr.length; i++){
            if(ptr[i] == 0){
                count++;
            }
        }
        if(count <= 0){
            return -1;
        }
        Random rand = new Random();
        int a = rand.nextInt(allMemory.size());
        while(ptr[a] == 1){
            a = rand.nextInt(allMemory.size());
        }
        ptr[a] = 1;
        // for(int i = 0; i < ptr.length; i++){
        //     System.out.print(ptr[i]);
        // }
        System.out.println("");
        return a;
    }
    private void printVMMemory(){
        System.out.println("RM: Available VMs:");
        printVMlist();
        int command = Integer.parseInt(getConsoleCommand());
        System.out.println("RM:Page list");
        allMemory.get(VMList.get(command).getPtr()).printAllNicely(0);
        System.out.println("RM:Memory");
        allMemory.get(Integer.parseInt(Word.wordToString(allMemory.get(VMList.get(command).getPtr()).getInstruction(0)).trim())).printAllNicely(0);

    }

    private void addVirtualMachine() {
        int page = getRandomPage();
        VirtualMachine virtualMachine = new VirtualMachine();
        virtualMachine.setPtr(page);

        VMList.add(virtualMachine);
    }

    private void loadToMemory(){
        String command = consoleInputs.getLastCommand();
        int num = -1;
        System.out.println("RM:Free memories:");
        for(int i = 0; i < ptr.length; i++){
            if(ptr[i] == 0){
                System.out.print(i + " ");
            }
        }
        System.out.println("");
        while(true){
            num = Integer.parseInt(getConsoleCommand());
            if(num >= 0 && num < ptr.length){
                if(ptr[num] == 0){
                    break;
                }
            }
        }
        ptr[num] = 1;
        System.out.println("RM:Program name:");
        command = getConsoleCommand();
        if (command.equals(" ") || command.equals("1")) {
            command = "PROGURAMUUWU.txt";
        }
        command = "2ND/" + command;

        allMemory.get(num).loadToMemory(command);

        System.out.println("RM:Available VMs:");
        printVMlist();
        System.out.println("");
        command = getConsoleCommand();
        int numVM = Integer.parseInt(command);
        //VMList.get(numVM).setPtr(num);
        allMemory.get(VMList.get(numVM).getPtr()).set(0, Word.stringToWord("   "+Integer.toString(num)));
        
    }


    private void waitABit() {
        try {
            wait(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void debug(){
        doStep(0);
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

        while (true) {

            //String command = consoleInputs.getLastCommand();
            String command = getConsoleCommand();

            if (command != null) {
                // System.out.println("command" + command);
                if (command.equals("x")) {
                    break;
                }
                if (command.equals("0")) {
                    printHelp();
                }
                if (command.equals("a")) {
                    debug();
                }
                if (command.equals("1")) {
                    addVirtualMachine();
                    System.out.println("RM:added A Virtual Machine");
                    System.out.println("RM:Done");
                }
                if (command.equals("2")) {
                    System.out.println("");
                    runVirtualMachines();
                }
                if (command.equals("3")) {
                    runVirtualMachineTillCompletion();
                }
                if (command.equals("4")) {
                    VMList.clear();
                    System.out.println("RM:Virtual Machines are terminated");
                    System.out.println("RM:Done");
                }
                if (command.equals("5")) {
                    printMemory();
                    System.out.println("RM:Done");
                }
                if (command.equals("6")) {
                    printRegisters();
                    System.out.println("RM:Done");
                }
                if (command.equals("7")) {
                    loadToMemory();
                    System.out.println("RM:Done");
                }
                if (command.equals("8")) {
                    printVMlist();
                    System.out.println("RM:Done");
                }
                if (command.equals("9")) {
                    printVMMemory();
                    System.out.println("RM:Done");
                }
            }
        }
        try {
            consoleInputs.killThread();
            consoleInputsThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("maiu");
    }
    private void runVirtualMachines(){
        for (int i = 0; i < VMList.size(); i++){
            if(!VMList.get(i).isFinished()){
                System.out.println("x");
                doStep(i);
                //VMList.get(i).doStep();

                //interuptManagement(VMList.get(i).getSf(), ptr[i], i);

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
                    //interuptManagement(VMList.get(i).getSf(), ptr[i], i);
                }
            }
        }
        System.out.println(" ");
        System.out.println("finished");
    }
    public void doStep(int VMNum){
        System.out.println(paging(VMList.get(VMNum).getPtr(), 0));
    }
    private void interuptManagement(int flag, int pt, int i){
        switch(flag){
            case 39: // PRT AX
                System.out.println(VMList.get(i).getAx());
                break;
            case 78: // PRT BX
                System.out.println(VMList.get(i).getBx());
                break;
            case 20: // PRS AX
                ax = 0; 
                while(!Word.wordToString(allMemory.get(pt).getInstruction(VMList.get(i).getAx() + ax)).equals("NULL")){
                    System.out.print(Word.wordToString(allMemory.get(pt).getInstruction(VMList.get(i).getAx() + ax)));
                    ax++;
                }
                break;
            case 21: //PRS BX
                ax = 0;
                while(!Word.wordToString(allMemory.get(pt).getInstruction(VMList.get(i).getBx() + ax)).equals("NULL")){
                    System.out.print(Word.wordToString(allMemory.get(pt).getInstruction(VMList.get(i).getBx() + ax)));
                    ax++;
                }
                break;       
        }
    }
    private Word getGMemoryWord(int index){
        return allMemory.get(7).getInstruction(index + 128);
    }
    private void printMemory(){
        for(int i = 0; i < allMemory.size(); i++){
            allMemory.get(i).printAllNicely(i);
        }
    }
    private void printRegisters(){
        System.out.print("MODE: ");
        System.out.print(mode);
        System.out.print(" |TI: ");
        System.out.print(TI);
        System.out.print(" |II: ");
        System.out.print(ii);
        System.out.println("");
        System.out.print("SF: ");
        System.out.print(sf);
        System.out.print(" |CC: ");
        System.out.print(cc);
        System.out.print(" |DC: ");
        System.out.print(dc);
        System.out.print(" |PTR: ");
        System.out.print(ptr);
        System.out.print(" |MP: ");
        System.out.print(mp);
        System.out.println("");
        System.out.print("AX: ");
        System.out.print(ax);
        System.out.print(" |BX: ");
        System.out.print(bx);
        System.out.print(" |CX: ");
        System.out.print(cx);
        System.out.print(" |DX: ");
        System.out.print(dx);
        System.out.print(" |CHR: ");
        System.out.print(chr);
        System.out.print(" |EI: ");
        System.out.print(ei);
    }
}