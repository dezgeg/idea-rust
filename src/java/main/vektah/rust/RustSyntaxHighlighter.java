package vektah.rust;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.LayeredLexer;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.StringEscapesTokenTypes;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import vektah.rust.psi.RustTokens;

import java.io.Reader;

import static vektah.rust.psi.RustTokens.*;

public class RustSyntaxHighlighter extends SyntaxHighlighterBase {
	public static final TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey("KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
	public static final TextAttributesKey IDENTIFIER = TextAttributesKey.createTextAttributesKey("IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);
	public static final TextAttributesKey BLOCK_COMMENT = TextAttributesKey.createTextAttributesKey("BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);
	public static final TextAttributesKey LINE_COMMENT = TextAttributesKey.createTextAttributesKey("LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
	public static final TextAttributesKey DOC_COMMENT = TextAttributesKey.createTextAttributesKey("DOC_COMMENT", DefaultLanguageHighlighterColors.DOC_COMMENT);
	public static final TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey("STRING", DefaultLanguageHighlighterColors.STRING);
	public static final TextAttributesKey NUMBER = TextAttributesKey.createTextAttributesKey("NUMBER", DefaultLanguageHighlighterColors.NUMBER);
	public static final TextAttributesKey BRACKETS = TextAttributesKey.createTextAttributesKey("BRACKETS", DefaultLanguageHighlighterColors.BRACKETS);
	public static final TextAttributesKey BRACES = TextAttributesKey.createTextAttributesKey("BRACES", DefaultLanguageHighlighterColors.BRACES);
	public static final TextAttributesKey PARENTHESES = TextAttributesKey.createTextAttributesKey("PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES);
	public static final TextAttributesKey COMMA = TextAttributesKey.createTextAttributesKey("COMMA", DefaultLanguageHighlighterColors.COMMA);
	public static final TextAttributesKey SYMBOL = TextAttributesKey.createTextAttributesKey("SYMBOL", DefaultLanguageHighlighterColors.PREDEFINED_SYMBOL);
	public static final TextAttributesKey SEMICOLON = TextAttributesKey.createTextAttributesKey("SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON);
	public static final TextAttributesKey OPERATOR = TextAttributesKey.createTextAttributesKey("OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
	public static final TextAttributesKey PATH_SEPARATOR = TextAttributesKey.createTextAttributesKey("PATH_SEPARATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
	public static final TextAttributesKey VALID_STRING_ESCAPE = TextAttributesKey.createTextAttributesKey("VALID_STRING_ESCAPE", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE);
	public static final TextAttributesKey INVALID_STRING_ESCAPE = TextAttributesKey.createTextAttributesKey("INVALID_STRING_ESCAPE", DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);
	public static final TextAttributesKey ATTRIBUTE = TextAttributesKey.createTextAttributesKey("ATTRIBUTE", DefaultLanguageHighlighterColors.METADATA);

	public static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};
	public static final TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{IDENTIFIER};
	public static final TextAttributesKey[] BLOCK_COMMENT_KEYS = new TextAttributesKey[]{BLOCK_COMMENT};
	public static final TextAttributesKey[] LINE_COMMENT_KEYS = new TextAttributesKey[]{LINE_COMMENT};
	public static final TextAttributesKey[] DOC_COMMENT_KEYS = new TextAttributesKey[]{DOC_COMMENT};
	public static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
	public static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
	public static final TextAttributesKey[] BRACKET_KEYS = new TextAttributesKey[]{BRACKETS};
	public static final TextAttributesKey[] BRACE_KEYS = new TextAttributesKey[]{BRACES};
	public static final TextAttributesKey[] PARENTHESES_KEYS = new TextAttributesKey[]{PARENTHESES};
	public static final TextAttributesKey[] COMMA_KEYS = new TextAttributesKey[]{COMMA};
	public static final TextAttributesKey[] SYMBOL_KEYS = new TextAttributesKey[]{SYMBOL};
	public static final TextAttributesKey[] SEMICOLON_KEYS = new TextAttributesKey[]{SEMICOLON};
	public static final TextAttributesKey[] OPERATOR_KEYS = new TextAttributesKey[]{OPERATOR};
	public static final TextAttributesKey[] PATH_SEPARATOR_KEYS = new TextAttributesKey[]{PATH_SEPARATOR};
	public static final TextAttributesKey[] VALID_STRING_ESCAPE_KEYS = new TextAttributesKey[]{VALID_STRING_ESCAPE};
	public static final TextAttributesKey[] INVALID_STRING_ESCAPE_KEYS = new TextAttributesKey[]{INVALID_STRING_ESCAPE};
	public static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

	@NotNull
	@Override
	public Lexer getHighlightingLexer() {
		LayeredLexer ret = new LayeredLexer(new FlexAdapter(new RustLexer((Reader) null)));
		ret.registerSelfStoppingLayer(new RustStringLiteralLexer(RustTokens.STRING_LIT),
				new IElementType[] { RustTokens.STRING_LIT }, IElementType.EMPTY_ARRAY);
		return ret;
	}

	@NotNull
	@Override
	public TextAttributesKey[] getTokenHighlights(IElementType type) {
		if (type == KW_AS |
			type == KW_BREAK |
			type == KW_CRATE |
			type == KW_ELSE |
			type == KW_ENUM |
			type == KW_EXTERN |
			type == KW_FALSE |
			type == KW_FN |
			type == KW_FOR |
			type == KW_IF |
			type == KW_IMPL |
			type == KW_IN |
			type == KW_LET |
			type == KW_LOOP |
			type == KW_MATCH |
			type == KW_MOD |
			type == KW_MUT |
			type == KW_PRIV |
			type == KW_PROC |
			type == KW_PUB |
			type == KW_REF |
			type == KW_RETURN |
			type == KW_SELF |
			type == KW_STATIC |
			type == KW_STRUCT |
			type == KW_SUPER |
			type == KW_TRAIT |
			type == KW_TRUE |
			type == KW_TYPE |
			type == KW_UNSAFE |
			type == KW_USE |
			type == KW_WHILE |
			type == KW_CONTINUE |
			type == KW_BOX)
		{
			return KEYWORD_KEYS;
		}

		if (type == RustTokens.IDENTIFIER) {
			return IDENTIFIER_KEYS;
		}
		if (type == RustTokens.BLOCK_COMMENT) {
			return BLOCK_COMMENT_KEYS;
		}
		if (type == RustTokens.LINE_COMMENT) {
			return LINE_COMMENT_KEYS;
		}
		if (type == RustTokens.BLOCK_DOC_COMMENT || type == RustTokens.LINE_DOC_COMMENT) {
			return DOC_COMMENT_KEYS;
		}
		if (type == CHAR_LIT || type == STRING_LIT || type == RAW_STRING_LIT) {
			return STRING_KEYS;
		}
		if (type == StringEscapesTokenTypes.VALID_STRING_ESCAPE_TOKEN) {
			return VALID_STRING_ESCAPE_KEYS;
		}
		if (type == StringEscapesTokenTypes.INVALID_CHARACTER_ESCAPE_TOKEN || type == StringEscapesTokenTypes.INVALID_UNICODE_ESCAPE_TOKEN) {
			return INVALID_STRING_ESCAPE_KEYS;
		}
		if (type == DEC_LIT || type == OCT_LIT || type == HEX_LIT || type == BIN_LIT) {
			return NUMBER_KEYS;
		}
		if (type == HASH) {
			return SYMBOL_KEYS;
		}
		if (type == OPEN_PAREN || type == CLOSE_PAREN) {
			return PARENTHESES_KEYS;
		}
		if (type == OPEN_BRACE || type == CLOSE_BRACE) {
			return BRACE_KEYS;
		}
		if (type == OPEN_SQUARE_BRACKET || type == CLOSE_SQUARE_BRACKET) {
			return BRACKET_KEYS;
		}
		// FIXME: I think there are missing operators here... (and some might not be operators as well)
		if (
				type == GREATER_THAN ||
				type == GREATER_THAN_OR_EQUAL ||
				type == LESS_THAN ||
				type == LESS_THAN_OR_EQUAL ||
				type == PLUS ||
				type == MINUS ||
				type == ASSIGN ||
				type == ASSIGN_LEFT_SHIFT ||
				type == ASSIGN_RIGHT_SHIFT ||
				type == MULTIPLY ||
				type == DIVIDE ||
				type == REMAINDER
		) {
			return OPERATOR_KEYS;
		}
		if (type == RustTokens.COMMA) {
			return COMMA_KEYS;
		}
		if (type == RustTokens.SEMICOLON) {
			return SEMICOLON_KEYS;
		}
		if (type == RustTokens.DOUBLE_COLON) {
			return PATH_SEPARATOR_KEYS;
		}

		return EMPTY_KEYS;
	}
}
