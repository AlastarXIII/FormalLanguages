expr -> div {'+'|'-' div}
div  -> mul {'/' mul}
mul  -> num {'*' num}
num  -> digit | '(' expr ')' | '-' digit
digit  -> '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' | {digit}
