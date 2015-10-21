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
		addMacro("sql_cursor", "void*");
		addMacro("SQL_CURSOR", "void*");
	}
}
