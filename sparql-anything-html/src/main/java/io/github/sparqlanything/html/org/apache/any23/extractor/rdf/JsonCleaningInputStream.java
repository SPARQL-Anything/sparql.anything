/*
 * Copyright (c) 2024 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.sparqlanything.html.org.apache.any23.extractor.rdf;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Arrays;

/**
 * This class uses several strategies to fix common JSON syntax errors, including:
 * <ol>
 * <li>Remove CDATA markers</li>
 * <li>Remove YAML and C-style comments</li>
 * <li>Allow single-quoted strings</li>
 * <li>Ignore duplicated commas between elements of objects and arrays</li>
 * <li>Remove trailing commas from objects and arrays</li>
 * <li>Insert omitted commas after objects and arrays</li>
 * <li>Ignore all unicode whitespace characters (assumes UTF-8 encoding)</li>
 * <li>Treat semi-colons as commas</li>
 * </ol>
 *
 * @author Hans Brende (hansbrende@apache.org)
 */
class JsonCleaningInputStream extends InputStream {

    private static final int EOL_COMMENT = 1;
    private static final int MULTILINE_COMMENT = 2;

    private static final int NEEDS_COMMA = -1;
    private static final int NEEDS_COMMA_AND_NEWLINE = 1;

    private boolean inEscape;
    private boolean inCDATA;
    private int needsComma;
    private int currentState;

    private static final int MAX_BLANK_PUSHBACK = 128;
    private static final byte[] BLANK_PUSHBACK = new byte[MAX_BLANK_PUSHBACK];

    static {
        Arrays.fill(BLANK_PUSHBACK, (byte) ' ');
        BLANK_PUSHBACK[0] = '\n';
    }

    private final PushbackInputStream in;

    JsonCleaningInputStream(InputStream in) {
        this.in = new PushbackInputStream(in, 256);
    }

    private static void unread(PushbackInputStream in, int c) throws IOException {
        if (c != -1) {
            in.unread(c);
        }
    }

    private static boolean isNextOrUnread(PushbackInputStream in, int... next) throws IOException {
        int i = -1;
        for (int test : next) {
            int c = in.read();
            if (c != test) {
                unread(in, c);
                while (i >= 0) {
                    in.unread(next[i--]);
                }
                return false;
            }
            i++;
        }
        return true;
    }

    @Override
    public int read() throws IOException {
        PushbackInputStream in = this.in;

        for (;;) {
            int c = in.read();

            if (c == -1) {
                return c;
            }

            if (inCDATA) {
                if (c == ']' && isNextOrUnread(in, ']', '>')) {
                    inCDATA = false;
                    continue;
                }
            } else {
                if (c == '<' && isNextOrUnread(in, '!', '[', 'C', 'D', 'A', 'T', 'A', '[')) {
                    inCDATA = true;
                    continue;
                }
            }

            int ctx = currentState;
            switch (ctx) {
            case 0:
                break;
            case EOL_COMMENT:
                if (c == '\r' || c == '\n') {
                    // end single-line comment
                    currentState = 0;
                    if (needsComma != 0) {
                        needsComma = NEEDS_COMMA_AND_NEWLINE;
                        continue;
                    }
                    return c;
                }
                continue;
            case MULTILINE_COMMENT:
                if (c == '\r' || c == '\n') {
                    if (needsComma != 0) {
                        needsComma = NEEDS_COMMA_AND_NEWLINE;
                        continue;
                    }
                    return c;
                } else if (c == '*' && isNextOrUnread(in, '/')) {
                    // end multiline comment
                    currentState = 0;
                }
                continue;
            default:
                // we're in a quote
                if (inEscape) {
                    // end escape
                    inEscape = false;
                } else if (c == '\\') {
                    // begin escape
                    inEscape = true;
                } else if (c == ctx) {
                    // end quote
                    currentState = 0;
                    return '"';
                }
                return c;
            }

            // we're not in a quote or comment

            $whitespace: {
                switch (c) {
                case '#':
                    currentState = EOL_COMMENT;
                    continue;
                case '/':
                    int next = in.read();
                    if (next == '/') {
                        currentState = EOL_COMMENT;
                        continue;
                    } else if (next == '*') {
                        currentState = MULTILINE_COMMENT;
                        continue;
                    }
                    unread(in, next);
                    break;
                case ',':
                case ';':
                    // don't write out comma yet!
                    needsComma = NEEDS_COMMA;
                    continue;
                case '}':
                case ']':
                    // Only thing that can follow '}' or ']' is:
                    // '}' or ']' or ',' or EOF
                    needsComma = NEEDS_COMMA;
                    return c;
                case '\r':
                case '\n':
                    if (needsComma != 0) {
                        needsComma = NEEDS_COMMA_AND_NEWLINE;
                        continue;
                    }
                    return c;
                // UTF-8 whitespace detection
                case 0x09:
                case 0x0b:
                case 0x0c:
                case 0x1c:
                case 0x1d:
                case 0x1e:
                case 0x1f:
                case 0x20:
                    break $whitespace;
                case 0xc2:
                    if (isNextOrUnread(in, 0xa0)) {
                        break $whitespace;
                    }
                    break;
                case 0xe1:
                    if (isNextOrUnread(in, 0x9a, 0x80) || isNextOrUnread(in, 0xa0, 0x8e)) {
                        break $whitespace;
                    }
                    break;
                case 0xe2:
                    int c1 = in.read();
                    if (c1 == 0x80) {
                        int c2 = in.read();
                        // space separators
                        if (c2 >= 0x80 && c2 <= 0x8a || c2 == 0xaf
                        // line and paragraph separators
                                || c2 == 0xa8 || c2 == 0xa9) {
                            break $whitespace;
                        }
                        unread(in, c2);
                        in.unread(0x80);
                    } else if (c1 == 0x81) {
                        int c2 = in.read();
                        if (c2 == 0x9f) {
                            break $whitespace;
                        }
                        unread(in, c2);
                        in.unread(0x81);
                    } else {
                        unread(in, c1);
                    }
                    break;
                case 0xe3:
                    if (isNextOrUnread(in, 0x80, 0x80)) {
                        break $whitespace;
                    }
                    break;
                default:
                    break;
                }

                // here: character is not whitespace

                int nc = needsComma;
                if (nc != 0) {
                    in.unread(c);
                    if (nc == NEEDS_COMMA) {
                        in.unread(' ');
                    } else {
                        in.unread(BLANK_PUSHBACK, 0, nc);
                    }
                    needsComma = 0;
                    return ',';
                } else if (c == '"' || c == '\'') {
                    currentState = c;
                    return '"';
                }
                return c;
            } // end $whitespace

            // here: character is whitespace

            int nc = needsComma;
            if (nc != 0) {
                if (nc != NEEDS_COMMA && nc != MAX_BLANK_PUSHBACK) {
                    needsComma = nc + 1;
                }
                continue;
            }

            return ' ';

        }

    }
}
