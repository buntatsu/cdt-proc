package buntatsun.cdt.proc.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferencesUtil;

import buntatsun.cdt.proc.ui.UIActivator;

public class ProCKeywordPreferencePage extends PreferencePage
	implements IWorkbenchPreferencePage, Listener {

	KeywordTab tabs[];

	/**
	 * Create the preference page.
	 */
	public ProCKeywordPreferencePage() {
	}

	/**
	 * Create contents of the preference page.
	 * @param parent
	 */
	@Override
	public Control createContents(Composite parent) {
		tabs = new KeywordTab[3];

		tabs[0] = new KeywordTab(UIActivator.KEYWORDSET_KEYWORD
				, getPreferenceStore()
				, PreferenceConstants.PREF_PROC_KEYWORDS
				, PreferenceConstants.TITLE_PROC_KEYWORDS
				, PreferenceConstants.FNAME_PROC_KEYWORDS);

		tabs[1] = new KeywordTab(UIActivator.KEYWORDSET_SQL_FUNCTIONS
				, getPreferenceStore()
				, PreferenceConstants.PREF_PROC_SQL_FUNCTIONS
				, PreferenceConstants.TITLE_PROC_SQL_FUNCTIONS
				, PreferenceConstants.FNAME_PROC_SQL_FUNCTIONS);

		tabs[2] = new KeywordTab(UIActivator.KEYWORDSET_USER_DEFINED
				, getPreferenceStore()
				, PreferenceConstants.PREF_PROC_USER_DEFINED
				, PreferenceConstants.TITLE_PROC_USER_DEFINED
				, PreferenceConstants.FNAME_PROC_USER_DEFINED);

		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Link link = new Link(container, SWT.NONE);
		link.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		link.setText(PreferenceConstants.PREF_DESCRIPTION_KEYWORD);
		link.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				String u = event.text;
				PreferencesUtil.createPreferenceDialogOn(getShell(), u, null, null);
			}
		});

		TabFolder folder= new TabFolder(container, SWT.NONE);
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));

		TabItem item = new TabItem(folder, SWT.NONE);
		item.setText(PreferenceConstants.TITLE_PROC_KEYWORDS);
		item.setControl(tabs[0].createTabContents(folder));

		item = new TabItem(folder, SWT.NONE);
		item.setText(PreferenceConstants.TITLE_PROC_SQL_FUNCTIONS);
		item.setControl(tabs[1].createTabContents(folder));

		item = new TabItem(folder, SWT.NONE);
		item.setText(PreferenceConstants.TITLE_PROC_USER_DEFINED);
		item.setControl(tabs[2].createTabContents(folder));

		return folder;
	}

	/**
	 * Initialize the preference page.
	 */
	public void init(IWorkbench workbench) {
		// empty implementation
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
        return UIActivator.getDefault().getPreferenceStore();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();

		for (KeywordTab tab : tabs) {
			tab.initializeDefaults();
		}
	}

    @Override
	protected void performApply() {
		super.performApply();
		storeValues();
	}

	@Override
	public boolean performOk() {
		storeValues();
		return true;
	}

	private void storeValues() {
		for (KeywordTab tab : tabs) {
			tab.storeValues();
		}
	}

	@Override
	public void handleEvent(Event event) {
		// empty implementation
	}
}
