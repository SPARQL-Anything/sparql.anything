/**
 * Copyright 2012-2013 the Semargl contributors. See AUTHORS for more details.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.sparqlanything.html.org.semarglproject.sink;

import io.github.sparqlanything.html.org.semarglproject.rdf.ParseException;
import io.github.sparqlanything.html.org.semarglproject.sink.CharSink;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * Implementation of {@link io.github.sparqlanything.html.org.semarglproject.sink.CharSink}. Provides bridging to Java IO APIs
 * ({@link Writer}, {@link OutputStream}, {@link File}).
 */
public final class CharOutputSink implements CharSink {

    private File file;
    private Writer writer;
    private OutputStream outputStream;
    private boolean closeOnEndStream;
    private final Charset charset;

    private static final short BATCH_SIZE = 256;
    private StringBuilder buffer;
    private short bufferSize;

    /**
     * Creates class instance with default charset encoding..
     */
    public CharOutputSink() {
        this(Charset.defaultCharset());
    }

    /**
     * Creates class instance with specified charset encoding.
     * @param charset charset
     */
    public CharOutputSink(Charset charset) {
        this.charset = charset;
    }

    /**
     * Creates class instance with specified charset name.
     * @param charsetName charset name
     */
    public CharOutputSink(String charsetName) {
        this.charset = Charset.forName(charsetName);
    }

    /**
     * Redirects output to specified file
     * @param file output file
     */
    public void connect(File file) {
        this.file = file;
        this.writer = null;
        this.outputStream = null;
        this.closeOnEndStream = true;
    }

    /**
     * Redirects output to specified writer
     * @param writer output writer
     */
    public void connect(Writer writer) {
        this.file = null;
        this.writer = writer;
        this.outputStream = null;
        this.closeOnEndStream = false;
    }

    /**
     * Redirects output to specified stream
     * @param outputStream output stream
     */
    public void connect(OutputStream outputStream) {
        this.file = null;
        this.writer = null;
        this.outputStream = outputStream;
        this.closeOnEndStream = false;
    }

    @Override
    public CharOutputSink process(String str) throws ParseException {
        buffer.append(str);
        bufferSize = (short) (bufferSize + str.length());
        writeBuffer();
        return this;
    }

    @Override
    public CharOutputSink process(char ch) throws ParseException {
        buffer.append(ch);
        bufferSize++;
        writeBuffer();
        return this;
    }

    @Override
    public CharOutputSink process(char[] buffer, int start, int count) throws ParseException {
        this.buffer.append(buffer, start, count);
        bufferSize = (short) (bufferSize + count);
        writeBuffer();
        return this;
    }

    private void writeBuffer() {
        if (bufferSize >= BATCH_SIZE) {
            try {
                try {
                    writer.write(buffer.toString());
                } catch (IOException e) {
                    throw new ParseException(e);
                }
            } catch (ParseException e) {
                // do nothing
            }
            buffer = new StringBuilder(BATCH_SIZE);
            bufferSize = 0;
        }
    }

    @Override
    public void setBaseUri(String baseUri) {
    }

    @Override
    public void startStream() throws ParseException {
        buffer = new StringBuilder();
        bufferSize = 0;
        if (writer == null) {
            if (file != null) {
                try {
                    writer = new OutputStreamWriter(new FileOutputStream(file), charset);
                } catch (FileNotFoundException e) {
                    throw new ParseException(e);
                }
            } else if (outputStream != null) {
                writer = new OutputStreamWriter(outputStream, charset);
            }
        }
    }

    @Override
    public void endStream() throws ParseException {
        buffer.append("\n");
        bufferSize = BATCH_SIZE;
        writeBuffer();
        try {
            writer.flush();
        } catch (IOException e) {
            throw new ParseException(e);
        }
        if (closeOnEndStream) {
            if (writer != null) {
                closeQuietly(writer);
                writer = null;
            } else if (outputStream != null) {
                closeQuietly(outputStream);
                outputStream = null;
            }
        }
    }

    @Override
    public boolean setProperty(String key, Object value) {
        return false;
    }

    private static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }
}
