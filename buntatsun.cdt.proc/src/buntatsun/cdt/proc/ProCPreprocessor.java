package buntatsun.cdt.proc;

import org.eclipse.cdt.core.dom.parser.IScannerExtensionConfiguration;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IProblem;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IToken;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.OffsetLimitReachedException;
import org.eclipse.cdt.core.parser.ParserLanguage;
import org.eclipse.cdt.core.parser.util.CharArrayIntMap;
import org.eclipse.cdt.core.parser.util.CharArrayMap;
import org.eclipse.cdt.internal.core.parser.scanner.ASTInclusionStatement;
import org.eclipse.cdt.internal.core.parser.scanner.AbstractCharArray;
import org.eclipse.cdt.internal.core.parser.scanner.CPreprocessor;
import org.eclipse.cdt.internal.core.parser.scanner.ILexerLog;
import org.eclipse.cdt.internal.core.parser.scanner.ILocationCtx;
import org.eclipse.cdt.internal.core.parser.scanner.Lexer;
import org.eclipse.cdt.internal.core.parser.scanner.Lexer.LexerOptions;
import org.eclipse.cdt.internal.core.parser.scanner.ScannerContext;
import org.eclipse.cdt.internal.core.parser.scanner.ScannerContext.CodeState;
import org.eclipse.cdt.internal.core.parser.scanner.Token;

@SuppressWarnings("restriction")
public class ProCPreprocessor extends CPreprocessor {

	public ProCPreprocessor(FileContent fileContent, IScannerInfo info,
			ParserLanguage language, IParserLogService log,
			IScannerExtensionConfiguration configuration,
			IncludeFileContentProvider readerFactory) {

		super(fileContent, info, language, log, configuration, readerFactory);

		addProCHeaderReplaces();
		addProCKeywords();
	}

	private CharArrayIntMap fProCKeywords = new CharArrayIntMap(10, -1);

	protected void addProCKeywords() {
		fProCKeywords.put(ProCKeywords.cp_EXEC, IProCToken.tEXEC);
		fProCKeywords.put(ProCKeywords.cp_exec, IProCToken.tEXEC);
		fProCKeywords.put(ProCKeywords.cp_SQL, IProCToken.tSQL);
		fProCKeywords.put(ProCKeywords.cp_sql, IProCToken.tSQL);
		fProCKeywords.put(ProCKeywords.cp_ORACLE, IProCToken.tORACLE);
		fProCKeywords.put(ProCKeywords.cp_oracle, IProCToken.tORACLE);
		fProCKeywords.put(ProCKeywords.cp_TOOLS , IProCToken.tTOOLS);
		fProCKeywords.put(ProCKeywords.cp_tools , IProCToken.tTOOLS);
		fProCKeywords.put(ProCKeywords.cp_IAF , IProCToken.tIAF);
		fProCKeywords.put(ProCKeywords.cp_iaf , IProCToken.tIAF);

		fProCKeywords.put(ProCKeywords.cp_INCLUDE , IProCToken.tINCLUDE);
		fProCKeywords.put(ProCKeywords.cp_include , IProCToken.tINCLUDE);
		fProCKeywords.put(ProCKeywords.cp_EXECUTE , IProCToken.tEXECUTE);
		fProCKeywords.put(ProCKeywords.cp_execute , IProCToken.tEXECUTE);
		fProCKeywords.put(ProCKeywords.cp_DECLARE, IProCToken.tDECLARE);
		fProCKeywords.put(ProCKeywords.cp_declare, IProCToken.tDECLARE);
		fProCKeywords.put(ProCKeywords.cp_BEGIN, IProCToken.tBEGIN);
		fProCKeywords.put(ProCKeywords.cp_begin, IProCToken.tBEGIN);
		fProCKeywords.put(ProCKeywords.cp_END_EXEC , IProCToken.tEND_EXEC);
		fProCKeywords.put(ProCKeywords.cp_end_exec , IProCToken.tEND_EXEC);
	}

	private CharArrayMap<char[]> fHeaderReplaces = new CharArrayMap<>(8);

