
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;


public class Word {
    public static final int SIZE = 4; //bytes
    private final byte[] data;
    
    public Word(){
        data = new byte[SIZE];
    }
    
    public Word(Word src){
        data = src.data.clone();
    }
    
    public byte getByte(int index){
        return data[index];
    }
    public void setByte(int index, byte info){
        data[index] = info;
    }
    @Override
    public Word clone(){
        return new Word(this);
    }
    
    public boolean equals(Word another){
        return Arrays.equals(data, another.data);
    }

    byte[] getBytes() {
        return Arrays.copyOf(data, SIZE);
    }
    
    public static int wordToInt(Word word) {
        ByteBuffer bb = ByteBuffer.allocateDirect(SIZE);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.clear();
        for(int i = 0; i < SIZE; i++){
            bb.put(word.getByte(i));
        }
        bb.position(0);
        return bb.getInt();
    }
    
    public static Word intToWord(int value) {
        ByteBuffer bb = ByteBuffer.allocateDirect(SIZE);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.clear();
        bb.putInt(value);
        Word word = new Word();
        for(int i = 0; i < SIZE; i++){
            word.setByte(i, bb.get(i));
        }
        return word;
    }
    public static Word stringToWord(String string){
        Word tempWord = new Word();
        int ascii = 0;
        if(string.length() > SIZE){
            System.out.println("Error");     
        }
        for(int i = 0; i < SIZE; i++){
            ascii = ascii * 100;
            ascii = ascii + (int) string.charAt(i);
        }
        tempWord = Word.intToWord(ascii);
        //System.out.println(ascii);
        //System.out.println(Word.wordToInt(tempWord));
        return tempWord;
    }
    public static String wordToString(Word word){
        String tempString = "";
        int tempInt = Word.wordToInt(word);
        String temp = Integer.toString(tempInt);
        for(int i = 0; i < temp.length(); i = i + 2){
            int k = Integer.parseInt(temp.substring(i, i+2));
            tempString += Character.toString ((char) k); 
        }
        return tempString;
    }
}