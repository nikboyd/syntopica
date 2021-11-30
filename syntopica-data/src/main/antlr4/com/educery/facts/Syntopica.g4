
grammar Syntopica;

options { superClass = ParserGlue; }
@header {
import com.educery.cogs.*;
import static com.educery.utils.Utils.*;
}

unit      : d=domain ( slist+=statement )* ;
domain    : DOMAIN Named term=namedTerm Period {buildDomain($ctx);} ;
statement : 
( b=breed       {takeBreed($ctx.b);}
| a=alias 
| f=fact        {takeFact($ctx.f);} 
) Period 
;

alias 
: a=namedTerm   Assign 
( b=block       {takeBlock($ctx);}
| v=values      {takeValues($ctx);}
| r=record      {takeRecord($ctx);}
| f=fact        {takeFact($ctx);}
| n=namedTerm   {takeAlike($ctx);}
) 
;

enlist   : op=Semi | op=Comma ;
termList : terms+=namedTerm ( ops+=enlist terms+=namedTerm )* ;
fact  : n=namedTerm verb=KeywordHead ( preps+=preposition )? args+=termList ( preps+=preposition args+=termList )* ;
breed : n=namedTerm Extends b=namedTerm ;

block     : BlockInit sign=blockSign  body=send BlockExit ;
blockSign : ( ks=keySelect | ss=simpleTerm ) Bar ;
keySelect : keys+=KeywordHead args+=simpleTerm ( keys+=KeywordTail args+=simpleTerm )* ;

closure    : BlockInit sign=closedSign body=send BlockExit ;
closedSign : ( ( keys+=KeywordTail args+=simpleTerm )* Bar )? ;

nested                                  : TermInit s=send TermExit    ;
send    returns [BasicTerm term = null] : f=formula ( m=message )? {takeSend($ctx);} ;
formula returns [BasicTerm term = null] : p=basic   ( b=binary )?  {takeBinary($ctx);} ;
basic   returns [BasicTerm term = null] : p=primary ( s=unary )?   {takePrimary($ctx);} | s=unary {takeUnary($ctx);} ;
primary returns [BasicTerm term = null] :
( n=nested      {takeNest($ctx);} 
| c=closure     {takeClosure($ctx);} 
| v=value       {takeValue($ctx);} 
) 
;

unary   : selector=namedTerm ;
binary  : op=BinaryOperator b=basic ;
message : ( verb=KeywordHead | prep=preposition ) f=formula ;

record : NoteInit s=simpleList NoteExit ;
values returns [List<String> list = emptyList()] :
TermInit 
( v=valueList   {takeValues($ctx);} 
| s=simpleList  {takeTerms($ctx);} ) 
TermExit 
;

namedTerm returns [List<String> names = emptyList()] :
( p=properTerm  {takeProper($ctx);} 
| s=simpleTerm  {takeSimple($ctx);} ) 
;

simpleList : terms+=simpleTerm  ( Comma terms+=simpleTerm )* ;
valueList  : terms+=value       ( Comma terms+=value )* ;

value      : n=ConstantDecimal ;
simpleTerm : ( names+=SimpleName )+ ;
properTerm : ( names+=ProperName )+ ( names+=SimpleName )* ;

//==================================================================================================
// prepositions
//==================================================================================================

preposition :
  p=About | p=Above | p=Across | p=After | p=Against | p=Along | p=Amid | p=Among | p=Around | p=As | p=At | p=Atop |
  p=Before | p=Behind | p=Below | p=Beneath | p=Beside | p=Between | p=Beyond | p=By | p=During | p=Except | p=Excluding | 
  p=Following | p=For | p=From | p=In | p=Including | p=Inside | p=Into | p=Of | p=Off | p=On | p=Onto | p=Outside | p=Over | 
  p=Past | p=Per| p=Since | p=Through | p=Throughout | p=To | p=Toward | p=Under | p=Underneath | p=Until | p=Upon | 
  p=With | p=Within | p=Without ;

