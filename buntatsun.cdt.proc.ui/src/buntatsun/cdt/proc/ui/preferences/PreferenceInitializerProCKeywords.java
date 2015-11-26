package buntatsun.cdt.proc.ui.preferences;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import buntatsun.cdt.proc.ui.UIActivator;

public class PreferenceInitializerProCKeywords extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = UIActivator.getDefault().getPreferenceStore();

		initDefault(store
				, PreferenceConstants.PREF_PROC_KEYWORDS
				, PreferenceConstants.RES_PROC_KEYWORDS);

		initDefault(store
				, PreferenceConstants.PREF_PROC_SQL_FUNCTIONS
				, PreferenceConstants.RES_PROC_SQL_FUNCTIONS);

		initDefault(store
				, PreferenceConstants.PREF_PROC_USER_DEFINED
				, PreferenceConstants.RES_PROC_USER_DEFINED);
	}

	private void initDefault(IPreferenceStore store, String prefName, String resourceName) {
		StringBuilder sb = new StringBuilder();
		try {
			InputStream is = PreferenceInitializerProCKeywords.class.getResourceAsStream(resourceName);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			String line;
			while ( (line = br.readLine()) != null) {
				if (!line.isEmpty()) {
					sb.append(line + PreferenceConstants.PREF_STORE_DELIMITER);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		store.setDefault(prefName, sb.toString());
	}
}
