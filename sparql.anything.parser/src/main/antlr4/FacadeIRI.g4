grammar FacadeIRI;

@header {
    package com.github.spiceh2020.sparql.anything.facadeiri.antlr;
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


