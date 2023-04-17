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
        int value = operand();

        while (symbol == Token.MUL){
            consume();
            value *= operand();
        }

        return value;
    }

    private int operand(){
        int value = 0;

        switch (symbol) {
            case NUMBER -> value = digit();
            case LPAR -> {
                consume();
                value = expr();
                if(symbol != Token.RPAR)
                    throw new CalculatorException("No matching parenthesis found.");
                consume();
            }
            case MINUS -> {
                consume();
                value = -operand();
            }
            case EOF -> throw new CalculatorException("Expression is incomplete.");
        }

        return value;
    }

    private int digit(){
        int value = lexer.getValue();
        consume();

        return value;
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
