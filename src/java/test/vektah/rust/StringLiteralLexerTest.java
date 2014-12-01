package vektah.rust;

import vektah.rust.psi.RustTokens;

public class StringLiteralLexerTest extends PyLexerTestCase {
	public void testValidBackslashN() {
		PyLexerTestCase.doLexerTest("\"foo\\nbar\"", new RustStringLiteralLexer(RustTokens.STRING_LIT),
				"STRING_LIT", "VALID_STRING_ESCAPE_TOKEN", "STRING_LIT");
	}

	public void testValidHexEscape() {
		PyLexerTestCase.doLexerTest("\"foo\\x12bar\"", new RustStringLiteralLexer(RustTokens.STRING_LIT),
				"STRING_LIT", "VALID_STRING_ESCAPE_TOKEN", "STRING_LIT");
	}

	public void testBackslashA() {
		PyLexerTestCase.doLexerTest("\"foo\\abar\"", new RustStringLiteralLexer(RustTokens.STRING_LIT),
				"STRING_LIT", "INVALID_CHARACTER_ESCAPE_TOKEN", "STRING_LIT");
	}
}
