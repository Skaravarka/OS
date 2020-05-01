public class Main {

    /*
    TODO: 
        setReadFile
        pager
        interupt = 0

        Changes:
        Added: Handling strings that are too long in stringToWord
        Added: Reading entire program to memory
        Added: Vm set Cc after loading program



    */
    public static void main(String[] args) throws InterruptedException {

        System.out.println("Helllooo, Welcum to our computah");

        Thread RM1 = new Thread(new RealMachine());

        RM1.start();

        RM1.join();
    }
}