package buntatsun.cdt.proc.ui;

public class ProCUserDefinedHighlighting extends AbstractProCHighlighting {
	public ProCUserDefinedHighlighting() {
		keywords = UIActivator.getDefault()
				.getKeywordSet(UIActivator.KEYWORDSET_USER_DEFINED);
	}
}
