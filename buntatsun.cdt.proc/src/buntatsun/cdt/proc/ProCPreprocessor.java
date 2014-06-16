package buntatsun.cdt.proc;

import org.eclipse.cdt.core.dom.parser.IScannerExtensionConfiguration;
import org.eclipse.cdt.core.parser.EndOfFileException;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IProblem;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IToken;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.OffsetLimitReachedException;
import org.eclipse.cdt.core.parser.ParserLanguage;
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
	}

	@Override
	protected Token internalFetchToken(ScannerContext uptoEndOfCtx,
			int options, boolean withinExpansion)
			throws OffsetLimitReachedException {

		Token ppToken= fCurrentContext.currentLexerToken();

        while (true) {
        	switch (ppToken.getType()) {
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
        		 * ProC
        		 */
        		ppToken= fCurrentContext.nextPPToken();
            	if (ppToken.getType() == IProCToken.tSQL) {
            		ppToken= fCurrentContext.nextPPToken();
                	final char[] ppName = ppToken.getCharImage();
                	final int ppType = fPPKeywords.get(ppName);

                	switch (ppType) {
                	case IProCToken.tINCLUDE:
                		final Lexer lexer= fCurrentContext.getLexer();
                		if (lexer != null) {
                			executeInclude(lexer, ppToken.getOffset(), ppType,
                					fCurrentContext.getCodeState() == CodeState.eActive, withinExpansion);
                			ppToken= fCurrentContext.currentLexerToken();
                			continue;
                		}
                	}

        			final char[] name= ppToken.getCharImage();
        			int tokenType = fKeywords.get(name);
        			if (tokenType == fKeywords.undefined) {
        				tokenType = IProCToken.tUNDEFINED_;
        			}
    				ppToken.setType(tokenType);
    				return ppToken;
            	}
        		break;

        	}
        	fCurrentContext.nextPPToken();
        	return ppToken;
        }
    }

	@Override
	public IToken nextToken() throws EndOfFileException {
		//FIXEME bunbun neo
		IToken t;
		t = super.nextToken();
//		System.out.println("nextToke(): Token(" + t.getType() + ")[" + t.getImage() + "]");

//		if (t.getType() == IToken.tPROC_VARCHAR) {
//			t.setType(IToken.tIDENTIFIER);
//		}

		if (t.getType() >= IProCToken.FIRST_IProCToken) {
			// ";"まで読み飛ばし
			while ((t = super.nextToken()).getType() != IToken.tSEMI) {
				;
//				System.out.println("#### skipping... t type[" + t.getType() + "],image[" + t.getImage() + "]");
			}
		}

		return t;
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
