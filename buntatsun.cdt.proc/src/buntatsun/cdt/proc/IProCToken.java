package buntatsun.cdt.proc;

import org.eclipse.cdt.core.parser.IToken;

public interface IProCToken extends IToken {
//	int FIRST_IProCToken =  LAST_RESERVED_IExtensionToken + 1;
	int FIRST_IProCToken =  8000;

	public static final int
		tEXEC = FIRST_IProCToken + 1,
		tORACLE = FIRST_IProCToken + 2,
		tSQL = FIRST_IProCToken + 3,
		tINCLUDE = FIRST_IProCToken + 4,
		tBEGIN = FIRST_IProCToken + 5,
		tEND = FIRST_IProCToken + 6,
		tDECLARE = FIRST_IProCToken + 7,
		tSECTION = FIRST_IProCToken + 8,
		tCURSOR = FIRST_IProCToken + 9,
		tFOR = FIRST_IProCToken + 10,
		tOPEN = FIRST_IProCToken + 11,
		tCLOSE = FIRST_IProCToken + 12,
		tFETCH = FIRST_IProCToken + 13,
		tINTO = FIRST_IProCToken + 14,
		tVARCHAR = FIRST_IProCToken + 15,
		//
		tSELECT = FIRST_IProCToken + 100,
		tDELETE = FIRST_IProCToken + 101,
		tINSERT = FIRST_IProCToken + 102,
		tUPDATE = FIRST_IProCToken + 103,
		tTRUNCATE = FIRST_IProCToken + 104,

		tUNDEFINED_ = FIRST_IProCToken + 999;
}
