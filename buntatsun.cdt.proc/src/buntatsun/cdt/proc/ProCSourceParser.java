package buntatsun.cdt.proc;

import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTLabelStatement;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.parser.c.ICParserExtensionConfiguration;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.parser.EndOfFileException;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScanner;
import org.eclipse.cdt.core.parser.IToken;
import org.eclipse.cdt.core.parser.ParserMode;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.dom.parser.BacktrackException;
import org.eclipse.cdt.internal.core.dom.parser.DeclarationOptions;
import org.eclipse.cdt.internal.core.dom.parser.c.CASTFunctionCallExpression;
import org.eclipse.cdt.internal.core.dom.parser.c.GNUCSourceParser;

@SuppressWarnings("restriction")
public class ProCSourceParser extends GNUCSourceParser {

	public ProCSourceParser(IScanner scanner, ParserMode parserMode,
			IParserLogService logService, ICParserExtensionConfiguration config) {
		this(scanner, parserMode, logService, config, null);
	}

	public ProCSourceParser(IScanner scanner, ParserMode parserMode,
			IParserLogService logService, ICParserExtensionConfiguration config,
			IIndex index) {
		super(scanner, parserMode, logService, config, index);
	}

	@Override
	protected IASTStatement statement() throws EndOfFileException, BacktrackException {
		switch (LT(1)) {
		case IProCToken.tEXEC:
			switch (LT(2)) {
			case IProCToken.tSQL:
			case IProCToken.tORACLE:
			case IProCToken.tTOOLS:
			case IProCToken.tIAF:
				/*
				 * Pro*C
				 */
				return parseSqlStatement();
			}
		}

		return super.statement();
	}

	/*
	 * Pro*C
	 */
	protected IASTStatement parseSqlStatement() throws EndOfFileException, BacktrackException {
		final IToken t1 = consume();
		IASTStatement stmt = getNodeFactory().newCompoundStatement();

		int endOfProc = IToken.tSEMI;

		IToken t = t1;
		while (true) {
			final int type = t.getType();

			switch (type) {
			case IProCToken.tEXECUTE:
				switch (LT(1)) {
				case IProCToken.tDECLARE:
				case IProCToken.tBEGIN:
					endOfProc = IProCToken.tEND_EXEC;
					break;
				}
				break;
			}

			if (type == IToken.tIDENTIFIER || type >= IProCToken.FIRST_IProCToken) {
				/*
				 * Pro*C keyword
				 */
				final int offset = t.getOffset();
				final int endOffset = t.getEndOffset();
				final int length = t.getLength();

				IASTName name = getNodeFactory().newName(t.getCharImage());
				((ASTNode) name).setOffsetAndLength(offset, length);

				IBinding binding = new ProCBinding(name);
				name.setBinding(binding);

				ProCNullStatement null_statement = new ProCNullStatement(name);
				setRange(null_statement, offset, endOffset);
				null_statement.setParent(stmt);
				((IASTCompoundStatement) stmt).addStatement(null_statement);
			}

			if (type == endOfProc) {
				break;
			}

			for (int loop = 0; LT(1) == IToken.tCOLON && loop < 2; loop++) {
				/*
				 * Pro*C host & indicator variable. -> fake ++ expression.
				 *   1st loop : host variable
				 *   2nd loop : indicator variable
				 */
				LA(1).setType(IToken.tINCR);

				IASTExpression expression
					= unaryExpression(IASTUnaryExpression.op_prefixIncr, CastExprCtx.eDirectlyInBExpr, null);

				// ignore "bar" of ":foo(bar)"
				IASTNode[] nodes = expression.getChildren();
				for (int i = 0; i < nodes.length; i++) {
					if (nodes[i] instanceof CASTFunctionCallExpression) {
						CASTFunctionCallExpression fc = (CASTFunctionCallExpression) nodes[i];
						fc.setArguments(null);
					}
				}

				IASTExpressionStatement expressionStatement
					= getNodeFactory().newExpressionStatement(expression);
				setRange(expressionStatement, expression);

				expressionStatement.setParent(stmt);
				((IASTCompoundStatement) stmt).addStatement(expressionStatement);
			}

			t = consume();
		}

		if (stmt == null) {
			stmt = getNodeFactory().newNullStatement();
		}

		((ASTNode) stmt).setOffsetAndLength(t1.getOffset(), t.getEndOffset() - t1.getOffset());
		return stmt;
	}

	@Override
	protected IASTDeclaration[] problemDeclaration(int offset, BacktrackException bt, DeclarationOptions option) {
		try {
			switch (LT(1)) {
			case IProCToken.tEXEC:

				switch (LT(2)) {
				case IProCToken.tSQL:
				case IProCToken.tORACLE:
				case IProCToken.tTOOLS:
				case IProCToken.tIAF:
					IASTStatement stmt = getNodeFactory().newCompoundStatement();

					// skip to semicolon or END-EXEC
					int endOfProc = IToken.tSEMI;
					IToken t = consume();
					while (true) {
						switch (t.getType()) {
						case IProCToken.tEXECUTE:
							switch (LT(1)) {
							case IProCToken.tDECLARE:
							case IProCToken.tBEGIN:
								endOfProc = IProCToken.tEND_EXEC;
								break;
							}
							break;
						}

						final int type = t.getType();

						if (type == IToken.tIDENTIFIER || type >= IProCToken.FIRST_IProCToken) {
							final int p_offset = t.getOffset();
							final int p_endOffset = t.getEndOffset();
							final int p_length = t.getLength();

							IASTName name = getNodeFactory().newName(t.getCharImage());
							((ASTNode) name).setOffsetAndLength(p_offset, p_length);

							IBinding binding = new ProCBinding(name);
							name.setBinding(binding);

							IASTLabelStatement label_statement = getNodeFactory().newLabelStatement(name, null);
							setRange(label_statement, p_offset, p_endOffset);
							label_statement.setParent(stmt);
							((IASTCompoundStatement) stmt).addStatement(label_statement);
						}

						if (type == endOfProc) {
							break;
						}

						t = consume();
					}
					return new IASTDeclaration[] {};
				}
			}
		} catch (EndOfFileException e) {
			return new IASTDeclaration[] {};
		}
		return super.problemDeclaration(offset, bt, option);
	}
}
