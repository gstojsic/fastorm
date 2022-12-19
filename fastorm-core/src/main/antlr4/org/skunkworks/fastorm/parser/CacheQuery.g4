grammar CacheQuery;

query : FIND_BY expression finalExpression?;

expression : atom (operator atom)*;

operator : AND | OR;

atom : number | variable;

finalExpression : finalOperator variable;

finalOperator : ORDER_BY;

number : DIGIT+;

variable : LETTER (LETTER | DIGIT)*;

fragment UPPERCASE : [A-Z];

fragment LOWERCASE : [a-z];

FIND_BY : 'findBy';

AND : 'And';

OR : 'Or';

ORDER_BY : 'OrderBy';

LETTER : ('a' .. 'z') | ('A' .. 'Z');

DIGIT : ('0' .. '9');