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
			 * ProC
			 */
			return parseSqlStatement();
		}

		return super.statement();
	}

	/*
	 * ProC
	 */
	protected IASTStatement parseSqlStatement() throws EndOfFileException, BacktrackException {
		final IToken t1 = consume();
		IToken t;
		IASTStatement stmt = null;

		int endOfProc = IToken.tSEMI;

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

			if (LT(1) == IToken.tINCR) {
				/*
				 * Pro*C host variable. -> fake ++ expression.
				 * (See ProCLexer.fetchToken)
				 */
				IASTExpression expression
					= unaryExpression(IASTUnaryExpression.op_prefixIncr, CastExprCtx.eInBExpr, null);
				IASTExpressionStatement expressionStatement
					= nodeFactory.newExpressionStatement(expression);
				setRange(expressionStatement, expression);
				if (stmt == null) {
					stmt = nodeFactory.newCompoundStatement();
				}
				expressionStatement.setParent(stmt);
				((IASTCompoundStatement) stmt).addStatement(expressionStatement);
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
