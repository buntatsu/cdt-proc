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
    	fProCKeywords.put(ProCKeywords.cp_ORACLE, IProCToken.tORACLE);
    	fProCKeywords.put(ProCKeywords.cp_oracle, IProCToken.tORACLE);
    	fProCKeywords.put(ProCKeywords.cp_SQL, IProCToken.tSQL);
    	fProCKeywords.put(ProCKeywords.cp_sql, IProCToken.tSQL);
    	fProCKeywords.put(ProCKeywords.cp_INCLUDE , IProCToken.tINCLUDE);
    	fProCKeywords.put(ProCKeywords.cp_include , IProCToken.tINCLUDE);

    	fProCKeywords.put(ProCKeywords.cp_BEGIN, IProCToken.tBEGIN);
    	fProCKeywords.put(ProCKeywords.cp_begin, IProCToken.tBEGIN);
    	fProCKeywords.put(ProCKeywords.cp_END, IProCToken.tEND);
    	fProCKeywords.put(ProCKeywords.cp_end, IProCToken.tEND);
    	fProCKeywords.put(ProCKeywords.cp_DECLARE, IProCToken.tDECLARE);
    	fProCKeywords.put(ProCKeywords.cp_declare, IProCToken.tDECLARE);
    	fProCKeywords.put(ProCKeywords.cp_SECTION, IProCToken.tSECTION);
    	fProCKeywords.put(ProCKeywords.cp_section, IProCToken.tSECTION);
    	fProCKeywords.put(ProCKeywords.cp_CURSOR, IProCToken.tCURSOR);
    	fProCKeywords.put(ProCKeywords.cp_cursor, IProCToken.tCURSOR);
    	fProCKeywords.put(ProCKeywords.cp_FOR, IProCToken.tFOR);
    	fProCKeywords.put(ProCKeywords.cp_for, IProCToken.tFOR);
    	fProCKeywords.put(ProCKeywords.cp_OPEN, IProCToken.tOPEN);
    	fProCKeywords.put(ProCKeywords.cp_open, IProCToken.tOPEN);
    	fProCKeywords.put(ProCKeywords.cp_CLOSE, IProCToken.tCLOSE);
    	fProCKeywords.put(ProCKeywords.cp_close, IProCToken.tCLOSE);
    	fProCKeywords.put(ProCKeywords.cp_FETCH, IProCToken.tFETCH);
    	fProCKeywords.put(ProCKeywords.cp_fetch, IProCToken.tFETCH);
    	fProCKeywords.put(ProCKeywords.cp_INTO, IProCToken.tINTO);
    	fProCKeywords.put(ProCKeywords.cp_into, IProCToken.tINTO);

    	fProCKeywords.put(ProCKeywords.sq_SELECT, IProCToken.tSELECT);
    	fProCKeywords.put(ProCKeywords.sq_select, IProCToken.tSELECT);
    	fProCKeywords.put(ProCKeywords.sq_DELETE, IProCToken.tDELETE);
    	fProCKeywords.put(ProCKeywords.sq_delete, IProCToken.tDELETE);
    	fProCKeywords.put(ProCKeywords.sq_INSERT, IProCToken.tINSERT);
    	fProCKeywords.put(ProCKeywords.sq_insert, IProCToken.tINSERT);
    	fProCKeywords.put(ProCKeywords.sq_UPDATE, IProCToken.tUPDATE);
    	fProCKeywords.put(ProCKeywords.sq_update, IProCToken.tUPDATE);
    	fProCKeywords.put(ProCKeywords.sq_TRUNCATE, IProCToken.tTRUNCATE);
    	fProCKeywords.put(ProCKeywords.sq_truncate, IProCToken.tTRUNCATE);
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
        		Token tokenSql = null;

        		ppToken= fCurrentContext.nextPPToken();

//TODO EXEC<LF>SQL
        		if (ppToken.getType() == Lexer.tNEWLINE) {
            		ppToken= fCurrentContext.nextPPToken();
        		}

        		final int ppt = fProCKeywords.get(ppToken.getCharImage());
        		switch (ppt) {
        		case IProCToken.tSQL:
        		case IProCToken.tORACLE:
           			ppToken.setType(ppt);

           			tokenSql = ppToken;		// save "SQL","ORACLE"
            		ppToken = fCurrentContext.nextPPToken();
                	final char[] ppName = ppToken.getCharImage();
                	final int ppType = fProCKeywords.get(ppName);

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

    				return tokenSql;	// return "SQL","ORACLE"
            	}
//FIXME
//           	switch (ppToken.getType()) {
////        	case Lexer.tNEWLINE:
//            	case Lexer.tOTHER_CHARACTER:
//            	case IToken.tEND_OF_INPUT:
//           		continue;
//           	}

//FIXME
        		break;
//           	return ppToken;	//<- endless loop?

        	}
        	fCurrentContext.nextPPToken();
        	return ppToken;
        }
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
