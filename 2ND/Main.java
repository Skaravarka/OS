public class Main {

    /*
    TODO: 
        setReadFile
        pager
        interupt = 0

        Changes:
        Added: Run till complete functionality


        Btw MOR ir EQL komandos don't do shit
        Jos setina sf, prilausomai true ar false, kas is to XD



    */
    public static void main(String[] args) throws InterruptedException {

        System.out.println("Helllooo, Welcum to our computah");

        Thread RM1 = new Thread(new RealMachine());

        RM1.start();

        RM1.join();
    }
}