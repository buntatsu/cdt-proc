package buntatsun.cdt.proc.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ProCPreferencePage extends PreferencePage
	implements IWorkbenchPreferencePage {

	public ProCPreferencePage() {
		setDescription(PreferenceConstants.PREF_DESCRIPTION);
		noDefaultAndApplyButton();
	}

	@Override
	public void init(IWorkbench workbench) {
		// empty implementation
	}

	@Override
	protected Control createContents(Composite parent) {
		return null;
	}
}