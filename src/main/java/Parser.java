import java.io.IOException;

public class Parser {

    private Token symbol;
    private final Lexer lexer;

    public Parser(Lexer lexer) {
        if(lexer == null)
            throw new RuntimeException("Lexer cannot be null.");
        this.lexer = lexer;
    }

    public double statement(){
        consume();
        return expr();
    }

    private double expr(){
        double value = div();

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

    private double div(){
        double value = mul();

        while (symbol == Token.DIV || symbol == Token.REMAINDER){
            switch (symbol) {
                case DIV -> {
                    consume();
                    value /= mul();
                }
                case REMAINDER -> {
                    consume();
                    value %= mul();
                }
            }
        }

        return value;
    }

    private double mul(){
        double value = exponentiation();

        while (symbol == Token.MUL) {
            consume();
            value *= exponentiation();
        }

        return value;
    }

    private double exponentiation() {
        double value = operand();

        while (symbol == Token.EXP) {
            consume();
            value = Math.pow(value, operand());
        }

        return value;
    }

    private double operand(){
        double value = 0;

        switch (symbol) {
            case NUMBER -> {
                value = number();
                if(symbol == Token.DOT){
                    consume();
                    value += number();
                    if(symbol == Token.DOT)
                        throw new CalculatorException("Decimals cannot have two dots.");
                }
            }
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

    private double number(){
        double value = lexer.getValue();
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
