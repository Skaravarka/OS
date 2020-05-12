public class Main {

    /*
    TODO: 


        IMPORTANT: Pratestinti komandas, ir surasyt kokiais atvejais neveikia, kad galeciau pataisyt

        Galetu padirbet and input faila, kai nera 4char komanda.



    */
    public static void main(String[] args) throws InterruptedException {

        System.out.println("Helllooo, Welcum to our computah");

        Thread RM1 = new Thread(new RealMachine());

        RM1.start();

        RM1.join();
    }
}