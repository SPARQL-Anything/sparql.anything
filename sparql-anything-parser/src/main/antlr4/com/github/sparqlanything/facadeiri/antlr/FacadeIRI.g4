grammar FacadeIRI;



basicURL
:
	parameters
;

parameters
:
	parameter
	(
		',' parameters
	)*
;

parameter
:
	url
	| keyValue
;

url
:
	LITERAL | IDENTIFIER
;

keyValue
:
	IDENTIFIER '='
	(
		LITERAL
		| IDENTIFIER
	)
;

IDENTIFIER
:
	(
		LETTER
		| DIGIT
		| '-'
		| '.'
	)+
;

LITERAL
:
	(
		NOTESCAPED
		| ESCAPED
	)+
;

NOTESCAPED
:
	~( '=' | ',' )
;

ESCAPED
:
	ESCAPE
	(
		'='
		| ','
	)
;

ESCAPE
:
	'\\'
;

LETTER
:
	'a' .. 'z'
	| 'A' .. 'Z'
;

DIGIT
:
	'0' .. '9'
;

WHITESPACE
:
	' ' -> skip
;


