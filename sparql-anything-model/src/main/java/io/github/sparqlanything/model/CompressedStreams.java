/*
 * Copyright (c) 2026 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.sparqlanything.model;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Transparent decompression of gzip and bzip2 streams, detected from the content (#710).
 * Other compressors known to commons-compress are deliberately not handled: some have weak signatures
 * (e.g. lzma, deflate) that could match the first bytes of a plain archive, others need optional libraries.
 */
public final class CompressedStreams {

	private CompressedStreams() {
	}

	public static InputStream decompressIfCompressed(InputStream in) throws IOException {
		InputStream bin = in.markSupported() ? in : new BufferedInputStream(in);
		String compressor;
		try {
			compressor = CompressorStreamFactory.detect(bin);
		} catch (CompressorException e) {
			return bin; // no compressor signature
		}
		if (!CompressorStreamFactory.GZIP.equals(compressor) && !CompressorStreamFactory.BZIP2.equals(compressor)) {
			return bin;
		}
		try {
			// decompressConcatenated = true: multi-member gzip files are common (e.g. pigz output)
			return new BufferedInputStream(new CompressorStreamFactory(true).createCompressorInputStream(compressor, bin));
		} catch (CompressorException e) {
			throw new IOException("Cannot decompress " + compressor + " stream", e);
		}
	}
}
