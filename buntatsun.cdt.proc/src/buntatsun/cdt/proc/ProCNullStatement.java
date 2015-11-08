package buntatsun.cdt.proc;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTLabelStatement;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNullStatement;
//import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.internal.core.dom.parser.ASTAttributeOwner;

@SuppressWarnings("restriction")
public class ProCNullStatement extends ASTAttributeOwner implements IASTNullStatement {
	private IASTName name;

	public ProCNullStatement() {
	}

	public ProCNullStatement(IASTName name) {
		setName(name);
	}

	@Override
	public ProCNullStatement copy() {
		return copy(CopyStyle.withoutLocations);
	}

	@Override
	public ProCNullStatement copy(CopyStyle style) {
		ProCNullStatement copy = new ProCNullStatement();
		copy.setName(name == null ? null : name.copy(style));
		return copy(copy, style);
	}

	public IASTName getName() {
		return name;
	}

	public void setName(IASTName name) {
		assertNotFrozen();
		this.name = name;
		if (name != null) {
			name.setParent(this);
			name.setPropertyInParent(IASTLabelStatement.NAME);
		}
    }

	@Override
	public boolean accept(ASTVisitor action) {
		if (action.shouldVisitStatements) {
			switch (action.visit(this)) {
				case ASTVisitor.PROCESS_ABORT: return false;
				case ASTVisitor.PROCESS_SKIP: return true;
				default: break;
			}
		}

		if (!acceptByAttributeSpecifiers(action)) return false;
		if (name != null && !name.accept(action)) return false;

		if (action.shouldVisitStatements) {
			switch (action.leave(this)) {
				case ASTVisitor.PROCESS_ABORT: return false;
				case ASTVisitor.PROCESS_SKIP: return true;
				default: break;
			}
		}
		return true;
    }
}
