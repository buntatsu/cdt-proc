package buntatsun.cdt.proc;

import org.eclipse.cdt.core.parser.IToken;

public interface IProCToken extends IToken {
//	int FIRST_IProCToken =  LAST_RESERVED_IExtensionToken + 1;
	int FIRST_IProCToken =  8000;

	public static final int
		tEXEC = FIRST_IProCToken + 1,
		tSQL = FIRST_IProCToken + 2,
		tORACLE = FIRST_IProCToken + 3,
		tTOOLS = FIRST_IProCToken + 4,
		tIAF = FIRST_IProCToken + 5,

		tINCLUDE = FIRST_IProCToken + 10,
		tEXECUTE = FIRST_IProCToken + 11,
		tDECLARE = FIRST_IProCToken + 12,
		tBEGIN = FIRST_IProCToken + 13,
		tEND_EXEC = FIRST_IProCToken + 14,

		tUNDEFINED_ = FIRST_IProCToken + 999;
}
