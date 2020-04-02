import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ConsoleInputs implements Runnable{
    String command = null;
    boolean newInput = false;
    boolean comStart = false;
    boolean script = false;
    private int i = 0;

    ArrayList<String> commandList = new ArrayList<String>();

    public ConsoleInputs() throws IOException {
    }
    public void run(){
        while(true) {
            System.out.println("consoleRun");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            try {
                // if(!script) {
                //     String temp = br.readLine();
                //     if (temp.compareTo("new") == 0) {
                //         comStart = true;
                //     }
                //     if (temp.compareTo("end") == 0) {
                //         script = true;
                //         //comStart = true;
                //     }
                //     if (comStart) {
                //         commandList.add(temp);
                //     } else {
                //         command = temp;
                //         newInput = true;
                //         System.out.println(command);
                //     }
                // }else {
                //     //System.out.println(commandList.size());
                //     if (commandList.get(0).compareTo("end") != 0) {
                //         if (waitABit()){
                //             command = commandList.get(0);
                //             newInput = true;
                //             commandList.remove(0);
                //             if (commandList.size() == 1) {
                //                 script = false;
                //                 comStart = false;
                //                 commandList.clear();
                //             }
                //         }
                //     }
                // }
                
                String temp = br.readLine();
                newInput = true;
                command = temp;
                System.out.println("miau" + command);
                System.out.println(newInput);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean waitABit(){
        i++;
        if(i == 50000){
            i = 0;
            return true;
        }
        return false;
    }
    public String getLastCommand(){
        //System.out.println(command);
        if(command != null) {
            System.out.println("@@@lastComman@@@");
            //System.out.println("newImput");
            String temp = command;
            //command = null;
            return temp;
        }
        else return null;
    }
}
