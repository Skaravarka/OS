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
        System.out.println("RM:To run VM till completion: 3");
        System.out.println("RM:To terminate VMs: 4");
        System.out.println("RM:To print memory: 5");
        System.out.println("RM:To print registers: 6");
        System.out.println("RM:Load to memory: 7");
        System.out.println("RM:To print VM list: 8");
        System.out.println("RM:To print VM memory: 9");
        System.out.println("RM:To print VM registers: q");
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
    private boolean isVMRegister(String reg){
        if(reg.equals("AX") || reg.equals("BX")){
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

    private void printVmRegisters(){
        System.out.println("RM: Available VMs:");
        printVMlist();
        int command = Integer.parseInt(getConsoleCommand());
        System.out.println("Virtual machine nr: "+command+" registers:");
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
        System.out.println("RM:Free memories:");
        for(int i = 0; i < ptr.length; i++){
            if(ptr[i] == 0){
                System.out.print(i + " ");
            }
        }
        System.out.println("");
        while(true){
            // command = consoleInputs.getLastCommand();
            // if(command != null && !command.equals("")){
            //     num = Integer.parseInt(command);   
            // }
            num = Integer.parseInt(getConsoleCommand());
            if(num >= 0 && num < ptr.length){
                if(ptr[num] == 0){
                    break;
                }
            }
        }
        ptr[num] = 1;
        System.out.println("RM:Program name:");
        // command = "";
        // while (command == "" || command == null) {
        //     command = consoleInputs.getLastCommand();
        // }
        command = getConsoleCommand();
        if (command.equals(" ") || command.equals("1")) {
            command = "PROGURAMUUWU.txt";
            System.out.println("Loading default 'PROGURAMUUWU.txt'");
        }
        command = "2ND/" + command;

        int instructinCount = allMemory.get(num).getInstructionCount(command);
        allMemory.get(num).loadToMemory(command);

        System.out.println("RM:Available VMs:");
        printVMlist();
        System.out.println("");
        // command = "";
        // while (command == "" || command == null) {
        //     command = consoleInputs.getLastCommand();
        // }
        command = getConsoleCommand();
        int numVM = Integer.parseInt(command);
        //VMList.get(numVM).setPtr(num);
        allMemory.get(VMList.get(numVM).getPtr()).set(0, Word.stringToWord("   "+Integer.toString(num)));
        VMList.get(numVM).setCc(instructinCount);
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
                if (command.equals("q")){
                    printVmRegisters();
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
            if(! isFinished(i)){
                System.out.println("Doing a step, executing line: "+(VMList.get(i).getCc()+1));
                doStep(i);
                //VMList.get(i).doStep();

                //interuptManagement(VMList.get(i).getSf(), ptr[i], i);

            }
            else{
                System.out.println("VM nr: "+i+" has finished");
            }
        }
        // do finnished stuff
    }
    private void runVirtualMachineTillCompletion(){
        for(int t = DEFAULTTI; t > 0; t++){
            for (int i = 0; i < VMList.size(); i++){
                if(!isFinished(i)){
                    ///////////////////////////////////////////////////////////VMList.get(i).doStep();
                    //interuptManagement(VMList.get(i).getSf(), ptr[i], i);
                }
            }
        }
        System.out.println(" ");
        System.out.println("finished");
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void doStep(int VMNum){
        System.out.println("Doing Vm nr:"+VMNum+" step");
        executeCommand(VMNum);
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

    public void executeCommand(int VMNum){

        if(paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc()).equals("HALT")){
            return;
        }
        
        String string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc()).trim();
        System.out.println("Original string: "+string);
        VMList.get(VMNum).incCc();
        VMList.get(VMNum).setSf(0);
        

        if (string.equals("ADD")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            System.out.println("Vykdoma: ADD "+Lside+", "+Rside);
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
                System.out.println("Left side is not a register, refer to docs");
            }
            return;
        }
        else if(string.equals("ADDV")){
            //ADD REG, value
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            System.out.println("Vykdoma: ADDV "+Lside+", "+Rside);
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
                System.out.println("Left side is not a register, refer to docs");
            }
            return;
        }
        if (string.equals("SUB")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            System.out.println("Vykdoma: SUB "+Lside+", "+Rside);
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
                System.out.println("Left side is not a register, refer to docs");
            }
            return;
        }
        else if(string.equals("SUBV")){
            //ADD REG, value
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            System.out.println("Vykdoma: SUBV "+Lside+", "+Rside);
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
                System.out.println("Left side is not a register, refer to docs");
            }
            return;
        }
        if (string.contains("MOR")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            System.out.println("Vykdoma MOR "+Lside+", "+Rside);
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
                System.out.println("One of the operands are not registers AX or BX");
            }
            return;
        }
        if (string.contains("EQL")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            System.out.println("Vykdoma MOR "+Lside+", "+Rside);
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
                System.out.println("One of the operands are not registers AX or BX");
            }
            return;
        }
        if (string.equals("LEA")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            System.out.println("Vykdoma LEA "+Lside+", "+Rside);
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
            System.out.println("Vykdoma MOV "+Lside+", "+Rside);
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
        //TODO:GET REWRITE BITCH
        if (string.contains("GET")){
            //GET value, returns pointer in AX 1<value<16
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            System.out.println("Vykdoma MOV "+string);
            if(Integer.parseInt(string)<1 || Integer.parseInt(string)>16){
                System.out.println("GET out of bounds, should be [1, 16] you donkey");
            }
            else{
                VMList.get(VMNum).setSf(Integer.parseInt(string));
            }
            return;
        }
        if (string.contains("PRR")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc()).trim();
            VMList.get(VMNum).incCc();
            System.out.println("Vykdoma PRR "+string);
            if(isVMRegister(string)){
                if(string.equals("AX")){
                    //PRR AX
                    VMList.get(VMNum).setSf(39);
                }
                else{
                    //PRR BX
                    VMList.get(VMNum).setSf(78);
                }
            }
            else
                System.out.println("Turejai registra ivest tu asilo berete");
            return;
        }
        if (string.contains("PRS")){
            //Isspausdina pasirinkta atmienties bloka PRS reg , kur reg-AX/BX
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc()).trim();
            VMList.get(VMNum).incCc();
            System.out.println("Vykdoma PRS "+string);
            if(isVMRegister(string)){
                if(string.equals("AX")){
                    //PRS AX
                    VMList.get(VMNum).setSf(20);
                }
                else{
                    //PRS BX
                    VMList.get(VMNum).setSf(21);
                }
            }
            else{
                System.out.println("Should be a register you mofo");
            }
            return;
        }
        if (string.contains("WGD")){
            //Nusikelia po OS
            return;
        }
        if (string.contains("RGD")){
            //Nusikelia po OS
            return;
        }
        if (string.contains("LGD")){
            //Nusikelia po OS
            return;
        }
        if (string.contains("UGD")){
            //Nusikelia po OS
            return;
        }
        if (string.equals("JMP")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            System.out.println("Vykdoma JMP "+string);
            VMList.get(VMNum).setCc(Integer.parseInt(string));
            return;
        }
        if (string.equals("JEZ")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            System.out.println("Vykdoma JEZ "+Lside+", "+Rside);
            if(isVMRegister(Lside)){
                System.out.println("Lside should not be a register");
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
                    System.out.println("Rside should be a register");
                }
            }
            return;
        }
        if (string.equals("JNZ")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            System.out.println("Vykdoma JNZ "+Lside+", "+Rside);
            if(isVMRegister(Lside)){
                System.out.println("Lside should not be a register");
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
                    System.out.println("Rside should be a register");
                }
            }
            return;
        }
        if (string.equals("JGZ")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            System.out.println("Vykdoma JGZ "+Lside+", "+Rside);
            if(isVMRegister(Lside)){
                System.out.println("Lside should not be a register");
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
                    System.out.println("Rside should be a register");
                }
            }
            return;
        }
        if (string.equals("JLZ")){
            string = paging(VMList.get(VMNum).getPtr(), VMList.get(VMNum).getCc());
            VMList.get(VMNum).incCc();
            String Lside = string.substring(0, 2).trim();
            String Rside = string.substring(2, 4).trim();
            System.out.println("Vykdoma JLZ "+Lside+", "+Rside);
            if(isVMRegister(Lside)){
                System.out.println("Lside should not be a register");
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
                    System.out.println("Rside should be a register");
                }
            }
            return;
            }
            if (string.contains("END")){
                //END();
                return;
            }
            System.out.println("Kaska neto ivedei");
    }

}