package buntatsun.cdt.proc.ui;

import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.parser.util.CharArraySet;
import org.eclipse.cdt.ui.text.ISemanticHighlighter;
import org.eclipse.cdt.ui.text.ISemanticToken;

import buntatsun.cdt.proc.IProCBinding;

public class AbstractProCHighlighting implements ISemanticHighlighter {
	protected CharArraySet keywords;

	@Override
	public boolean consumes(ISemanticToken token) {
		IBinding binding = token.getBinding();
		if (binding instanceof IProCBinding) {
			if (keywords.lookup(binding.getNameCharArray()) >= 0) {
				return true;
			}
		}
		return false;
	}
}
