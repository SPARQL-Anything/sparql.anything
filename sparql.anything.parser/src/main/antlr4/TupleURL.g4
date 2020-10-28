grammar TupleURL;

@header {
    package com.github.spiceh2020.sparql.anything.tupleurl.antlr;
}

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
	LITERAL
;

keyValue
:
	IDENTIFIER '=' LITERAL
;

/*
scheme
:
	'tuple:'
;
 */
IDENTIFIER
:
	(
		LETTER
		| DIGIT
		| '-'
	)+
;

LITERAL
:
	(
		LETTER
		| DIGIT
		| '-'
		| '/'
		| '#'
		| '@'
		| '&'
		| '.'
		| '+'
		| '%'
		| '!'
		| '?'
		| ';'
		| '$'
		| '_'
		| '~'
		| '*'
		| '\\'
		| '\''
		| ')'
		| '('
		| ':'
		| ESCAPED
	)+
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


