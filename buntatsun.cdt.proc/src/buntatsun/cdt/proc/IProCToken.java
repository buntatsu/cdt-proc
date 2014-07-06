package buntatsun.cdt.proc;

import org.eclipse.cdt.core.parser.IToken;

public interface IProCToken extends IToken {
//	int FIRST_IProCToken =  LAST_RESERVED_IExtensionToken + 1;
	int FIRST_IProCToken =  8000;

	public static final int
		tEXEC = FIRST_IProCToken + 1,
		tSQL = FIRST_IProCToken + 2,
		tINCLUDE = FIRST_IProCToken + 3,
		tBEGIN = FIRST_IProCToken + 4,
		tEND = FIRST_IProCToken + 5,
		tDECLARE = FIRST_IProCToken + 6,
		tSECTION = FIRST_IProCToken + 7,
		tCURSOR = FIRST_IProCToken + 8,
		tFOR = FIRST_IProCToken + 9,
		tOPEN = FIRST_IProCToken + 10,
		tCLOSE = FIRST_IProCToken + 11,
		tFETCH = FIRST_IProCToken + 12,
		tINTO = FIRST_IProCToken + 13,
		tVARCHAR = FIRST_IProCToken + 14,
		//
		tSELECT = FIRST_IProCToken + 15,
		tDELETE = FIRST_IProCToken + 16,
		tINSERT = FIRST_IProCToken + 17,
		tUPDATE = FIRST_IProCToken + 18,
		tTRUNCATE = FIRST_IProCToken + 19,

		tUNDEFINED_ = FIRST_IProCToken + 999;
}