About   : 'about:' ;
Above   : 'above:' ;
Across  : 'across:' ;
After   : 'after:' ;
Against : 'against:' ;
Along   : 'along:' ;
Amid    : 'amid:' ;
Among   : 'among:' ;
Around  : 'around:' ;
As      : 'as:'   ;
At      : 'at:' ;
Atop    : 'atop:' ;
Before  : 'before:' ;
Behind  : 'behind:' ;
Below   : 'below:' ;
Beneath : 'beneath:' ;
Beside  : 'beside:' ;
Between : 'between:' ;
Beyond  : 'beyond:' ;
By      : 'by:'   ;
During  : 'during:' ;
Except  : 'except:' ;
Excluding : 'excluding:' ;
Following : 'following:' ;
For     : 'for:'  ;
From    : 'from:' ;
In      : 'in:'   ;
Including : 'including:' ;
Inside  : 'inside:' ;
Into    : 'into:' ;
Of      : 'of:'   ;
Off     : 'off:' ;
On      : 'on:'   ;
Onto    : 'onto:' ;
Outside : 'outside:' ;
Over    : 'over:' ;
Past    : 'past:' ;
Per     : 'per:' ; // per == for each
Since   : 'since:' ;
Through : 'through:' ;
Throughout : 'throughout:' ;
To      : 'to:' ;
Toward  : 'toward:' ;
Under   : 'under:' ;
Underneath : 'underneath:' ;
Until   : 'until:' ;
Upon    : 'upon:' ;
With    : 'with:' ;
Within  : 'within:' ;
Without : 'without:' ;

//==================================================================================================
// keywords + identifiers
//==================================================================================================

DOMAIN      : 'Domain' ;
Named       : 'named:' ;

KeywordHead : Name Colon ;
KeywordTail : Colon ;

ProperName  : UpperCase Tail* ;
SimpleName  : LowerCase Tail* ;

fragment Colon  : ':' ;
fragment Name   : Letter Tail* ;
fragment Tail   : Letter | DecimalDigit ;
fragment Letter : UpperCase | LowerCase ;

//==================================================================================================
// scopes
//==================================================================================================

BlockInit : '[' ;
BlockExit : ']' ;

TermInit  : '(' ;
TermExit  : ')' ;

NoteInit  : '{' ;
NoteExit  : '}' ;

//==================================================================================================
// punctuators
//==================================================================================================

Assign  : Colon Equal ;
Extends : Minus More ;
Usage   : Less Minus ;
Etc     : '...' ;

Exit    : '^' ;
Semi    : ';' ;
Bang    : '!' ;
Quest   : '?' ;
Pound   : '#' ;
Comma   : ',' ;
Bar     : '|' ;
Dollar  : '$' ;

//==================================================================================================
// strings
//==================================================================================================

CodeComment       : QuotedComment -> channel(HIDDEN) ;
ConstantString    : QuotedString ConstantString? ;

fragment QuotedString  : SingleQuote .*? SingleQuote ;
fragment QuotedComment : DoubleQuote .*? DoubleQuote ;

fragment DoubleQuote : '"' ;
fragment SingleQuote : '\'' ;

//==================================================================================================
// operators
//==================================================================================================

BinaryOperator : ComparisonOperator | MathOperator | LogicalOperator | ShiftOperator ;
fragment ComparisonOperator : More | Less | Equal | More Equal | Less Equal | Not Equal | Equal Equal | Not Not ;
fragment LogicalOperator    : And | Or | Less Less | More More ;
fragment MathOperator       : Times |  Divide |  Plus | Minus ;
fragment ShiftOperator      : More More | Less Less | Less Less Equal ;

//==================================================================================================
// operators
//==================================================================================================

fragment Percent  : '%' ;

fragment Divide   : '/' ;
fragment Times    : '*' ;
fragment Plus     : '+' ;
fragment Minus    : '-' ;

fragment Equal    : '=' ;
fragment More     : '>' ;
fragment Less     : '<' ;
fragment Not      : '~' ;

fragment And      : '&' ;
fragment Or       : '|' ;

//==================================================================================================
// literal numbers
//==================================================================================================

ConstantDecimal : CardinalNumber ( Dot CardinalFraction )? ;
ConstantInteger : CardinalNumber ;

Period : Dot ;
fragment Dot : '.' ;

fragment OrdinaryNumber   : OrdinalDigit DecimalDigit* ;
fragment OrdinaryFraction : DecimalDigit* OrdinalDigit ;
fragment CardinalNumber   : Zero | OrdinaryNumber ;
fragment CardinalFraction : Zero | OrdinaryFraction ;

//==================================================================================================
// letters + digits
//==================================================================================================

fragment UpperCase : [A-Z] ;
fragment LowerCase : [a-z] ;

fragment DecimalDigit : [0-9] ;
fragment OrdinalDigit : [1-9] ;
fragment Zero         : '0' ;

//==================================================================================================
// white space
//==================================================================================================

WhiteSpaces : WhiteSpace+ -> skip ;
fragment WhiteSpace : [ \t\r\n\f] ;

//==================================================================================================
