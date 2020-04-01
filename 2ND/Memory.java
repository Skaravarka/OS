import java.util.ArrayList;

public class Memory{
    private ArrayList<Word> virtualMemory = new ArrayList<Word>();

    public void PrintAll(){
        System.out.println(this.virtualMemory);
    }
}