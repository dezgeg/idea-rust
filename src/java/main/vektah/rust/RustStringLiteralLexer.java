/*
 * Based on com.jetbrains.python.lexer.PyStringLiteralLexer:
 *
 * Copyright 2000-2014 JetBrains s.r.o.
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
package vektah.rust;

import com.intellij.lexer.LexerBase;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.StringEscapesTokenTypes;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class RustStringLiteralLexer extends LexerBase {
	private static final Logger LOG = Logger.getInstance("#vektah.rust.RustStringLiteralLexer");

	private static final short BEFORE_FIRST_QUOTE = 0; // the initial state; may last during 'u' and 'r' prefixes.
	private static final short AFTER_FIRST_QUOTE = 1;
	private static final short AFTER_LAST_QUOTE = 2;

	private CharSequence myBuffer;
	private int myStart;
	private int myEnd;
	private int myState;
	private int myLastState;
	private int myBufferEnd;
	private char myQuoteChar;

	private boolean myIsByte;
	private boolean myIsRaw;
	private final IElementType myOriginalLiteralToken;

	/**
	 * @param originalLiteralToken the AST node we're layering over.
	 */
	public RustStringLiteralLexer(final IElementType originalLiteralToken) {
		myOriginalLiteralToken = originalLiteralToken;
	}

	public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
		myBuffer = buffer;
		myStart = startOffset;
		myState = initialState;
		myLastState = initialState;
		myBufferEnd = endOffset;

		// the following could be parsing steps if we wanted this info as tokens
		int i = myStart;

		if (buffer.charAt(i) == 'b') {
			i++;
			myIsByte = true;
		}
		if (buffer.charAt(i) == 'r') {
			i++;
			myIsRaw = true;
		}

		// which quote char?
		char c = buffer.charAt(i);
		assert (c == '"') || (c == '\'') : "String must be quoted by single or double quote. Found '" + c + "' in string " + buffer;
		myQuoteChar = c;

		// calculate myEnd at last
		myEnd = locateToken(myStart);
	}

	public int getState() {
		return myLastState;
	}

	public IElementType getTokenType() {
		if (myStart >= myEnd) return null;

		// skip non-escapes immediately
		if (myIsRaw || myBuffer.charAt(myStart) != '\\') {
			return myOriginalLiteralToken;
		}

		// from here on, only escapes
		if (myStart + 1 >= myEnd)
			return StringEscapesTokenTypes.INVALID_CHARACTER_ESCAPE_TOKEN; // escape ends too early
		char nextChar = myBuffer.charAt(myStart + 1);
		if (nextChar == '\n') {
			return StringEscapesTokenTypes.VALID_STRING_ESCAPE_TOKEN; // escaped EOL
		}
		if (nextChar == 'u' || nextChar == 'U') {
			final int width = nextChar == 'u' ? 4 : 8; // is it uNNNN or Unnnnnnnn
			for (int i = myStart + 2; i < myStart + width + 2; i++) {
				if (i >= myEnd || !StringUtil.isHexDigit(myBuffer.charAt(i)))
					return StringEscapesTokenTypes.INVALID_UNICODE_ESCAPE_TOKEN;
			}
			return StringEscapesTokenTypes.VALID_STRING_ESCAPE_TOKEN;
		}

		if (nextChar == 'x') {
			for (int i = myStart + 2; i < myStart + 4; i++) {
				if (i >= myEnd || !StringUtil.isHexDigit(myBuffer.charAt(i)))
					return StringEscapesTokenTypes.INVALID_UNICODE_ESCAPE_TOKEN;
			}
			return StringEscapesTokenTypes.VALID_STRING_ESCAPE_TOKEN;
		}

		switch (nextChar) {
			case 'n':
			case 'r':
			case 't':
			case '\'':
			case '\"':
			case '\\':
			case '0':
				return StringEscapesTokenTypes.VALID_STRING_ESCAPE_TOKEN;
		}

		return StringEscapesTokenTypes.INVALID_CHARACTER_ESCAPE_TOKEN;
	}

	public int getTokenStart() {
		assert myStart < myEnd || (myStart == myEnd && myEnd == myBufferEnd);
		return myStart;
	}

	public int getTokenEnd() {
		if (!(myStart < myEnd || (myStart == myEnd && myEnd == myBufferEnd))) {
			LOG.error("myStart=" + myStart + " myEnd=" + myEnd + " myBufferEnd=" + myBufferEnd + " text=" + myBuffer.subSequence(myStart, myBufferEnd));
		}
		return myEnd;
	}

	private int locateToken(int start) {
		if (start == myBufferEnd) {
			myState = AFTER_LAST_QUOTE;
		}
		if (myState == AFTER_LAST_QUOTE) return start; // exhausted
		int i = start;
		if (myBuffer.charAt(i) == '\\') {
			LOG.assertTrue(myState == AFTER_FIRST_QUOTE);
			i++;
			if (myIsRaw) return i;
			if (i == myBufferEnd) {
				myState = AFTER_LAST_QUOTE;
				return i;
			}

			// \xNN byte escape
			if (myBuffer.charAt(i) == 'x') {
				i++;
				for (; i < start + 4; i++) {
					if (i == myBufferEnd || myBuffer.charAt(i) == '\n' || myBuffer.charAt(i) == myQuoteChar) {
						return i;
					}
				}
				return i;
			}
			return i + 1;
		} else { // not a \something
			//LOG.assertTrue(myState == AFTER_FIRST_QUOTE || myBuffer.charAt(i) == myQuoteChar);
			while (i < myBufferEnd) { // scan to next \something
				if (myBuffer.charAt(i) == '\\' && !myIsRaw) {
					return i;
				}
				if (myState == BEFORE_FIRST_QUOTE && myBuffer.charAt(i) == myQuoteChar) {
					myState = AFTER_FIRST_QUOTE;
				} else if (myState == AFTER_FIRST_QUOTE && myBuffer.charAt(i) == myQuoteChar && (!myIsRaw || myBuffer.charAt(i - 1) != '\\')) { // done?
					myState = AFTER_LAST_QUOTE;
					return i + 1; // skip the last remaining quote
				}
				i++;
			}
		}

		return i;
	}

	public void advance() {
		myLastState = myState;
		myStart = myEnd;
		myEnd = locateToken(myStart);
		if (!(myStart < myEnd || (myStart == myEnd && myEnd == myBufferEnd))) {
			LOG.warn("Inconsistent: start " + myStart + ", end " + myEnd + ", buf end " + myBufferEnd);
		}
		//assert myStart < myEnd || (myStart == myEnd && myEnd == myBufferEnd) : "Inconsistent: start " + myStart + ", end " + myEnd + ", buf end " + myBufferEnd;
	}

	@NotNull
	public CharSequence getBufferSequence() {
		return myBuffer;
	}

	public int getBufferEnd() {
		return myBufferEnd;
	}
}
