grammar TestGrammar;

query : FIND_BY expression finalExpression?;

expression : variable (operator variable)*;

operator : 'And' | 'Or';

//atom : number | variable;

finalExpression : orderByExpression;

orderByExpression : ORDER_BY variable;

//number : DIGIT+;

variable : UPPERCASE_LETTER (LETTER | DIGIT)* |
           'And' (LETTER | DIGIT)* ;

fragment UPPERCASE : [A-Z];

fragment LOWERCASE : [a-z];

FIND_BY : 'findBy';

//AND : 'And';

ORDER_BY : 'OrderBy';

UPPERCASE_LETTER : UPPERCASE;

LETTER : UPPERCASE | LOWERCASE;

DIGIT : ('0' .. '9');