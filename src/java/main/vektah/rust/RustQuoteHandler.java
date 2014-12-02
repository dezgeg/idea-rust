package vektah.rust;

import com.intellij.codeInsight.editorActions.MultiCharQuoteHandler;
import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.Nullable;
import vektah.rust.psi.RustTokens;

public class RustQuoteHandler extends SimpleTokenSetQuoteHandler implements MultiCharQuoteHandler {
	public RustQuoteHandler() {
		super(TokenSet.create(RustTokens.STRING_LIT, RustTokens.RAW_STRING_LIT, RustTokens.CHAR_LIT));
	}

	@Override
	public boolean isOpeningQuote(HighlighterIterator iterator, int offset) {
		if (!myLiteralTokenSet.contains(iterator.getTokenType()))
			return false;

		final Document document = iterator.getDocument();
		if (document == null)
			return false;

		CharSequence chars = document.getCharsSequence();
		return findLiteralStartOffset(chars, iterator.getStart()).getFirst() == offset;
	}

	@Override
	public boolean isClosingQuote(HighlighterIterator iterator, int offset) {
		final Document document = iterator.getDocument();
		if (document == null)
			return false;

		CharSequence chars = document.getCharsSequence();
		// noinspection SimplifiableIfStatement
		if (findLiteralStartOffset(chars, iterator.getStart()).getSecond() > 0)
			return false;
		return super.isClosingQuote(iterator, offset);
	}

	@Nullable
	@Override
	public CharSequence getClosingQuote(HighlighterIterator iterator, int offset) {
		final Document document = iterator.getDocument();
		CharSequence chars = document.getCharsSequence();
		Pair<Integer, Integer> pair = findLiteralStartOffset(chars, iterator.getStart());

		// At the point getClosingQuote() is called, the '"' typed by user has already been
		// added to the document. So if a quote is added after:
		//  - 'r##'     -> then we want to return '"##'
		//  - 'r##"foo' -> then we want to return '##'
		String ret = offset - 1 == pair.getFirst() ? "\"" : "";

		// If we are adding a quote after 'foo' in r###"foo###, then don't add more hashes
		int numHashes = pair.second;
		while (numHashes > 0 && chars.charAt(offset) == '#') {
			offset++;
			numHashes--;
		}
		ret += StringUtil.repeat("#", numHashes);

		return ret;
	}

	@Override
	protected boolean isNonClosedLiteral(HighlighterIterator iterator, CharSequence chars) {
		int index = iterator.getEnd() - 1;
		Pair<Integer, Integer> pair = findLiteralStartOffset(chars, iterator.getStart());
		for (int i = 0; i < pair.getSecond(); i++) {
			if (index < iterator.getStart() || chars.charAt(index) != '#')
				return true;
			index--;
		}
		return index < iterator.getStart() || chars.charAt(index) != '"';
	}

	// @return: pair of (offset of quote, number of #'s in a raw string)
	private Pair<Integer, Integer> findLiteralStartOffset(CharSequence chars, int offset) {
		int numHashes = 0;
		if (chars.charAt(offset) == 'b')
			offset++;
		if (chars.charAt(offset) == 'r') {
			offset++;
			while (chars.charAt(offset) == '#') {
				offset++;
				numHashes++;
			}
		}
		return new Pair<Integer, Integer>(offset, numHashes);
	}
}
