package buntatsun.cdt.proc.ui.preferences;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {
	public static final String PREF_STORE_DELIMITER = ",";
	public static final String PREF_PROC_KEYWORDS = "PROC_KEYWORDS";
	public static final String PREF_PROC_SQL_FUNCTIONS = "PROC_SQL_FUNCTIONS";
	public static final String PREF_PROC_USER_DEFINED = "PROC_USER_DEFINED";

	public static final String PREF_DESCRIPTION
		= "Expnad the tree to edit preferences for a specific feature.";
	public static final String PREF_DESCRIPTION_KEYWORD
		= "Text color and attributes can be set via "
			+ "<a href=\"org.eclipse.cdt.ui.preferences.CodeColoringPreferencePage\">"
			+ "C/C++ Syntax Coloring</a>."
			+ "\n(Close and open the editors to get keywords updated.)";

	public static final String RES_PROC_KEYWORDS = "/res/proc_keywords.txt";
	public static final String RES_PROC_SQL_FUNCTIONS = "/res/proc_sql_functions.txt";
	public static final String RES_PROC_USER_DEFINED = "/res/proc_user_defined.txt";

	public static final String TITLE_PROC_KEYWORDS = "Keyword";
	public static final String TITLE_PROC_SQL_FUNCTIONS = "SQL Function";
	public static final String TITLE_PROC_USER_DEFINED = "User Defined";

	public static final String FNAME_PROC_KEYWORDS = "proc_keywords";
	public static final String FNAME_PROC_SQL_FUNCTIONS = "proc_sql_functions";
	public static final String FNAME_PROC_USER_DEFINED = "proc_user_defined";

	public static final String MSG_EXPORT_OVERWRITE = "{0} exists.\nDo you want to replace it?";
	public static final String MSG_EXPORT_ERROR = "Failed to write keywords.";
	public static final String MSG_IMPORT_ERROR0 = "Failed to read file.";
	public static final String MSG_IMPORT_ERROR1 = "Failed to read file:\n{0}";
}
