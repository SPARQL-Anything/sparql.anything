/*
 * Copyright (c) 2021 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.sparqlanything.markdown;

import org.apache.commons.lang3.StringUtils;
import org.commonmark.internal.DocumentBlockParser;
import org.commonmark.node.Block;
import org.commonmark.parser.block.AbstractBlockParser;
import org.commonmark.parser.block.AbstractBlockParserFactory;
import org.commonmark.parser.block.BlockContinue;
import org.commonmark.parser.block.BlockParser;
import org.commonmark.parser.block.BlockStart;
import org.commonmark.parser.block.MatchedBlockParser;
import org.commonmark.parser.block.ParserState;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Copied and modified from https://github.com/commonmark/commonmark-java/blob/main/commonmark-ext-yaml-front-matter/src/main/java/org/commonmark/ext/front/matter/internal/YamlFrontMatterBlockParser.java
 */
public class YamlFrontMatterGatherer extends AbstractBlockParser {
	private static final Pattern REGEX_BEGIN = Pattern.compile("^-{3}(\\s.*)?");
	private static final Pattern REGEX_END = Pattern.compile("^(-{3}|\\.{3})(\\s.*)?");
	private YamlFrontMatter block;
	private List<CharSequence> lines;

	public YamlFrontMatterGatherer(){
		block = new YamlFrontMatter();
		lines = new ArrayList<CharSequence>();
	}

	@Override
	public Block getBlock() {
		return block;
	}

	@Override
	public BlockContinue tryContinue(ParserState parserState) {
		final CharSequence line = parserState.getLine().getContent();

		if (REGEX_END.matcher(line).matches()) {
			// Stop loading lines
			block.setContent(StringUtils.joinWith("\n", lines.toArray()));
			return BlockContinue.finished();
		}
		lines.add(line);
		return BlockContinue.atIndex(parserState.getIndex());
	}

	public static class Factory extends AbstractBlockParserFactory {
		@Override
		public BlockStart tryStart(ParserState state, MatchedBlockParser matchedBlockParser) {
			CharSequence line = state.getLine().getContent();
			BlockParser parentParser = matchedBlockParser.getMatchedBlockParser();
			// check whether this line is the first line of whole document or not
			if (parentParser instanceof DocumentBlockParser && parentParser.getBlock().getFirstChild() == null &&
					REGEX_BEGIN.matcher(line).matches()) {
				return BlockStart.of(new YamlFrontMatterGatherer()).atIndex(state.getNextNonSpaceIndex());
			}

			return BlockStart.none();
		}
	}
}
