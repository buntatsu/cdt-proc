package buntatsun.cdt.proc.ui;

public class ProCKeywordHighlighting extends AbstractProCHighlighting {
	public ProCKeywordHighlighting() {
		keywords = UIActivator.getDefault()
				.getKeywordSet(UIActivator.KEYWORDSET_KEYWORD);
	}
}
