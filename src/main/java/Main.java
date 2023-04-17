import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args){
        try {
            FileReader fr = new FileReader("file.txt");
            BufferedReader br = new BufferedReader(fr);
            Lexer lexer = new Lexer(br);
            Parser parser = new Parser(lexer);
            System.out.println(parser.statement());
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
