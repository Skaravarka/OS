import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

    // puslapio trasliacija turetu buti 50, skaito reiksme, nuskaito kaska ka vygdo ir sumapina
    //VM neegzistuoja, 16x16
    // Registras, kuris rodo i GD, ir MP-geras
    // TI REGISTRA PADARYT!!!
    // PI, SI, kai instrukcija vykdoma, baige sakini jis
    // 0

public class RealMachine implements Runnable {
    private int activeMachine = 0;
    ArrayList<Supervisor> supervisorMemory = new ArrayList<Supervisor>();
    ArrayList<Memory> allMemory = new ArrayList<Memory>();
    ArrayList<VirtualMachine> VMList = new ArrayList<VirtualMachine>();
    private Thread consoleInputsThread;
    private ConsoleInputs consoleInputs;
    private Thread consoleOutputThread;
    private ConsoleOutput consoleOutputs;
    private int DEFAULTTI = 10;
    private int TIMERCOMMAND = 2;
    private int TIMERINTERUPT = 3;
    private boolean mode = false;
    private int TI = DEFAULTTI;
    private int ptr = 0; 
    private int sf = 0;
    private int ax = 0;  // darbinis
    private int bx = 0;  // darbinis
    private int cx = 0;  // darbinis
    private int dx = 0;  // darbinis
    private int chr = 0; // kanalu valdymo
    private int cc = 0;  // virtualios masinos komandu
    private int dc = 0;  // duomenu skaitliukas
    private boolean[] ml = {false, false, false, false, 
                            false, false, false, false, 
                            false, false, false, false, 
                            false, false, false, false };
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
        printToConsole("To print General Memory: w");
    }
    public void printVMlist() {
        for (int i = 0; i < VMList.size(); i++) {
            System.out.print(i + " ");
        }
        printToConsole("");
    }
    public void createMemory() {
        for (int i = 0; i < 16; i++) {
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
        String temp = "";
        int i = 0;
        while(cc >= 16){
            cc -= 16;
            i++;
        }

        return Word.wordToString(
                allMemory.get(Integer.parseInt(Word.wordToString(allMemory.get(ptr).getInstruction(i)).trim()))
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
    private int getRandomMemoryBlock(){
        int count = 0;
        for(int i = 0; i < ml.length; i++){
            if(ml[i] == false){
                count++;
            }
        }
        if(count <= 0){
            return -1;
        }
        Random rand = new Random();
        int a = rand.nextInt(allMemory.size() - 1);
        while(ml[a] == true){
            a = rand.nextInt(allMemory.size() - 1);
        }
        ml[a] = true;
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
        int i = 0;
        while(Word.wordToString(allMemory.get(VMList.get(command).getPtr()).getInstruction(i)).trim().compareTo("0000") != 0){
            allMemory.get(Integer.parseInt(Word.wordToString(allMemory.get(VMList.get(command).getPtr()).getInstruction(i)).trim())).printAllNicely(i);
            i++;
        }


    }
    private void printVmRegisters(){
        printToConsole(" Available VMs:");
        printVMlist();
        int command = Integer.parseInt(getConsoleCommand());
        printToConsole("Virtual machine nr: "+command+" registers:");
        VMList.get(command).printAllRegisters();
    }
    private int getSomeMemoryBlock(){
        int num = -1;
        printToConsole("Free memories:");
        for(int i = 0; i < ml.length; i++){
            if(ml[i] == false){
                System.out.print(i + " ");
            }
        }
        printToConsole("");
        while(true){
            num = Integer.parseInt(getConsoleCommand());
            if(num >= 0 && num < ml.length){
                if(ml[num] == false){
                    break;
                }
            }
        }
        ml[num] = true;

        return num;
    }
    private void addVirtualMachine() {
        int page = getSomeMemoryBlock();
        VirtualMachine virtualMachine = new VirtualMachine();
        virtualMachine.setPtr(page);

        supervisorMemory.add(new Supervisor());
        VMList.add(virtualMachine);
    }
    private void saveAndLoadSupervisorMemory(int a, int b){
        supervisorMemory.get(a).setAx(ax);
        supervisorMemory.get(a).setBx(bx);
        supervisorMemory.get(a).setCx(cx);
        supervisorMemory.get(a).setDx(dx);
        supervisorMemory.get(a).setCc(cc);
        supervisorMemory.get(a).setChr(chr);
        supervisorMemory.get(a).setDc(dc);
        supervisorMemory.get(a).setEi(ei);
        supervisorMemory.get(a).setIi(ii);
        supervisorMemory.get(a).setMode(mode);
        supervisorMemory.get(a).setPtr(ptr);
        supervisorMemory.get(a).setSf(sf);
        supervisorMemory.get(a).setTI(TI);
        
        ax = supervisorMemory.get(b).getAx();
        bx = supervisorMemory.get(b).getBx();
        cc = supervisorMemory.get(b).getCc();
        chr = supervisorMemory.get(b).getChr();
        cx = supervisorMemory.get(b).getCx();
        dc = supervisorMemory.get(b).getDc();
        dx = supervisorMemory.get(b).getDx();
        ei = supervisorMemory.get(b).getEi();
        ii = supervisorMemory.get(b).getIi();
        ptr = supervisorMemory.get(b).getPtr();
        sf = supervisorMemory.get(b).getSf();
        TI = supervisorMemory.get(b).getTI();
        mode = supervisorMemory.get(b).isMode();

    }
    private void loadToMemory(){
        String command = consoleInputs.getLastCommand();

        printToConsole("Available VMs:");
        printVMlist();
        printToConsole("");
        command = getConsoleCommand();
        int numVM = Integer.parseInt(command);
        
        printToConsole("Program name:");
        command = getConsoleCommand();
        if (command.equals(" ") || command.equals("1")) {
            command = "PROGURAMUUWU.txt";
            printToConsole("Loading default 'PROGURAMUUWU.txt'");
        }
        command = "2ND/" + command;

        int flagCount = 0;
        int flag = -1;
        while(flag == -1){
            int num = getRandomMemoryBlock();
            //allMemory.get(VMList.get(numVM).getPtr()).set((int)flagCount / 16, Word.stringToWord("   " + Integer.toString(num)));
            allMemory.get(VMList.get(numVM).getPtr()).set((int)flagCount / 16, Word.stringToWord(Word.numberPadding(num)));
            flag = allMemory.get(num).loadToMemory(command, flagCount);
            flagCount += 16;
        }

        int instructinCount = allMemory.get(0).getInstructionCount(command);
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
        supervisorMemory.add(new Supervisor());
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
                if (command.equals("w")){
                    printGeneralMemory();
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
    }
    private void runVirtualMachines(){
        if(!isFinished(activeMachine)){
            printToConsole("Doing a step, executing line: "+(VMList.get(activeMachine).getCc()+1));
            doStep(activeMachine); 
        }
        else {
            ii = 10;
            processErrors();
        }
    }
    private void runVirtualMachineTillCompletion(){
        for(int i = 0; i < VMList.size(); i++){
            for(TI = 0; TI < DEFAULTTI; TI++){
                if(!isFinished(i)){
                    printToConsole("Doing a step, executing line: "+(VMList.get(i).getCc()+1));
                    doStep(i); 
                }
                else break;
            }
        }

        // for(TI = 0; TI < DEFAULTTI + 1; TI++){
            
        //     if(TI == DEFAULTTI){
        //         ei = 4;
        //         processErrors();
        //         return;
        //     }
        //     int count = 0;
        //     for (int i = 0; i < VMList.size(); i++){
        //         if(! isFinished(i)){
        //             printToConsole("Doing a step, executing line: "+(VMList.get(i).getCc()+1));
        //             doStep(i);
        //             //VMList.get(i).doStep();

        //             //interuptManagement(VMList.get(i).getSf(), ptr[i], i);
        //         } 
        //         else count += 1;
        //     }
        //     if(count == VMList.size()){
        //         printToConsole("Done");
        //         return;
        //     }
        // }
    }
    public void doStep(int VMNum){
        printToConsole("Doing Vm nr:"+VMNum+" step");
        executeCommand(VMNum);
        processInterupts(VMNum);
        processErrors();
        if(TI < 0){
            ii = 11;
            processInterupts(VMNum);
        }
        processRegisters(VMNum);
    }
    public boolean isFinished(int VMNum){
        if(paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc()).equals("HALT")){
            return true;
        }
        else{
            return false;
        }
    }
    private void handleTimer(){
        if(VMList.size() - 1 > activeMachine){
            saveAndLoadSupervisorMemory(activeMachine + 1, activeMachine + 2);
            activeMachine++;
            TI = DEFAULTTI;
        }
        else {
            saveAndLoadSupervisorMemory(activeMachine + 1, 1);
            activeMachine = 0;
            TI = DEFAULTTI;
        }
    }
    private void handlePRS(int VMNum){

        int left = VMList.get(VMNum).getAx();
        int right = VMList.get(VMNum).getBx();
        if(left > right){
            ei = 2;
            return;
        }
        String temp = "";
        while(left < right){
            temp = temp + paging(VMList.get(VMNum).getPtr(), left);
            //printToConsole(paging(VMList.get(VMNum).getPtr(), left));
            left++;
        }
        printToConsole(temp);
    }
    private void handleWGD(int i, int VMnum){ // write to general memory
        if(i == 0){
            //WGD AX, value
            int index = ax;
            String str = String.valueOf(VMList.get(VMnum).getAx());
            //Padding string so Word.stringTOWord wouldn't complain
            String padded = "    ".substring(str.length())+str;
            Word word = Word.stringToWord(padded);
            allMemory.get(allMemory.size()-1).set(index, word);
        }
        else{
            //WGD BX, value
            int index = ax;
            String str = String.valueOf(VMList.get(VMnum).getBx());
            //Padding string so Word.stringTOWord wouldn't complain
            String padded = "    ".substring(str.length())+str;
            Word word = Word.stringToWord(padded);
            allMemory.get(allMemory.size()-1).set(index, word);
        }
    }
    private void handleRGD(int i, int VMnum){ // read from general memory
        if(i==0){
            //RGD AX, value
            int index = ax;
            Word got = allMemory.get(allMemory.size()-1).getInstruction(index);
            String alabama = Word.wordToString(got);
            int number = Integer.parseInt(alabama.trim());
            VMList.get(VMnum).setAx(number);
        }
        else{
            //RGD BX, value
            int index = ax;
            Word got = allMemory.get(allMemory.size()-1).getInstruction(index);
            String alabama = Word.wordToString(got);
            int number = Integer.parseInt(alabama.trim());
            VMList.get(VMnum).setBx(number);
        }
    }
    private void handleLGD(int VMnum){// lock general memory
        if(ax>15){
            return;
        } 
        if(!mp[ax]){
            mp[ax] = true;
            VMList.get(VMnum).setMp(ax);
        }
        else ei = 1;
        
    }
    private void handleUGD(int VMnum){ // unlock general memory
        if(ax>15){
            return;
        }
        mp[ax] = false;
        VMList.get(VMnum).setMp(ax);
    }
    private boolean checkCell(int cell, int VMNum){
        int a = 0;
        while(cell>16){
            cell = cell - 16;
            a++;
        }
        System.out.println("Checking cell:"+a);
        if(mp[a] == true){
            if(VMList.get(VMNum).getMp() == a){
                //Aka. vmas turi pats ussirakines sita
                return false;
            }
            return true;
        }
        else{
            return false;
        }

    }
    private void printGeneralMemory(){
        printToConsole("General Memory:");
        int q = 0;
        int w = 0;
        for (int i = 0; i < 256; i++) {
            q++;
            System.out.print(Word.wordToString(allMemory.get(allMemory.size() - 1).getInstruction(i))+" ");
            if(q == 16){
                if(mp[w]==true){
                    System.out.println(" Locked");
                }
                else{
                    System.out.println(" Unlocked");
                }
                q = 0;
                w++;
            }
        }
        System.out.println();
    }
    private void processInterupts(int VMnum){
        TI = TI - 1;
        switch(ii){
            case 0:
                TI = TI + 1;
                break;
            case 1:
                printToConsole(Integer.toString(VMList.get(VMnum).getAx()));
                ii = 0;
                break;
            case 2:
                printToConsole(Integer.toString(VMList.get(VMnum).getBx()));
                ii = 0;
                break;
            case 3:
                // PRS AX, BX
                handlePRS(VMnum);
                break;
            case 4:
                //Empty
                break;
            case 5:
                // WGD AX, value
                handleWGD(0, VMnum);
                break;
            case 6:
                // WGD BX, value
                handleWGD(1, VMnum);
                break;
            case 7:
                // RGD AX, value
                handleRGD(0, VMnum);
                break;
            case 8:
                // RGD BX, value
                handleRGD(1, VMnum);
                break;
            case 9:
                // LGD value
                handleLGD(VMnum);
                break;
            case 10:
                // UGD value
                handleUGD(VMnum);
            case 11:
                handleTimer();
                break;    
            default:
                break;
        }
    }
    private void processErrors(){
        switch(ei){
            case 1:
                printToConsole("!EI 1! Cell locked");
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
    private void processRegisters(int VMNum){
        ax = VMList.get(VMNum).getAx();
        bx = VMList.get(VMNum).getBx();
        cc = VMList.get(VMNum).getCc();
        ptr = VMList.get(VMNum).getPtr();
        sf = VMList.get(VMNum).getSf();
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
        for(int i = 0;i < mp.length; i++){
            if(mp[i] == false)
                System.out.print(0);
            else System.out.print(1);
        }
        System.out.println();
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
        System.out.print(" |ML: ");
        for(int i = 0;i < mp.length; i++){
            if(ml[i] == false)
                System.out.print(0);
            else System.out.print(1);
        }
        System.out.println();
    }
    public void executeCommand(int VMNum){

        TI = TI - TIMERCOMMAND;

        if(paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc()).equals("HALT")){
            return;
        }
        
        String string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc()).trim();
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
            printToConsole("Vykdoma PRR "+"'"+string+"'");
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
            else{
                //printToConsole("Turejai registra ivest tu asilo berete");
                this.ei = 3;
            }
            return;
        }
        if (string.contains("PRS")){
            //Isspausdina pasirinkta atmienties bloka PRS reg , kur reg-AX/BX
            printToConsole("Vykdoma PRS ");
            //PRS AX, BX
            ii = 3;
            return;
        }
        if (string.contains("WGD")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            printToConsole("Vykdoma WGD "+Lside+", "+Rside);
            if(checkCell(Integer.parseInt(Rside, 16), VMNum)){
                VMList.get(VMNum).setCc(VMList.get(VMNum).getCc()-2);
                ei = 1;
                return;
            }
            if(isVMRegister(Lside)){
                if(Lside.equals("AX")){
                    //WGD AX, value
                    ax = Integer.parseInt(Rside, 16);
                    ii = 5; 
                }
                else{
                    //WGD BX, value
                    ax = Integer.parseInt(Rside, 16);
                    ii = 6;
                }
            }
            else{
                //printToConsole("Should be a register you mofo");
                ei = 3;
            }
            return;
        }
        if (string.contains("RGD")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            printToConsole("Vykdoma WGD "+Lside+", "+Rside);
            if(checkCell(Integer.parseInt(Rside, 16), VMNum)){
                VMList.get(VMNum).setCc(VMList.get(VMNum).getCc()-2);
                ei = 1;
                return;
            }
            if(isVMRegister(Lside)){
                if(Lside.equals("AX")){
                    //WGD AX, value
                    ax = Integer.parseInt(Rside, 16);
                    ii = 7; 
                }
                else{
                    //WGD BX, value
                    ax = Integer.parseInt(Rside, 16);
                    ii = 8;
                }
            }
            else{
                //printToConsole("Should be a register you mofo");
                ei = 3;
            }
            return;
        }
        if (string.contains("LGD")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc()).trim();
            VMList.get(VMNum).incCc();
            printToConsole("Vykdoma LGD "+string);
            //LGD value
            ax = Integer.parseInt(string);
            ii = 9;
            return;
        }
        if (string.contains("UGD")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc()).trim();
            VMList.get(VMNum).incCc();
            printToConsole("Vykdoma UGD "+string);
            //UGD value
            ax = Integer.parseInt(string);
            ii = 10;
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
                ei = 3;
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
                    ei = 3;
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
                ei = 3;
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
                    ei = 3;
                }
            }
            return;
            }
            if (string.contains("END")){
                //END();
                return;
            }
            printToConsole("Kaska neto ivedei");
            ei = 2;
    }

}