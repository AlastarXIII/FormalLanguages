import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.*;

public class CalculatorTests {

    @Test
    void testLexer_UnknownSymbol_ShouldThrowException(){
        IOException thrown = Assertions.assertThrows(IOException.class, () -> {
            File file = new File("file.txt");
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("?");
            bw.close();
            new Lexer(new BufferedReader(new FileReader(file))).nextToken();
            file.deleteOnExit();
        });

        Assertions.assertNotEquals(null, thrown, "Exception wasn't triggered.");
        Assertions.assertEquals("Unknown symbol.", thrown.getMessage(), "Wrong exception was triggered.");
    }

    @Test
    void testLexer_NumberSymbol_ShouldAssignValue(){
        int value = 0;
        try {
            File file = new File("file.txt");
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("5");
            bw.close();
            Lexer lexer = new Lexer(new BufferedReader(new FileReader(file)));
            lexer.nextToken();
            value = lexer.getValue();
            file.deleteOnExit();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        Assertions.assertEquals(5, value, "Value wasn't assigned.");
    }

    @Test
    void testLexer_ReaderNull_ShouldThrowException(){
        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> new Lexer(null));

        Assertions.assertNotEquals(null, thrown, "Exception wasn't triggered.");
        Assertions.assertEquals("Reader cannot be null.", thrown.getMessage(), "Wrong exception was triggered.");
    }

    @Test
    void testLexer_EmptyFile_ShouldReturnEOF(){
        Token token = null;
        try {
            File file = new File("file.txt");
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("");
            bw.close();
            Lexer lexer = new Lexer(new BufferedReader(new FileReader(file)));
            token = lexer.nextToken();
            file.deleteOnExit();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        Assertions.assertEquals(Token.EOF, token, "EOF token wasn't returned.");
    }

    @Test
    void testParser_LexerNull_ShouldThrowException(){
        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> new Parser(null));

        Assertions.assertNotEquals(null, thrown, "Exception wasn't triggered.");
        Assertions.assertEquals("Lexer cannot be null.", thrown.getMessage(), "Wrong exception was triggered.");
    }

    @Test
    void testParser_SimpleExpression_MultiplicationShouldHaveHigherPriorityThanDivision(){
        int result = 0;
        try {
            File file = new File("file.txt");
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("6/2*3");
            bw.close();
            result = new Parser(new Lexer(new BufferedReader(new FileReader(file)))).statement();
            file.deleteOnExit();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        Assertions.assertEquals(1, result, "Result wasn't correct.");
        Assertions.assertNotEquals(9, result, "Multiplication doesn't have higher priority than division.");
    }

    @Test
    void testParser_SingleParenthesesExpression_ParenthesesShouldHaveHighestPriority(){
        int result = 0;
        try {
            File file = new File("file.txt");
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("9/3*(-2+5)+1");
            bw.close();
            result = new Parser(new Lexer(new BufferedReader(new FileReader(file)))).statement();
            file.deleteOnExit();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        Assertions.assertEquals(2, result, "Parentheses don't have the highest priority.");
    }

    @Test
    void testParser_MultipleParenthesesExpression_InnerParenthesesShouldHaveHighestPriority(){
        int result = 0;
        try {
            File file = new File("file.txt");
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            // 2 * (15 / (9 - 24))
            bw.write("(2*(15/((4+5)-(8*(2+1)))))");
            bw.close();
            result = new Parser(new Lexer(new BufferedReader(new FileReader(file)))).statement();
            file.deleteOnExit();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        Assertions.assertEquals(-2, result, "Inner parentheses don't have the highest priority.");
    }

    @Test
    void testParser_UnclosedParentheses_ShouldThrowCalculatorException(){
        CalculatorException thrown = Assertions.assertThrows(CalculatorException.class, () -> {
            File file = new File("file.txt");
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("(2*(15/((4+5)-(8*(2+1))))");
            bw.close();
            new Parser(new Lexer(new BufferedReader(new FileReader(file)))).statement();
            file.deleteOnExit();
        });

        Assertions.assertNotEquals(null, thrown, "Exception wasn't triggered.");
        Assertions.assertEquals("No matching parentheses found.", thrown.getMessage(), "Wrong exception was triggered.");
    }

    @Test
    void testParser_IncompleteExpression_ShouldThrowCalculatorException(){
        CalculatorException thrown = Assertions.assertThrows(CalculatorException.class, () -> {
            File file = new File("file.txt");
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("2+5*");
            bw.close();
            new Parser(new Lexer(new BufferedReader(new FileReader(file)))).statement();
            file.deleteOnExit();
        });

        Assertions.assertNotEquals(null, thrown, "Exception wasn't triggered.");
        Assertions.assertEquals("Expression is incomplete.", thrown.getMessage(), "Wrong exception was triggered.");
    }

    @Test
    void testParser_UnaryMinusBeforeParentheses_ShouldMakeWholeExpressionNegative(){
        int result = 0;
        try {
            File file = new File("file.txt");
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            // 2 * (15 / (9 - 24))
            bw.write("-(2*(15/((4+5)-(8*(2+1)))))");
            bw.close();
            result = new Parser(new Lexer(new BufferedReader(new FileReader(file)))).statement();
            file.deleteOnExit();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        Assertions.assertEquals(2, result, "Unary minus didn't affect the parentheses.");
    }
}
