public class Main {

    /*
    TODO:
        

    */
    public static void main(String[] args) {
        System.out.println("Hello, World");

        VirtualMachine virtualMachine = new VirtualMachine(0, 0, 0, 0, 0, 0);
        
        virtualMachine.loadToMemory("2ND/PROG1.txt");
        
    }

}