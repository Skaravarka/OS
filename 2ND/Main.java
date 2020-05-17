public class Main {

    /*
    TODO: 


        IMPORTANT: Pratestinti komandas, ir surasyt kokiais atvejais neveikia, kad galeciau pataisyt
        ps. General memory perdariau su lastelem, o ne vienu bloku

        Galetu padirbet and input faila, kai nera 4char komanda.


        [ ] supervizorine
        [x] paging
        [ ] real machine registrai
        [x] atmintis 16x16
        [x] make instructions to work with 16x16 memory
        [ ] TI registras
        [ ] PTR redo
        [ ] general memory pointer
        [ ] PI, SI???????????


    */
    public static void main(String[] args) throws InterruptedException {

        System.out.println("Helllooo, Welcome to our computah");

        Thread RM1 = new Thread(new RealMachine());

        RM1.start();

        RM1.join();
    }
}