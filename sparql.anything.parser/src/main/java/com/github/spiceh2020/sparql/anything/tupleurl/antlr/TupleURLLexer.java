// Generated from TupleURL.g4 by ANTLR 4.4

    package com.github.spiceh2020.sparql.anything.tupleurl.antlr;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class TupleURLLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__1=1, T__0=2, IDENTIFIER=3, LITERAL=4, ESCAPED=5, ESCAPE=6, LETTER=7, 
		DIGIT=8, WHITESPACE=9;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"'\\u0000'", "'\\u0001'", "'\\u0002'", "'\\u0003'", "'\\u0004'", "'\\u0005'", 
		"'\\u0006'", "'\\u0007'", "'\b'", "'\t'"
	};
	public static final String[] ruleNames = {
		"T__1", "T__0", "IDENTIFIER", "LITERAL", "ESCAPED", "ESCAPE", "LETTER", 
		"DIGIT", "WHITESPACE"
	};


	public TupleURLLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "TupleURL.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\13\65\b\1\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\3"+
		"\2\3\3\3\3\3\4\3\4\3\4\6\4\35\n\4\r\4\16\4\36\3\5\3\5\3\5\3\5\6\5%\n\5"+
		"\r\5\16\5&\3\6\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\n\2\2\13"+
		"\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\3\2\5\n\2##%-/\61<=AB^^aa\u0080"+
		"\u0080\4\2..??\4\2C\\c|;\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2"+
		"\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\3\25"+
		"\3\2\2\2\5\27\3\2\2\2\7\34\3\2\2\2\t$\3\2\2\2\13(\3\2\2\2\r+\3\2\2\2\17"+
		"-\3\2\2\2\21/\3\2\2\2\23\61\3\2\2\2\25\26\7.\2\2\26\4\3\2\2\2\27\30\7"+
		"?\2\2\30\6\3\2\2\2\31\35\5\17\b\2\32\35\5\21\t\2\33\35\7/\2\2\34\31\3"+
		"\2\2\2\34\32\3\2\2\2\34\33\3\2\2\2\35\36\3\2\2\2\36\34\3\2\2\2\36\37\3"+
		"\2\2\2\37\b\3\2\2\2 %\5\17\b\2!%\5\21\t\2\"%\t\2\2\2#%\5\13\6\2$ \3\2"+
		"\2\2$!\3\2\2\2$\"\3\2\2\2$#\3\2\2\2%&\3\2\2\2&$\3\2\2\2&\'\3\2\2\2\'\n"+
		"\3\2\2\2()\5\r\7\2)*\t\3\2\2*\f\3\2\2\2+,\7^\2\2,\16\3\2\2\2-.\t\4\2\2"+
		".\20\3\2\2\2/\60\4\62;\2\60\22\3\2\2\2\61\62\7\"\2\2\62\63\3\2\2\2\63"+
		"\64\b\n\2\2\64\24\3\2\2\2\7\2\34\36$&\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}