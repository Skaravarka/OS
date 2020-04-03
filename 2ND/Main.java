import java.util.Scanner;

public class Main {

    /*
    TODO: 
        setReadFile
        pager
        interupt = 0


    */
    public static void main(String[] args) {

        System.out.println("Helllooo, Welcum to our computah");

        Thread RM1 = new Thread(new RealMachine());

        RM1.start();

    }

}