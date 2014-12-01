package vektah.rust.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.CustomFoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import vektah.rust.psi.RustTokens;

import java.util.List;

public class RustFoldingBuilder extends CustomFoldingBuilder {

	@Override
	protected void buildLanguageFoldRegions(@NotNull List<FoldingDescriptor> descriptors, @NotNull PsiElement root,
											@NotNull Document document, boolean quick) {
		recurse(root.getNode(), document, descriptors);
	}

	private void recurse(ASTNode node, Document document, List<FoldingDescriptor> descriptors) {
		if (node.getElementType() == RustTokens.STATEMENT_BLOCK && spanMultipleLines(node, document)) {
			descriptors.add(new FoldingDescriptor(node, node.getTextRange()));
		}

		for (ASTNode child : node.getChildren(null)) {
			recurse(child, document, descriptors);
		}
	}

	@Override
	protected String getLanguagePlaceholderText(@NotNull ASTNode astNode, @NotNull TextRange textRange) {
		return "{...}";
	}

	@Override
	protected boolean isRegionCollapsedByDefault(@NotNull ASTNode astNode) {
		return false;
	}

	private static boolean spanMultipleLines(@NotNull ASTNode node, @NotNull Document document) {
		final TextRange range = node.getTextRange();
		return document.getLineNumber(range.getStartOffset()) < document.getLineNumber(range.getEndOffset());
	}
}
