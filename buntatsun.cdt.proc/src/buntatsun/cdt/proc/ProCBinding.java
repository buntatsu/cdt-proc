package buntatsun.cdt.proc;

import org.eclipse.cdt.core.dom.ILinkage;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.internal.core.dom.Linkage;
import org.eclipse.cdt.internal.core.dom.parser.c.CVisitor;
import org.eclipse.core.runtime.PlatformObject;

@SuppressWarnings("restriction")
public class ProCBinding extends PlatformObject implements IProCBinding {
	private final IASTName procStatement;

	public ProCBinding(IASTName statement) {
		procStatement = statement;
		statement.setBinding(this);
	}

	@Override
	public String getName() {
		return procStatement.toString();
	}

	@Override
	public char[] getNameCharArray() {
		return procStatement.toCharArray();
	}

	@Override
	public ILinkage getLinkage() {
		return Linkage.C_LINKAGE;
	}

	@Override
	public IBinding getOwner() {
		return CVisitor.findEnclosingFunction(procStatement);
	}

	@Override
	public IScope getScope() throws DOMException {
		return CVisitor.getContainingScope(procStatement.getParent());
	}
}
