import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ConsoleInputs implements Runnable{
    volatile String  command = null;
    volatile boolean newInput = false;

    ArrayList<String> commandList = new ArrayList<String>();

    public ConsoleInputs() throws IOException {
    }
    public void run(){
        while(true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            try {
                String temp = br.readLine();
                newInput = true;
                command = temp;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String getLastCommand(){

        if(newInput) {
            String temp = command;
            newInput = false;
            return temp;
        }
        else return null;
    }
}
