public class ConsoleOutput implements Runnable {
    private volatile String printString = "";
    private volatile boolean isAlive = true;

    public ConsoleOutput() {
    }

    public void run() {
        while (isAlive) {
            if (printString.compareTo("") != 0) {
                System.out.println(printString);
                printString = "";
            }
        }
    }
    public void sendToOutput(String string){
        printString = string;
    }
    public void killThread(){
        isAlive = false;
    }
    public synchronized void waitThread() {
        try {
            this.wait();
            //Thread.currentThread().wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
}