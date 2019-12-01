grammar FOOL;

@lexer::members {
   //there is a much better way to do this, check the ANTLR guide
   //I will leave it like this for now just becasue it is quick
   //but it doesn't work well
   public int lexicalErrors=0;
}

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/
  
prog   : (classdec SEMIC)+ (let)? (exp|stms) SEMIC			#classExp
       | let (exp | stms) SEMIC             				#letInExp
       |  exp SEMIC                          				#singleExp
       ;
       
       
let   : LET (dec SEMIC)+ IN ;
       
dec   : varasm           #varAssignment
      | fun              #funDeclaration
      ;
      
varasm     : vardec ASM exp ;

fun    : type ID LPAR ( vardec ( COMMA vardec)* )? RPAR (let)? (exp | stms) ;

vardec  : type ID ;

stms	: (stm)+
		;

stm 	: IF exp THEN CLPAR thenBranch=stms CRPAR (ELSE CLPAR elseBranch=stms CRPAR)? 		#ifStm
		| ID ASM exp (SEMIC)?																#asm
		;	
		
type   : INT  
       | BOOL
       | ID
      ; 
      
classdec	: CLASS ID ( EXT ID )? LPAR( vardec ( COMMA vardec)* )?RPAR  (CLPAR (fun SEMIC)* CRPAR)?  
		;	 
    
exp    	: (NOT)? (MINUS)? left=term ((PLUS | MINUS) right=exp)?
      	;
   
term   	: left=factor ((TIMES | DIV) right=term)?
      	;
   
factor 	: left=value ((EQ|LEQ|GEQ|OR|AND) right=value)?
		;  
   
value  :  INTEGER                           #intVal
      | ( TRUE | FALSE )                   #boolVal
      | LPAR exp RPAR                      #baseExp
      | IF cond=exp THEN CLPAR thenBranch=exp CRPAR ELSE CLPAR elseBranch=exp CRPAR  #ifExp /* Mettendo l'else facoltativo ha un ramo che non produce un risultato */
      | ID                                                                           #varExp
      | ID ( LPAR (exp (COMMA exp)* )? RPAR )                                        #funExp
      | (ID | THIS) POINT ID ( LPAR (exp (COMMA exp)* )? RPAR )	  					 #methodExp        
      | NEW ID LPAR(exp (COMMA exp)*)?RPAR		                                 	 #new      
      | THIS										                                 #this
      ; 
				

   

   
/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/
SEMIC  : ';' ;
COLON  : ':' ;
COMMA  : ',' ;
EQ     : '==' ;
LEQ  : '<=' ;
GEQ  : '>=' ;
TRUE   : 'true' ;
FALSE  : 'false' ;
OR	   : '||';	
AND    : '&&';
NOT    : 'not' ;
ASM    : '=' ;
PLUS   : '+' ;
MINUS  : '-' ;
TIMES  : '*' ;
DIV    : '/' ;
LPAR   : '(' ;
RPAR   : ')' ;
CLPAR  : '{' ;
CRPAR  : '}' ;
IF        : 'if' ;
THEN   : 'then' ;
ELSE   : 'else' ;
//PRINT : 'print' ; 
LET    : 'let' ;
IN     : 'in' ;
//VAR    : 'var' ;
CLASS   : 'class' ;
EXT   : 'extends' ;
NEW    : 'new' ;
THIS   : 'this' ;
POINT    : '.' ;
FUN    : 'fun' ;
INT    : 'int' ;
BOOL   : 'bool' ;




//Numbers
fragment DIGIT : '0'..'9';    
INTEGER       : DIGIT+;

//IDs
fragment CHAR  : 'a'..'z' |'A'..'Z' ;
ID              : CHAR (CHAR | DIGIT)* ;

//ESCAPED SEQUENCES
WS              : (' '|'\t'|'\n'|'\r')-> skip;
LINECOMENTS    : '//' (~('\n'|'\r'))* -> skip;
BLOCKCOMENTS    : '/*'( ~('/'|'*')|'/'~'*'|'*'~'/'|BLOCKCOMENTS)* '*/' -> skip;



 //VERY SIMPLISTIC ERROR CHECK FOR THE LEXING PROCESS, THE OUTPUT GOES DIRECTLY TO THE TERMINAL
 //THIS IS WRONG!!!!
ERR     : . { System.out.println("Invalid char: "+ getText()); lexicalErrors++; } -> channel(HIDDEN); 
