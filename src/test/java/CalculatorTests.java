import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@SuppressWarnings("all")
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
            file.delete();
        });

        Assertions.assertNotEquals(null, thrown, "Exception wasn't triggered.");
        Assertions.assertEquals("Unknown symbol.", thrown.getMessage(), "Wrong exception was triggered.");
    }

    @Test
    void testLexer_NumberSymbol_ShouldAssignValue(){
        double result = 0;
        try {
            File file = new File("file.txt");
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("5");
            bw.close();
            Lexer lexer = new Lexer(new BufferedReader(new FileReader(file)));
            lexer.nextToken();
            result = lexer.getValue();
            file.delete();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        Assertions.assertEquals(5, result, "Value wasn't assigned.");
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
            file.delete();
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
        double result = 0;
        try {
            File file = new File("file.txt");
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("6/2*3");
            bw.close();
            result = new Parser(new Lexer(new BufferedReader(new FileReader(file)))).statement();
            file.delete();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        Assertions.assertEquals(1, result, "Result wasn't correct.");
        Assertions.assertNotEquals(9, result, "Multiplication doesn't have higher priority than division.");
    }

    @Test
    void testParser_SingleParenthesesExpression_ParenthesesShouldHaveHighestPriority(){
        double result = 0;
        try {
            File file = new File("file.txt");
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("9/3*(-2+5)+1");
            bw.close();
            result = new Parser(new Lexer(new BufferedReader(new FileReader(file)))).statement();
            file.delete();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        Assertions.assertEquals(2, result, "Parentheses don't have the highest priority.");
    }

    @Test
    void testParser_MultipleParenthesesExpression_InnerParenthesesShouldHaveHighestPriority(){
        double result = 0;
        try {
            File file = new File("file.txt");
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            // 2 * (15 / (9 - 24))
            bw.write("(2*(15/((4+5)-(8*(2+1)))))");
            bw.close();
            result = new Parser(new Lexer(new BufferedReader(new FileReader(file)))).statement();
            file.delete();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        Assertions.assertEquals(-2, result, "Inner parentheses don't have the highest priority.");
    }

    @Test
    void testParser_RemainderShouldHaveLowerPriorityThanMultiplication(){
        double result = 0;
        try {
            File file = new File("file.txt");
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("24%8*(2+1)");
            bw.close();
            result = new Parser(new Lexer(new BufferedReader(new FileReader(file)))).statement();
            file.delete();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        Assertions.assertEquals(0, result, "Inner parentheses don't have the highest priority.");
    }

    @Test
    void testParser_RemainderShouldHaveSamePriorityAsDivision(){
        double result = 0;
        try {
            File file = new File("file.txt");
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("24/8%8/3");
            bw.close();
            result = new Parser(new Lexer(new BufferedReader(new FileReader(file)))).statement();
            file.delete();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        Assertions.assertEquals(1, result, "Inner parentheses don't have the highest priority.");
    }

    @Test
    void testParser_ExponentiationShouldHaveTheHighestPriority(){
        double result = 0;
        try {
            File file = new File("file.txt");
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("24/6*2^2");
            bw.close();
            result = new Parser(new Lexer(new BufferedReader(new FileReader(file)))).statement();
            file.delete();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        Assertions.assertEquals(1, result, "Inner parentheses don't have the highest priority.");
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
            file.delete();
        });

        Assertions.assertNotEquals(null, thrown, "Exception wasn't triggered.");
        Assertions.assertEquals("No matching parenthesis found.", thrown.getMessage(), "Wrong exception was triggered.");
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
            file.delete();
        });

        Assertions.assertNotEquals(null, thrown, "Exception wasn't triggered.");
        Assertions.assertEquals("Expression is incomplete.", thrown.getMessage(), "Wrong exception was triggered.");
    }

    @Test
    void testParser_UnaryMinusBeforeParentheses_ShouldMakeWholeExpressionNegative(){
        double result = 0;
        try {
            File file = new File("file.txt");
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            // 2 * (15 / (9 - 24))
            bw.write("-(2*(15/((4+5)-(8*(2+1)))))");
            bw.close();
            result = new Parser(new Lexer(new BufferedReader(new FileReader(file)))).statement();
            file.delete();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        Assertions.assertEquals(2, result, "Unary minus didn't affect the parentheses.");
    }

    @Test
    void testParser_Decimals_ShouldWorkWithNonDecimals(){
        double result = 0;
        try {
            File file = new File("file.txt");
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("6+3.3-1.1*2");
            bw.close();
            result = new Parser(new Lexer(new BufferedReader(new FileReader(file)))).statement();
            file.delete();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        Assertions.assertEquals(7.1, new BigDecimal(result).setScale(6, RoundingMode.DOWN).doubleValue(), "Unary minus didn't affect the parentheses.");
    }

    @Test
    void testParser_Decimals_DoubleDotShouldResultInError() {
        double result = 0;
        RuntimeException thrown = null;
        try {
            File file = new File("file.txt");
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("5.7+6.87.4");
            bw.close();
            thrown = Assertions.assertThrows(CalculatorException.class, () -> new Parser(new Lexer(new BufferedReader(new FileReader(file)))).statement());
            file.delete();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        Assertions.assertNotEquals(null, thrown, "Exception wasn't triggered.");
        Assertions.assertEquals("Decimals cannot have two dots.", thrown.getMessage(), "Wrong exception was triggered.");
    }
}
