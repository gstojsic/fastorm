grammar Query;

@header {
import com.skunkworks.fastorm.parser.query.Query;

import java.util.HashSet;
import java.util.HashMap;
}

@members {
private final Query queryCtx = new Query();
}

query returns [Query ctx]
   : FIND_BY expression finalExpression
   { $ctx = queryCtx; }
   ;

expression
   //: atom {System.out.println("left:" + $atom.text);} (operator atom{System.out.println("atom:" + $atom.text + ", operator:" + $operator.text);})*
   : atom (operator{queryCtx.addQueryOperator($operator.text);} atom)*
   ;

operator
    : AND
    | OR
    ;

atom
   : number {queryCtx.addQueryParam($number.text);}
   | variable {queryCtx.addQueryParam($variable.text);}
   ;

finalExpression : finalOperator variable
    {queryCtx.setOrderByParam($variable.text);}
    ;

finalOperator
   : ORDER_BY
   ;

number
   : DIGIT +
   ;

variable
   : LETTER (LETTER | DIGIT)*
   ;

FIND_BY
   : 'findBy'
   ;

AND
   : 'And'
   ;

OR
   : 'Or'
   ;

ORDER_BY
   : 'OrderBy'
   ;

LETTER
   : ('a' .. 'z') | ('A' .. 'Z')
   ;

DIGIT
   : ('0' .. '9')
   ;

//WS
//   : [ \r\n\t] + -> channel (HIDDEN)
//;