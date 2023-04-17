# FormalLanguages

## Context-free grammar calculator

It's my experimental project created with a purpose of understanding context-free grammars better. This calculator has custom operation priorities for the sake of interest.
The grammar for this project goes as follows:

>expr -> div {'+'|'-' div}  
>div  -> mul {'/' mul}  
>mul  -> operand {'*' operand}  
>operand  -> digit | '(' expr ')' | '-' operand  
>digit  -> '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' | {digit}

It's easy to notice that multiplication has higher priority than division (unlike the equal priority for substraction and addition). That's the only difference in this calculator's operation priority.  
This change means that the following expression would result in **1** instead of **9**.

>6/2*3

### Usage
This application reads the input from a file called _file.txt_ and prints out the result. The expression should not contain any spaces. This calculator only supports '+', '-', '/', '*', '(', ')', and digits as input for now.
