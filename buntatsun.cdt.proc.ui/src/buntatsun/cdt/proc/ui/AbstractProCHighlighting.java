package buntatsun.cdt.proc.ui;

import java.util.Set;

import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.ui.text.ISemanticHighlighter;
import org.eclipse.cdt.ui.text.ISemanticToken;

import buntatsun.cdt.proc.IProCBinding;

public class AbstractProCHighlighting implements ISemanticHighlighter {
	protected Set<String> keywords;

	@Override
	public boolean consumes(ISemanticToken token) {
		IBinding binding = token.getBinding();
		if (binding instanceof IProCBinding) {
			if (keywords.contains(binding.getName())) {
				return true;
			}
		}
		return false;
	}
}
