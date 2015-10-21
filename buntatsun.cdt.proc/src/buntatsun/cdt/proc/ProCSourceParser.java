package buntatsun.cdt.proc;

import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
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
		case IProCToken.tSQL:
		case IProCToken.tORACLE:
		case IProCToken.tTOOLS:
		case IProCToken.tIAF:
			/*
			 * Pro*C
			 */
			return parseSqlStatement();
		}

		return super.statement();
	}

	/*
	 * Pro*C
	 */
	protected IASTStatement parseSqlStatement() throws EndOfFileException, BacktrackException {
		final IToken t1 = consume();
		IASTStatement stmt = null;

		int endOfProc = IToken.tSEMI;

		IToken t = t1;
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

			for (int loop = 0; LT(1) == IToken.tCOLON && loop < 2; loop++) {
				/*
				 * Pro*C host & indicator variable. -> fake ++ expression.
				 *   1st loop : host variable
				 *   2nd loop : indicator variable
				 */
				LA(1).setType(IToken.tINCR);

				IASTExpression expression
					= unaryExpression(IASTUnaryExpression.op_prefixIncr, CastExprCtx.eDirectlyInBExpr, null);
				IASTExpressionStatement expressionStatement
					= nodeFactory.newExpressionStatement(expression);
				setRange(expressionStatement, expression);
				if (stmt == null) {
					stmt = nodeFactory.newCompoundStatement();
				}
				expressionStatement.setParent(stmt);
				((IASTCompoundStatement) stmt).addStatement(expressionStatement);
			}

			t = consume();
			if (t.getType() == endOfProc) {
				break;
			}
		}

		if (stmt == null) {
			stmt = nodeFactory.newNullStatement();
		}

		((ASTNode) stmt).setOffsetAndLength(t1.getOffset(), t.getEndOffset() - t1.getOffset());
		return stmt;
	}

	@Override
	protected IASTDeclaration[] problemDeclaration(int offset, BacktrackException bt, DeclarationOptions option) {
		try {
			switch (LT(1)) {
			case IProCToken.tSQL:
			case IProCToken.tORACLE:
			case IProCToken.tTOOLS:
			case IProCToken.tIAF:
				// skip to semicolon or END-EXEC
				int endOfProc = IToken.tSEMI;
				IToken t;
				while ((t = consume()).getType() != endOfProc) {
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
				}
				return new IASTDeclaration[] {};
			}
		} catch (EndOfFileException e) {
			return new IASTDeclaration[] {};
		}
		return super.problemDeclaration(offset, bt, option);
	}
}
