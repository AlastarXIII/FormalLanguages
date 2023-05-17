import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class Lexer {

    private char current;
    private double value;
    private final Reader input;
    private int positionAfterDot;

    public Lexer(BufferedReader input) {
        if(input == null)
            throw new RuntimeException("Reader cannot be null.");
        this.input = input;
        value = 0;
        positionAfterDot = 0;
        consume();
    }

    public Token nextToken() throws IOException {
        value = 0;
        switch (current){
            case '+':
                consume();
                return Token.PLUS;
            case '-':
                consume();
                return Token.MINUS;
            case '/':
                consume();
                return Token.DIV;
            case '*':
                consume();
                return Token.MUL;
            case '(':
                consume();
                return Token.LPAR;
            case ')':
                consume();
                return Token.RPAR;
            case '^':
                consume();
                return Token.EXP;
            case '%':
                consume();
                return Token.REMAINDER;
            case '.':
            case ',':
                consume();
                positionAfterDot++;
                return Token.DOT;
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '0':
                do {
                    if(positionAfterDot == 0)
                        value *= 10;
                    value += Character.getNumericValue(current) / Math.pow(10, positionAfterDot);
                    if(positionAfterDot != 0)
                        positionAfterDot++;
                    consume();
                } while (Character.isDigit(current));
                positionAfterDot = 0;
                return Token.NUMBER;
            case (char) -1:
                return Token.EOF;
            default:
                throw new IOException("Unknown symbol.");
        }
    }

    private void consume(){
        try{
            current = (char)input.read();
        } catch (IOException e) {
            throw new RuntimeException("Reading failed.");
        }
    }

    public double getValue(){
        return value;
    }
}
