import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class RealMachine implements Runnable {

    ArrayList<Memory> allMemory = new ArrayList<Memory>();
    ArrayList<VirtualMachine> VMList = new ArrayList<VirtualMachine>();
    private Thread consoleInputsThread;
    private ConsoleInputs consoleInputs;
    private Thread consoleOutputThread;
    private ConsoleOutput consoleOutputs;
    private int DEFAULTTI = 50;

    private boolean mode = false;
    private int TI = 0;
    private int[] ptr = { 0, 0, 0, 0, 0, 0, 0 }; // puslapio trasliacija
    private int sf = 0;
    private int ax = 0; // darbinis
    private int bx = 0; // darbinis
    private int cx = 0; // darbinis
    private int dx = 0; // darbinis
    private int chr = 0; // kanalu valdymo
    private int cc = 0; // virtualios masinos komandu
    private int dc = 0; // duomenu skaitliukas
    private boolean[] mp = {false, false, false, false, 
                            false, false, false, false, 
                            false, false, false, false, 
                            false, false, false, false }; // bendros atminties semaforas
    private int ii = 0; // interupt'u
    private int ei = 0; // error'u

    public void printHelp() {
        printToConsole("#####################");
        printToConsole("To quit press: x");
        printToConsole("Debug: a");
        printToConsole("To show help press: 0");
        printToConsole("To create VM press: 1");
        printToConsole("To run VM press: 2");
        printToConsole("To run VM till completion: 3");
        printToConsole("To terminate VMs: 4");
        printToConsole("To print memory: 5");
        printToConsole("To print registers: 6");
        printToConsole("Load to memory: 7");
        printToConsole("To print VM list: 8");
        printToConsole("To print VM memory: 9");
        printToConsole("To print VM registers: q");
    }

    public void printVMlist() {
        for (int i = 0; i < VMList.size(); i++) {
            System.out.print(i + " ");
        }
        printToConsole("");
    }

    public void createMemory() {
        for (int i = 0; i < 8; i++) {
            allMemory.add(new Memory());
        }
    }

    private boolean isVMRegister(String reg) {
        if (reg.equals("AX") || reg.equals("BX")) {
            return true;
        }
        return false;
    }

    private String paging(int ptr, int cc) {
        // Integer.parseInt(Word.wordToString(allMemory.get(ptr).getInstruction(0)));
        return Word.wordToString(
                allMemory.get(Integer.parseInt(Word.wordToString(allMemory.get(ptr).getInstruction(0)).trim()))
                        .getInstruction(cc));

    }

    private String getConsoleCommand() {
        
        while (true) {
            String command = consoleInputs.getLastCommand();
            if (command != null && !command.equals("")) {
                return command;
            }
        }
    }

    private void printToConsole(String string) {

        consoleOutputs.sendToOutput("RM:" + string);
        waitABit();
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
        int a = rand.nextInt(allMemory.size() - 1);
        while(ptr[a] == 1){
            a = rand.nextInt(allMemory.size() - 1);
        }
        ptr[a] = 1;
        printToConsole("");
        return a;
    }
    private void printVMMemory(){
        printToConsole(" Available VMs:");
        printVMlist();
        int command = Integer.parseInt(getConsoleCommand());
        printToConsole("Page list");
        allMemory.get(VMList.get(command).getPtr()).printAllNicely(0);
        printToConsole("Memory");
        allMemory.get(Integer.parseInt(Word.wordToString(allMemory.get(VMList.get(command).getPtr()).getInstruction(0)).trim())).printAllNicely(0);

    }

    private void printVmRegisters(){
        printToConsole(" Available VMs:");
        printVMlist();
        int command = Integer.parseInt(getConsoleCommand());
        printToConsole("Virtual machine nr: "+command+" registers:");
        VMList.get(command).printAllRegisters();
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
        printToConsole("Free memories:");
        for(int i = 0; i < ptr.length; i++){
            if(ptr[i] == 0){
                System.out.print(i + " ");
            }
        }
        printToConsole("");
        while(true){
            num = Integer.parseInt(getConsoleCommand());
            if(num >= 0 && num < ptr.length){
                if(ptr[num] == 0){
                    break;
                }
            }
        }
        ptr[num] = 1;
        printToConsole("Program name:");
        command = getConsoleCommand();
        if (command.equals(" ") || command.equals("1")) {
            command = "PROGURAMUUWU.txt";
            printToConsole("Loading default 'PROGURAMUUWU.txt'");
        }
        command = "2ND/" + command;

        int instructinCount = allMemory.get(num).getInstructionCount(command);
        allMemory.get(num).loadToMemory(command);

        printToConsole("Available VMs:");
        printVMlist();
        printToConsole("");
        command = getConsoleCommand();
        int numVM = Integer.parseInt(command);
        allMemory.get(VMList.get(numVM).getPtr()).set(0, Word.stringToWord("   "+Integer.toString(num)));
        VMList.get(numVM).setCc(instructinCount);
    }

    private synchronized void waitABit() {
        try {
            wait(40);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void debug(){
        printToConsole("miau");
        printToConsole("test");
        printToConsole("kek");
    }

    public void run() {
        createMemory();
        try {
            consoleInputs = new ConsoleInputs();
            consoleOutputs = new ConsoleOutput(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
        consoleInputsThread = new Thread(consoleInputs);
        consoleInputsThread.start();

        consoleOutputThread = new Thread(consoleOutputs);
        consoleOutputThread.start();

        printHelp();

        while (true) {

            //String command = consoleInputs.getLastCommand();
            String command = getConsoleCommand();

            if (command != null) {
                // printToConsole("command" + command);
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
                    printToConsole("added A Virtual Machine");
                    printToConsole("Done");
                }
                if (command.equals("2")) {
                    printToConsole("");
                    runVirtualMachines();
                }
                if (command.equals("3")) {
                    runVirtualMachineTillCompletion();
                }
                if (command.equals("4")) {
                    VMList.clear();
                    printToConsole("Virtual Machines are terminated");
                    printToConsole("Done");
                }
                if (command.equals("5")) {
                    printMemory();
                    printToConsole("Done");
                }
                if (command.equals("6")) {
                    printRegisters();
                    printToConsole("Done");
                }
                if (command.equals("7")) {
                    loadToMemory();
                    printToConsole("Done");
                }
                if (command.equals("8")) {
                    printVMlist();
                    printToConsole("Done");
                }
                if (command.equals("9")) {
                    printVMMemory();
                    printToConsole("Done");
                }
                if (command.equals("q")){
                    printVmRegisters();
                    printToConsole("Done");
                }
            }
        }
        try {
            consoleInputs.killThread();
            consoleInputsThread.join();
            consoleOutputs.killThread();
            consoleOutputThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        printToConsole("maiu");
    }
    private void runVirtualMachines(){
        for (int i = 0; i < VMList.size(); i++){
            if(! isFinished(i)){
                printToConsole("Doing a step, executing line: "+(VMList.get(i).getCc()+1));
                doStep(i);
                //VMList.get(i).doStep();

                //interuptManagement(VMList.get(i).getSf(), ptr[i], i);

            }
            else{
                printToConsole("VM nr: "+i+" has finished");
            }
        }
        // do finnished stuff
    }
    private void runVirtualMachineTillCompletion(){
        for(TI = 0; TI < DEFAULTTI + 1; TI++){
            if(TI == DEFAULTTI){
                ei = 4;
            }
            for (int i = 0; i < VMList.size(); i++){
                while(! isFinished(i)){
                    printToConsole("Doing a step, executing line: "+(VMList.get(i).getCc()+1));
                    doStep(i);
                    //VMList.get(i).doStep();

                    //interuptManagement(VMList.get(i).getSf(), ptr[i], i);

                }
                printToConsole("VM nr: "+i+" has finished");
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void doStep(int VMNum){
        printToConsole("Doing Vm nr:"+VMNum+" step");
        executeCommand(VMNum);
        processInterupts(VMNum);
        processErrors();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean isFinished(int VMNum){
        if(paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc()).equals("HALT")){
            return true;
        }
        else{
            return false;
        }
    }
    private void handleWGD(){ // write to general memory
        int tempIndex = 0;
        String tempWord = "";
        allMemory.get(allMemory.size() - 1).set(tempIndex, Word.stringToWord(tempWord));
    }
    private void handleRGD(){ // read from general memory
        int tempIndex = 0;
        Word temp = allMemory.get(allMemory.size() - 1).getInstruction(tempIndex);
        String tempWord = Word.wordToString(temp);
    }
    private void handleLGD(int VMnum){// lock general memory 
        if(!mp[0]){
            mp[0] = true;
            VMList.get(VMnum).setMp(1);
        }
        else ei = 1;
        
    }
    private void handleUGD(int VMnum){ // unlock general memory
        mp[0] = false;
        VMList.get(VMnum).setMp(0);
    }

    private void processInterupts(int VMnum){
        switch(ii){
            case 1:
                printToConsole(Integer.toString(ax));
                ii = 0;
                break;
            case 2:
                printToConsole(Integer.toString(bx));
                ii = 0;
                break;
            case 3:
                handleWGD();
                break;
            case 4:
                handleRGD();
                break;
            case 5:
                handleLGD(VMnum);
                break;
            case 6:
                handleUGD(VMnum);
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                break;
            default:
                break;
        }
    }
    private void processErrors(){
        switch(ei){
            case 1:
                printToConsole("!EI 1! Semafores are shit :D");
                ei = 0;
                break;
            case 2:
                printToConsole("!EI 2! Bad command");
                ei = 0;
                break;
            case 3:
                printToConsole("!EI 3! Bad Operant");
                ei = 0;
                break;
            case 4:
                printToConsole("!EI 4! Timer reach max");
                ei = 0;
            default:
                break;
        }
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
        printToConsole("");
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
        printToConsole("");
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

    public void executeCommand(int VMNum){

        if(paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc()).equals("HALT")){
            return;
        }
        
        String string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc()).trim();
        printToConsole("Original string: "+string);
        VMList.get(VMNum).incCc();
        VMList.get(VMNum).setSf(0);
        

        if (string.equals("ADD")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            printToConsole("Vykdoma: ADD "+Lside+", "+Rside);
            if(isVMRegister(Lside)){
                if(Lside.equals("AX")){
                    if(isVMRegister(Rside)){
                        if(Rside.equals("AX")){
                            //ADD AX, AX
                            VMList.get(VMNum).setAx(VMList.get(VMNum).getAx()+VMList.get(VMNum).getAx());
                        }
                        else{
                            //ADD AX, BX
                            VMList.get(VMNum).setAx(VMList.get(VMNum).getAx()+VMList.get(VMNum).getBx());
                        }
                    }
                    else{
                        //ADD AX, [hex]
                        VMList.get(VMNum).setAx(VMList.get(VMNum).getAx()+ Integer.parseInt(paging(VMList.get(VMNum).getPtr(), Integer.parseInt(Rside, 16))));
                    }
                }
                else{
                    //Lside is BX
                    if(isVMRegister(Rside)){
                        if(Rside.equals("AX")){
                            //ADD BX, AX
                            VMList.get(VMNum).setBx(VMList.get(VMNum).getBx()+VMList.get(VMNum).getAx());
                        }
                        else{
                            //ADD BX, BX
                            VMList.get(VMNum).setBx(VMList.get(VMNum).getBx()+VMList.get(VMNum).getBx());
                        }
                    }
                    else{
                        //ADD BX, [hex]
                        VMList.get(VMNum).setBx(VMList.get(VMNum).getBx()+Integer.parseInt(paging(VMList.get(VMNum).getPtr(), Integer.parseInt(Rside, 16))));
                    }
                }
            }
            else{
                printToConsole("Left side is not a register, refer to docs");
                this.ei = 3;
            }
            return;
        }
        else if(string.equals("ADDV")){
            //ADD REG, value
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            printToConsole("Vykdoma: ADDV "+Lside+", "+Rside);
            if(isVMRegister(Lside)){
                if(Lside.equals("AX")){
                    //ADD AX, value
                    VMList.get(VMNum).setAx(VMList.get(VMNum).getAx()+Integer.parseInt(Rside));
                }
                else{
                    //ADD BX, value
                    VMList.get(VMNum).setBx(VMList.get(VMNum).getBx()+Integer.parseInt(Rside));
                }
            }
            else{
                printToConsole("Left side is not a register, refer to docs");
                this.ei = 3;
            }
            return;
        }
        if (string.equals("SUB")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            printToConsole("Vykdoma: SUB "+Lside+", "+Rside);
            if(isVMRegister(Lside)){
                if(Lside.equals("AX")){
                    if(isVMRegister(Rside)){
                        if(Rside.equals("AX")){
                            //SUB AX, AX
                            VMList.get(VMNum).setAx(0);
                        }
                        else{
                            //SUB AX, BX
                            VMList.get(VMNum).setAx(VMList.get(VMNum).getAx()-VMList.get(VMNum).getBx());
                        }
                    }
                    else{
                        //SUB AX, [hex]
                        VMList.get(VMNum).setAx(VMList.get(VMNum).getAx()-Integer.parseInt(paging(VMList.get(VMNum).getPtr(), Integer.parseInt(Rside, 16))));
                    }
                }
                else{
                    //Lside is BX
                    if(isVMRegister(Rside)){
                        if(Rside.equals("AX")){
                            //SUB BX, AX
                            VMList.get(VMNum).setBx(VMList.get(VMNum).getBx()-VMList.get(VMNum).getAx());
                        }
                        else{
                            //SUB BX, BX
                            VMList.get(VMNum).setBx(0);
                        }
                    }
                    else{
                        //SUB BX, [hex]
                        VMList.get(VMNum).setBx(VMList.get(VMNum).getBx()-Integer.parseInt(paging(VMList.get(VMNum).getPtr(), Integer.parseInt(Rside, 16))));
                    }
                }
            }
            else{
                printToConsole("Left side is not a register, refer to docs");
                this.ei = 3;
            }
            return;
        }
        else if(string.equals("SUBV")){
            //ADD REG, value
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            printToConsole("Vykdoma: SUBV "+Lside+", "+Rside);
            if(isVMRegister(Lside)){
                if(Lside.equals("AX")){
                    //ADD AX, value
                    VMList.get(VMNum).setAx(VMList.get(VMNum).getAx()-Integer.parseInt(Rside));
                }
                else{
                    //ADD BX, value
                    VMList.get(VMNum).setBx(VMList.get(VMNum).getBx()-Integer.parseInt(Rside));
                }
            }
            else{
                printToConsole("Left side is not a register, refer to docs");
                this.ei = 3;
            }
            return;
        }
        if (string.contains("MOR")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            printToConsole("Vykdoma MOR "+Lside+", "+Rside);
            if(isVMRegister(Lside)&&isVMRegister(Rside)){
                if(Lside.equals("AX")){
                    if(Rside.equals("AX")){
                        //MOR AX, AX
                        VMList.get(VMNum).setSf(0);
                    }
                    else{
                        //MOX AX, BX
                        if(VMList.get(VMNum).getAx()>VMList.get(VMNum).getBx())
                            VMList.get(VMNum).setSf(1);
                        else
                            VMList.get(VMNum).setSf(0);
                    }
                }
                else{
                    if(Rside.equals("AX")){
                        //MOR BX, AX
                        if(VMList.get(VMNum).getBx()>VMList.get(VMNum).getAx())
                            VMList.get(VMNum).setSf(1);
                        else
                            VMList.get(VMNum).setSf(0);
                    }
                    else{
                        //MOR BX, BX
                        VMList.get(VMNum).setSf(0);
                    }

                }
            }
            else{
                printToConsole("One of the operands are not registers AX or BX");
                this.ei = 3;
            }
            return;
        }
        if (string.contains("EQL")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            printToConsole("Vykdoma MOR "+Lside+", "+Rside);
            if(isVMRegister(Lside)&&isVMRegister(Rside)){
                if(Lside.equals("AX")){
                    if(Rside.equals("AX")){
                        //EQL AX, AX
                        VMList.get(VMNum).setSf(1);
                    }
                    else{
                        //EQL AX, BX
                        if(VMList.get(VMNum).getAx()==VMList.get(VMNum).getBx())
                            VMList.get(VMNum).setSf(1);
                        else
                            VMList.get(VMNum).setSf(0);
                    }
                }
                else{
                    if(Rside.equals("AX")){
                        //EQL BX, AX
                        if(VMList.get(VMNum).getBx()==VMList.get(VMNum).getAx())
                            VMList.get(VMNum).setSf(1);
                        else
                            VMList.get(VMNum).setSf(0);
                    }
                    else{
                        //EQL BX, BX
                        VMList.get(VMNum).setSf(1);
                    }

                }
            }
            else{
                printToConsole("One of the operands are not registers AX or BX");
                this.ei = 3;
            }
            return;
        }
        if (string.equals("LEA")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            printToConsole("Vykdoma LEA "+Lside+", "+Rside);
            if(isVMRegister(Lside)){
                if(Lside.equals("AX")){
                    if(isVMRegister(Rside)){
                        if(Rside.equals("AX")){
                            //Do nuffin cuz LEA AX, AX
                        }
                        else{
                            //LEA AX, BX
                            VMList.get(VMNum).setAx(VMList.get(VMNum).getBx());
                        }
                    }
                    else{
                        //LEA AX, [hex]
                        //Setina ax i int value of cell value of hex number kill me :)
                        VMList.get(VMNum).setAx(Integer.parseInt(paging(VMList.get(VMNum).getPtr(), Integer.parseInt(Rside, 16))));

                    }
                }
                else{
                    //Lside is BX
                    if(isVMRegister(Rside)){
                        if(Rside.equals("AX")){
                            //LEA BX, AX
                            VMList.get(VMNum).setBx(VMList.get(VMNum).getAx());
                        }
                        else{
                            //Do nuffin cuz LEA BX, BX
                        }
                    }
                    else{
                        //LEA BX, [hex]
                        //I BX ideda [hex] vietoje esanti cello int value
                        VMList.get(VMNum).setBx(Integer.parseInt(paging(VMList.get(VMNum).getPtr(), Integer.parseInt(Rside, 16))));
                    }
                }
            }
            else{
                //Lside not register
                if(isVMRegister(Rside)){
                    if(Rside.equals("AX")){
                        //LEA [hex], AX
                        allMemory.get(Integer.parseInt(Word.wordToString(allMemory.get(VMList.get(VMNum).getPtr()).getInstruction(0)).trim())).set(Integer.parseInt(Lside, 16), Word.stringToWord(Integer.toString(VMList.get(VMNum).getAx())));
                    }
                    else{
                        //LEA [hex], BX
                        allMemory.get(Integer.parseInt(Word.wordToString(allMemory.get(VMList.get(VMNum).getPtr()).getInstruction(0)).trim())).set(Integer.parseInt(Lside, 16), Word.stringToWord(Integer.toString(VMList.get(VMNum).getBx())));
                    }
                }
                else{
                    //LEA [hex], [hex]
                    //I atminties vieta i kuria rodo [hex1] idedama atvinties vieta i kuria rodo [hex2]
                    //Naujas rekordininkas huh ? :D
                    allMemory.get(Integer.parseInt(Word.wordToString(allMemory.get(VMList.get(VMNum).getPtr()).getInstruction(0)).trim())).set(Integer.parseInt(Lside, 16), allMemory.get(Integer.parseInt(Word.wordToString(allMemory.get(VMList.get(VMNum).getPtr()).getInstruction(0)).trim())).getInstruction(Integer.parseInt(Rside, 16)));
                    
                }
            }
            return;
        }
        if(string.equals("MOV")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            printToConsole("Vykdoma MOV "+Lside+", "+Rside);
            if(isVMRegister(Lside)){
                if(Lside.equals("AX")){
                    //MOV AX, value
                    VMList.get(VMNum).setAx(Integer.parseInt(Rside));
                }
                else{
                    //MOV BX, value
                    VMList.get(VMNum).setBx(Integer.parseInt(Rside));
                }
            }
            else{
                //MOV [hex], values
                //TODO: TEST THIS
                allMemory.get(Integer.parseInt(Word.wordToString(allMemory.get(VMList.get(VMNum).getPtr()).getInstruction(0)).trim())).set(Integer.parseInt(Lside, 16), Word.stringToWord(Rside));

            }
            return;
        }
        if (string.contains("GET")){
            //GET value, returns pointer in AX 1<value<16
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            printToConsole("Vykdoma GET "+string);
            if(Integer.parseInt(string)<1 || Integer.parseInt(string)>16){
                printToConsole("GET out of bounds, should be [1, 16] you donkey");
                this.ei = 3;
            }
            else{
                this.ii = 9;
            }
            return;
        }
        if (string.contains("PRR")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc()).trim();
            VMList.get(VMNum).incCc();
            printToConsole("Vykdoma PRR "+string);
            if(isVMRegister(string)){
                if(string.equals("AX")){
                    //PRR AX
                    this.ii = 1;
                }
                else{
                    //PRR BX
                    this.ii = 2;
                }
            }
            else
                printToConsole("Turejai registra ivest tu asilo berete");
                this.ei = 3;
            return;
        }
        if (string.contains("PRS")){
            //Isspausdina pasirinkta atmienties bloka PRS reg , kur reg-AX/BX
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc()).trim();
            VMList.get(VMNum).incCc();
            printToConsole("Vykdoma PRS "+string);
            if(isVMRegister(string)){
                if(string.equals("AX")){
                    //PRS AX
                    this.ii = 3;
                }
                else{
                    //PRS BX
                    this.ii = 4;
                }
            }
            else{
                printToConsole("Should be a register you mofo");
                this.ei = 3;
            }
            return;
        }
        if (string.contains("WGD")){
            //Nusikelia po OS
            //Kodel nepridejai interrupt'u xddd?


            ii = 3;
            return;
        }
        if (string.contains("RGD")){
            //Nusikelia po OS
            //Kodel nepridejai interrupt'u xddd?
            ii = 4;
            return;
        }
        if (string.contains("LGD")){
            //Nusikelia po OS
            //Kodel nepridejai interrupt'u xddd?
            ii = 5;
            return;
        }
        if (string.contains("UGD")){
            //Nusikelia po OS
            //Kodel nepridejai interrupt'u xddd?
            ii = 6;
            return;
        }
        if (string.equals("JMP")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            printToConsole("Vykdoma JMP "+string);
            VMList.get(VMNum).setCc(Integer.parseInt(string));
            return;
        }
        if (string.equals("JEZ")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            printToConsole("Vykdoma JEZ "+Lside+", "+Rside);
            if(isVMRegister(Lside)){
                printToConsole("Lside should not be a register");
                this.ei = 3;
            }
            else{
                if(isVMRegister(Rside)){
                    if(Rside.equals("AX")){
                        //JEZ [hex], AX
                        if(VMList.get(VMNum).getAx()==0){
                            VMList.get(VMNum).setCc(Integer.parseInt(Lside, 16));
                        }
                    }
                    else{
                        //JEZ [hex], BX
                        if(VMList.get(VMNum).getBx()==0){
                            VMList.get(VMNum).setCc(Integer.parseInt(Lside, 16));
                        }
                    }
                }
                else{
                    printToConsole("Rside should be a register");
                    this.ei = 3;
                }
            }
            return;
        }
        if (string.equals("JNZ")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            printToConsole("Vykdoma JNZ "+Lside+", "+Rside);
            if(isVMRegister(Lside)){
                printToConsole("Lside should not be a register");
                this.ei = 3;
            }
            else{
                if(isVMRegister(Rside)){
                    if(Rside.equals("AX")){
                        //JEZ [hex], AX
                        if(VMList.get(VMNum).getAx()!=0){
                            VMList.get(VMNum).setCc(Integer.parseInt(Lside, 16));
                        }
                    }
                    else{
                        //JEZ [hex], BX
                        if(VMList.get(VMNum).getBx()!=0){
                            VMList.get(VMNum).setCc(Integer.parseInt(Lside, 16));
                        }
                    }
                }
                else{
                    printToConsole("Rside should be a register");
                    this.ei = 3;
                }
            }
            return;
        }
        if (string.equals("JGZ")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            printToConsole("Vykdoma JGZ "+Lside+", "+Rside);
            if(isVMRegister(Lside)){
                printToConsole("Lside should not be a register");
                this.ei = 3;
            }
            else{
                if(isVMRegister(Rside)){
                    if(Rside.equals("AX")){
                        //JEZ [hex], AX
                        if(VMList.get(VMNum).getAx()>0){
                            VMList.get(VMNum).setCc(Integer.parseInt(Lside, 16));
                        }
                    }
                    else{
                        //JEZ [hex], BX
                        if(VMList.get(VMNum).getBx()>0){
                            VMList.get(VMNum).setCc(Integer.parseInt(Lside, 16));
                        }
                    }
                }
                else{
                    printToConsole("Rside should be a register");
                    this.ei = 3;
                }
            }
            return;
        }
        if (string.equals("JLZ")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            printToConsole("Vykdoma JLZ "+Lside+", "+Rside);
            if(isVMRegister(Lside)){
                printToConsole("Lside should not be a register");
                this.ei = 3;
            }
            else{
                if(isVMRegister(Rside)){
                    if(Rside.equals("AX")){
                        //JEZ [hex], AX
                        if(VMList.get(VMNum).getAx()<0){
                            VMList.get(VMNum).setCc(Integer.parseInt(Lside, 16));
                        }
                    }
                    else{
                        //JEZ [hex], BX
                        if(VMList.get(VMNum).getBx()<0){
                            VMList.get(VMNum).setCc(Integer.parseInt(Lside, 16));
                        }
                    }
                }
                else{
                    printToConsole("Rside should be a register");
                    this.ei = 3;
                }
            }
            return;
            }
            if (string.contains("END")){
                //END();
                return;
            }
            printToConsole("Kaska neto ivedei");
            this.ei = 2;
    }

}