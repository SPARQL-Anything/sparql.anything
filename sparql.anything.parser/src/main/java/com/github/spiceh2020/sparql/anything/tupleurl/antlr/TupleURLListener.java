// Generated from TupleURL.g4 by ANTLR 4.4

    package com.github.spiceh2020.sparql.anything.tupleurl.antlr;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TupleURLParser}.
 */
public interface TupleURLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TupleURLParser#basicURL}.
	 * @param ctx the parse tree
	 */
	void enterBasicURL(@NotNull TupleURLParser.BasicURLContext ctx);
	/**
	 * Exit a parse tree produced by {@link TupleURLParser#basicURL}.
	 * @param ctx the parse tree
	 */
	void exitBasicURL(@NotNull TupleURLParser.BasicURLContext ctx);
	/**
	 * Enter a parse tree produced by {@link TupleURLParser#keyValue}.
	 * @param ctx the parse tree
	 */
	void enterKeyValue(@NotNull TupleURLParser.KeyValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link TupleURLParser#keyValue}.
	 * @param ctx the parse tree
	 */
	void exitKeyValue(@NotNull TupleURLParser.KeyValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link TupleURLParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(@NotNull TupleURLParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link TupleURLParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(@NotNull TupleURLParser.ParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link TupleURLParser#parameters}.
	 * @param ctx the parse tree
	 */
	void enterParameters(@NotNull TupleURLParser.ParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link TupleURLParser#parameters}.
	 * @param ctx the parse tree
	 */
	void exitParameters(@NotNull TupleURLParser.ParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link TupleURLParser#url}.
	 * @param ctx the parse tree
	 */
	void enterUrl(@NotNull TupleURLParser.UrlContext ctx);
	/**
	 * Exit a parse tree produced by {@link TupleURLParser#url}.
	 * @param ctx the parse tree
	 */
	void exitUrl(@NotNull TupleURLParser.UrlContext ctx);
}