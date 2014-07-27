package buntatsun.cdt.proc;

import org.eclipse.cdt.core.dom.parser.c.GCCScannerExtensionConfiguration;
import org.eclipse.cdt.core.parser.IScannerInfo;

public class ProCScannerExtensionConfiguration extends
		GCCScannerExtensionConfiguration {
	private static ProCScannerExtensionConfiguration sInstance
		= new ProCScannerExtensionConfiguration();

	public static ProCScannerExtensionConfiguration getInstance() {
		return sInstance;
	}
	public static ProCScannerExtensionConfiguration getInstance(IScannerInfo info) {
		return getInstance();
	}

	public ProCScannerExtensionConfiguration() {
		super();

		addMacro(new String(ProCKeywords.cp_varchar), "struct {short len;char arr[1];}");
		addMacro(new String(ProCKeywords.cp_VARCHAR), "struct {short len;char arr[1];}");
		addMacro("sql_context", "void*");
		addMacro("SQL_CONTEXT", "void*");

//		addPreprocessorKeyword(ProCKeywords.cp_EXEC    , IProCToken.tEXEC);
//		addPreprocessorKeyword(ProCKeywords.cp_exec    , IProCToken.tEXEC);
//    	addPreprocessorKeyword(ProCKeywords.cp_SQL     , IProCToken.tSQL);
//    	addPreprocessorKeyword(ProCKeywords.cp_sql     , IProCToken.tSQL);
//    	addPreprocessorKeyword(ProCKeywords.cp_INCLUDE , IProCToken.tINCLUDE);
//    	addPreprocessorKeyword(ProCKeywords.cp_include , IProCToken.tINCLUDE);

//    	addKeyword(ProCKeywords.cp_EXEC, IProCToken.tEXEC);
//    	addKeyword(ProCKeywords.cp_exec, IProCToken.tEXEC);
//    	addKeyword(ProCKeywords.cp_SQL, IProCToken.tSQL);
//    	addKeyword(ProCKeywords.cp_sql, IProCToken.tSQL);
//    	addKeyword(ProCKeywords.cp_BEGIN, IProCToken.tBEGIN);
//    	addKeyword(ProCKeywords.cp_begin, IProCToken.tBEGIN);
//    	addKeyword(ProCKeywords.cp_END, IProCToken.tEND);
//    	addKeyword(ProCKeywords.cp_end, IProCToken.tEND);
//    	addKeyword(ProCKeywords.cp_DECLARE, IProCToken.tDECLARE);
//    	addKeyword(ProCKeywords.cp_declare, IProCToken.tDECLARE);
//    	addKeyword(ProCKeywords.cp_SECTION, IProCToken.tSECTION);
//    	addKeyword(ProCKeywords.cp_section, IProCToken.tSECTION);
//    	addKeyword(ProCKeywords.cp_CURSOR, IProCToken.tCURSOR);
//    	addKeyword(ProCKeywords.cp_cursor, IProCToken.tCURSOR);
//    	addKeyword(ProCKeywords.cp_FOR, IProCToken.tFOR);
//    	addKeyword(ProCKeywords.cp_for, IProCToken.tFOR);
//    	addKeyword(ProCKeywords.cp_OPEN, IProCToken.tOPEN);
//    	addKeyword(ProCKeywords.cp_open, IProCToken.tOPEN);
//    	addKeyword(ProCKeywords.cp_CLOSE, IProCToken.tCLOSE);
//    	addKeyword(ProCKeywords.cp_close, IProCToken.tCLOSE);
//    	addKeyword(ProCKeywords.cp_FETCH, IProCToken.tFETCH);
//    	addKeyword(ProCKeywords.cp_fetch, IProCToken.tFETCH);
//    	addKeyword(ProCKeywords.cp_INTO, IProCToken.tINTO);
//    	addKeyword(ProCKeywords.cp_into, IProCToken.tINTO);
//    	addKeyword(ProCKeywords.sq_SELECT, IProCToken.tSELECT);
//    	addKeyword(ProCKeywords.sq_select, IProCToken.tSELECT);
//    	addKeyword(ProCKeywords.sq_DELETE, IProCToken.tDELETE);
//    	addKeyword(ProCKeywords.sq_delete, IProCToken.tDELETE);
//    	addKeyword(ProCKeywords.sq_INSERT, IProCToken.tINSERT);
//    	addKeyword(ProCKeywords.sq_insert, IProCToken.tINSERT);
//    	addKeyword(ProCKeywords.sq_UPDATE, IProCToken.tUPDATE);
//    	addKeyword(ProCKeywords.sq_update, IProCToken.tUPDATE);
//    	addKeyword(ProCKeywords.sq_TRUNCATE, IProCToken.tTRUNCATE);
//    	addKeyword(ProCKeywords.sq_truncate, IProCToken.tTRUNCATE);
	}
}
