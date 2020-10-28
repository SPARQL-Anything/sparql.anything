// Generated from TupleURL.g4 by ANTLR 4.4

    package com.github.spiceh2020.sparql.anything.tupleurl.antlr;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class TupleURLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__1=1, T__0=2, IDENTIFIER=3, LITERAL=4, ESCAPED=5, ESCAPE=6, LETTER=7, 
		DIGIT=8, WHITESPACE=9;
	public static final String[] tokenNames = {
		"<INVALID>", "','", "'='", "IDENTIFIER", "LITERAL", "ESCAPED", "'\\'", 
		"LETTER", "DIGIT", "' '"
	};
	public static final int
		RULE_basicURL = 0, RULE_parameters = 1, RULE_parameter = 2, RULE_url = 3, 
		RULE_keyValue = 4;
	public static final String[] ruleNames = {
		"basicURL", "parameters", "parameter", "url", "keyValue"
	};

	@Override
	public String getGrammarFileName() { return "TupleURL.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public TupleURLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class BasicURLContext extends ParserRuleContext {
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public BasicURLContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_basicURL; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TupleURLListener ) ((TupleURLListener)listener).enterBasicURL(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TupleURLListener ) ((TupleURLListener)listener).exitBasicURL(this);
		}
	}

	public final BasicURLContext basicURL() throws RecognitionException {
		BasicURLContext _localctx = new BasicURLContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_basicURL);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(10); parameters();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParametersContext extends ParserRuleContext {
		public ParametersContext parameters(int i) {
			return getRuleContext(ParametersContext.class,i);
		}
		public ParameterContext parameter() {
			return getRuleContext(ParameterContext.class,0);
		}
		public List<ParametersContext> parameters() {
			return getRuleContexts(ParametersContext.class);
		}
		public ParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TupleURLListener ) ((TupleURLListener)listener).enterParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TupleURLListener ) ((TupleURLListener)listener).exitParameters(this);
		}
	}

	public final ParametersContext parameters() throws RecognitionException {
		ParametersContext _localctx = new ParametersContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_parameters);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(12); parameter();
			setState(17);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(13); match(T__1);
					setState(14); parameters();
					}
					} 
				}
				setState(19);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParameterContext extends ParserRuleContext {
		public UrlContext url() {
			return getRuleContext(UrlContext.class,0);
		}
		public KeyValueContext keyValue() {
			return getRuleContext(KeyValueContext.class,0);
		}
		public ParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TupleURLListener ) ((TupleURLListener)listener).enterParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TupleURLListener ) ((TupleURLListener)listener).exitParameter(this);
		}
	}

	public final ParameterContext parameter() throws RecognitionException {
		ParameterContext _localctx = new ParameterContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_parameter);
		try {
			setState(22);
			switch (_input.LA(1)) {
			case LITERAL:
				enterOuterAlt(_localctx, 1);
				{
				setState(20); url();
				}
				break;
			case IDENTIFIER:
				enterOuterAlt(_localctx, 2);
				{
				setState(21); keyValue();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UrlContext extends ParserRuleContext {
		public TerminalNode LITERAL() { return getToken(TupleURLParser.LITERAL, 0); }
		public UrlContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_url; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TupleURLListener ) ((TupleURLListener)listener).enterUrl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TupleURLListener ) ((TupleURLListener)listener).exitUrl(this);
		}
	}

	public final UrlContext url() throws RecognitionException {
		UrlContext _localctx = new UrlContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_url);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(24); match(LITERAL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KeyValueContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(TupleURLParser.IDENTIFIER, 0); }
		public TerminalNode LITERAL() { return getToken(TupleURLParser.LITERAL, 0); }
		public KeyValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_keyValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TupleURLListener ) ((TupleURLListener)listener).enterKeyValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TupleURLListener ) ((TupleURLListener)listener).exitKeyValue(this);
		}
	}

	public final KeyValueContext keyValue() throws RecognitionException {
		KeyValueContext _localctx = new KeyValueContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_keyValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(26); match(IDENTIFIER);
			setState(27); match(T__0);
			setState(28); match(LITERAL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\13!\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\3\2\3\2\3\3\3\3\3\3\7\3\22\n\3\f\3\16\3\25"+
		"\13\3\3\4\3\4\5\4\31\n\4\3\5\3\5\3\6\3\6\3\6\3\6\3\6\2\2\7\2\4\6\b\n\2"+
		"\2\35\2\f\3\2\2\2\4\16\3\2\2\2\6\30\3\2\2\2\b\32\3\2\2\2\n\34\3\2\2\2"+
		"\f\r\5\4\3\2\r\3\3\2\2\2\16\23\5\6\4\2\17\20\7\3\2\2\20\22\5\4\3\2\21"+
		"\17\3\2\2\2\22\25\3\2\2\2\23\21\3\2\2\2\23\24\3\2\2\2\24\5\3\2\2\2\25"+
		"\23\3\2\2\2\26\31\5\b\5\2\27\31\5\n\6\2\30\26\3\2\2\2\30\27\3\2\2\2\31"+
		"\7\3\2\2\2\32\33\7\6\2\2\33\t\3\2\2\2\34\35\7\5\2\2\35\36\7\4\2\2\36\37"+
		"\7\6\2\2\37\13\3\2\2\2\4\23\30";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}