	protected void addProCHeaderReplaces() {
		fHeaderReplaces.put(ProCKeywords.rh_ORACA_H, ProCKeywords.rh_oraca_h);
		fHeaderReplaces.put(ProCKeywords.rh_SQLCA_H, ProCKeywords.rh_sqlca_h);
		fHeaderReplaces.put(ProCKeywords.rh_SQLDA_H, ProCKeywords.rh_sqlda_h);

		fHeaderReplaces.put(ProCKeywords.rh_ORACA, ProCKeywords.rh_oraca_h);
		fHeaderReplaces.put(ProCKeywords.rh_SQLCA, ProCKeywords.rh_sqlca_h);
		fHeaderReplaces.put(ProCKeywords.rh_SQLDA, ProCKeywords.rh_sqlda_h);

		fHeaderReplaces.put(ProCKeywords.rh_oraca, ProCKeywords.rh_oraca_h);
		fHeaderReplaces.put(ProCKeywords.rh_sqlca, ProCKeywords.rh_sqlca_h);
		fHeaderReplaces.put(ProCKeywords.rh_sqlda, ProCKeywords.rh_sqlda_h);
	}

	boolean isInsideProCBlock = false;
	int endOfProCBlock = IToken.tSEMI;
	@Override
	protected Token internalFetchToken(ScannerContext uptoEndOfCtx,
			int options, boolean withinExpansion)
					throws OffsetLimitReachedException {
		Token ppToken= fCurrentContext.currentLexerToken();
		while (true) {
			if (isInsideProCBlock) {
				/*
				 * Pro*C
				 */
				final int ppt = fProCKeywords.get(ppToken.getCharImage());
				if (ppt > 0) {
					ppToken.setType(ppt);
				}
			}

			final int type = ppToken.getType();
			switch (type) {
			case Lexer.tBEFORE_INPUT:
				ppToken= fCurrentContext.nextPPToken();
				continue;

			case Lexer.tNEWLINE:
				if ((options & STOP_AT_NL) != 0) {
					return ppToken;
				}
				ppToken= fCurrentContext.nextPPToken();
				continue;

			case Lexer.tOTHER_CHARACTER:
				handleProblem(IProblem.SCANNER_BAD_CHARACTER, ppToken.getCharImage(),
						ppToken.getOffset(), ppToken.getEndOffset());
				ppToken= fCurrentContext.nextPPToken();
				continue;

			case IToken.tEND_OF_INPUT:
				if (fCurrentContext == uptoEndOfCtx || uptoEndOfCtx == null) {
					if (fCurrentContext == fRootContext && !fHandledEndOfTranslationUnit
							&& (options & STOP_AT_NL) == 0) {
						fHandledEndOfTranslationUnit= true;
						fLocationMap.endTranslationUnit(ppToken.getEndOffset(), fCurrentContext.getSignificantMacros());
					}
					return ppToken;
				}

				final ILocationCtx locationCtx = fCurrentContext.getLocationCtx();
				ASTInclusionStatement inc = locationCtx.getInclusionStatement();
				if (inc != null) {
					completeInclusion(inc);
				}
				fLocationMap.popContext(locationCtx);

				fCurrentContext.propagateSignificantMacros();
				fCurrentContext= fCurrentContext.getParent();
				assert fCurrentContext != null;

				ppToken= fCurrentContext.currentLexerToken();
				continue;

			case IToken.tPOUND:
			{
				final Lexer lexer= fCurrentContext.getLexer();
				if (lexer != null && lexer.currentTokenIsFirstOnLine()) {
					executeDirective(lexer, ppToken.getOffset(), withinExpansion);
					ppToken= fCurrentContext.currentLexerToken();
					continue;
				}
				break;
			}

			case IToken.tIDENTIFIER:
				fCurrentContext.nextPPToken(); // consume the identifier
				if ((options & NO_EXPANSION) == 0) {
					final Lexer lexer= fCurrentContext.getLexer();
					if (lexer != null && expandMacro(ppToken, lexer, options, withinExpansion)) {
						ppToken= fCurrentContext.currentLexerToken();
						continue;
					}

					final char[] name= ppToken.getCharImage();
					int tokenType = fKeywords.get(name);
					if (tokenType != fKeywords.undefined) {
						ppToken.setType(tokenType);
					}
				}
				return ppToken;

			case IToken.tINTEGER:
				if ((options & CHECK_NUMBERS) != 0) {
					checkNumber(ppToken, false);
				}
				break;

			case IToken.tFLOATINGPT:
				if ((options & CHECK_NUMBERS) != 0) {
					checkNumber(ppToken, true);
				}
				break;

			case IProCToken.tEXEC:
				/*
				 * Pro*C
				 */
				Token tokenExec = ppToken;	// save "EXEC"

				// skip newlines
				while ((ppToken = fCurrentContext.nextPPToken()).getType() == Lexer.tNEWLINE) {
					;
				}

//TODO When only the exec, it is not treated as Pro*C block. (int exec = 0;)
				tokenExec.setNext(ppToken);

				final int ppt = fProCKeywords.get(ppToken.getCharImage());
				switch (ppt) {
				case IProCToken.tSQL:
				case IProCToken.tORACLE:
				case IProCToken.tTOOLS:
				case IProCToken.tIAF:
					isInsideProCBlock = true;
					endOfProCBlock = IToken.tSEMI;

					final ProCLexer pl = (ProCLexer) fCurrentContext.getLexer();
					if (pl != null) {
						pl.saveState();
						pl.isInsideProCBlock = true;
					}

					ppToken = fCurrentContext.nextPPToken();
					final char[] ppName = ppToken.getCharImage();
					final int ppType = fProCKeywords.get(ppName);

					switch (ppType) {
					case IProCToken.tINCLUDE:
						isInsideProCBlock = false;
						final Lexer lexer= fCurrentContext.getLexer();
						if (lexer != null) {
							executeInclude(lexer, ppToken.getOffset(), ppType,
									fCurrentContext.getCodeState() == CodeState.eActive, withinExpansion);
							ppToken= fCurrentContext.currentLexerToken();
							if (pl != null) {
								pl.isInsideProCBlock = false;
							}
							continue;
						}
						break;
					}

					if (pl != null) {
						pl.restoreState();
					}
					break;

//TODO When only "exec", it is not treated as Pro*C block. (int exec = 0;)
//				default:
//					tokenExec.setType(IToken.tIDENTIFIER);
//					ppToken = tokenExec;
//					continue;
				}
				return tokenExec;	// return "EXEC"

			case IProCToken.tEXECUTE:
				final Token tokenExecute = ppToken;	// save "EXECUTE"

				// skip newlines
				while ((ppToken = fCurrentContext.nextPPToken()).getType() == Lexer.tNEWLINE) {
					;
				}

				final char[] ppName = ppToken.getCharImage();
				final int ppType = fProCKeywords.get(ppName);
				switch (ppType) {
				case IProCToken.tDECLARE:
				case IProCToken.tBEGIN:
					endOfProCBlock = IProCToken.tEND_EXEC;
					break;
				}
				return tokenExecute;
			}

			if (isInsideProCBlock) {
				/*
				 * Pro*C
				 */
				if (type == endOfProCBlock) {
					isInsideProCBlock = false;
					endOfProCBlock = IToken.tSEMI;
					final ProCLexer pl = (ProCLexer) fCurrentContext.getLexer();
					if (pl != null) {
						pl.isInsideProCBlock = false;
					}
				}
			}

			fCurrentContext.nextPPToken();
			return ppToken;
		}
	}

	@Override
	protected boolean expandMacro(final Token identifier, Lexer lexer, int options,
			boolean withinExpansion) throws OffsetLimitReachedException {
		if (isInsideProCBlock) {
			/*
			 * Inside Pro*C Block, not expand macro
			 */
			return false;
		}
		return super.expandMacro(identifier, lexer, options, withinExpansion);
	}

	@Override
	protected char[] extractHeaderName(
			final char[] image, final char startDelim, final char endDelim, int[] offsets) {

		char[] headerName = super.extractHeaderName(image, startDelim, endDelim, offsets);
		char[] headerNameConv = null;

		headerNameConv = fHeaderReplaces.get(headerName);

		return headerNameConv == null ? headerName : headerNameConv;
	}

	@Override
	protected Lexer newLexer(char[] input, LexerOptions options, ILexerLog log, Object source) {
		return new ProCLexer(input, options, log, source);
	}

	@Override
	protected Lexer newLexer(AbstractCharArray input, LexerOptions options, ILexerLog log, Object source) {
		return new ProCLexer(input, options, log, source);
	}
}
