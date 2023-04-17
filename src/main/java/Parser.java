import java.io.IOException;

public class Parser {

    private Token symbol;
    private final Lexer lexer;

    public Parser(Lexer lexer) {
        if(lexer == null)
            throw new RuntimeException("Lexer cannot be null.");
        this.lexer = lexer;
    }

    public int statement(){
        consume();
        return expr();
    }

    private int expr(){
        int value = div();

        while (symbol == Token.PLUS || symbol == Token.MINUS){
            switch (symbol) {
                case PLUS -> {
                    consume();
                    value += div();
                }
                case MINUS -> {
                    consume();
                    value -= div();
                }
            }
        }

        return value;
    }

    private int div(){
        int value = mul();

        while (symbol == Token.DIV){
            consume();
            value /= mul();
        }

        return value;
    }

    private int mul(){
        int value = num();

        while (symbol == Token.MUL){
            consume();
            value *= num();
        }

        return value;
    }

    private int num(){
        int value = 0;

        switch (symbol) {
            case NUMBER -> value = term();
            case LPAR -> {
                consume();
                value = expr();
                match(Token.RPAR);
                consume();
            }
            case MINUS -> {
                consume();
                value = -term();
            }
            case EOF -> throw new CalculatorException("Expression is incomplete.");
        }

        return value;
    }

    private int term(){
        int value = lexer.getValue();
        consume();

        while (symbol == Token.NUMBER){
            value *= 10;
            value += lexer.getValue();
            consume();
        }

        return value;
    }

    private void match(Token expectedSymbol){
        if(symbol != expectedSymbol && expectedSymbol == Token.RPAR)
            throw new CalculatorException("No matching parentheses found.");
    }

    private void consume(){
        try {
            symbol = lexer.nextToken();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
