public class Main {

    /*
    TODO: 

        Galetu padirbet and input faila, kai 3char vietoj 4char.



    */
    public static void main(String[] args) throws InterruptedException {

        System.out.println("Helllooo, Welcum to our computah");

        Thread RM1 = new Thread(new RealMachine());

        RM1.start();

        RM1.join();
    }
}