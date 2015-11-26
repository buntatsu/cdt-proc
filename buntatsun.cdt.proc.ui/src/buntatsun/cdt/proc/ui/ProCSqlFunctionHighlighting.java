package buntatsun.cdt.proc.ui;

public class ProCSqlFunctionHighlighting extends AbstractProCHighlighting {
	public ProCSqlFunctionHighlighting() {
		keywords = UIActivator.getDefault()
				.getKeywordSet(UIActivator.KEYWORDSET_SQL_FUNCTIONS);
	}
}
