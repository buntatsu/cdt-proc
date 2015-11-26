package buntatsun.cdt.proc.ui;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import buntatsun.cdt.proc.ui.preferences.PreferenceConstants;

/**
 * The activator class controls the plug-in life cycle
 */
public class UIActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "buntatsun.cdt.proc.ui"; //$NON-NLS-1$

	// The shared instance
	private static UIActivator plugin;

	public IPreferenceStore store;

	// Highlighting Keywords
	public static final int KEYWORDSET_KEYWORD = 0;
	public static final int KEYWORDSET_SQL_FUNCTIONS = 1;
	public static final int KEYWORDSET_USER_DEFINED = 2;
	@SuppressWarnings("unchecked")
	private final Set<String>[] highlightingKeywords = new Set[3];

	/**
	 * The constructor
	 */
	public UIActivator() {
		for (int i = 0; i < highlightingKeywords.length; i++) {
			highlightingKeywords[i] = new HashSet<>();
		}
	}

	/**
	 *
	 * @param kind KEYWORDSET_SQL_KEYWORD, KEYWORDSET_SQL_FUNCTIONS, KEYWORDSET_USER_DEFINED
	 * @return
	 */
	public Set<String> getKeywordSet(int kind) {
		return highlightingKeywords[kind];
	}

	/**
	 *
	 * @param kind KEYWORDSET_SQL_KEYWORD, KEYWORDSET_SQL_FUNCTIONS, KEYWORDSET_USER_DEFINED
	 * @param uppercaseSet
	 */
	public void replaceKeyword(int kind, String[] uppercase) {
		Set<String> set = highlightingKeywords[kind];
		set.clear();
		for (String s : uppercase) {
			set.add(s);
		}

		expandLowerKeywords(set);
	}

	private void expandLowerKeywords(Set<String> set) {
		Set<String> wkSet = new HashSet<String>();
		for (String s : set) {
			wkSet.add(s.toLowerCase());
		}
		set.addAll(wkSet);
	}

	private void initKeyword(Set<String> set, String prefName) {
		set.clear();
		String value = store.getString(prefName);
		StringTokenizer st = new StringTokenizer(value, PreferenceConstants.PREF_STORE_DELIMITER);
		while (st.hasMoreTokens()) {
			String key = st.nextToken();
			if (!key.isEmpty()) {
				set.add(key);
			}
		}

		expandLowerKeywords(set);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		store = UIActivator.getDefault().getPreferenceStore();

		initKeyword(highlightingKeywords[KEYWORDSET_KEYWORD]
				, PreferenceConstants.PREF_PROC_KEYWORDS);
		initKeyword(highlightingKeywords[KEYWORDSET_SQL_FUNCTIONS]
				, PreferenceConstants.PREF_PROC_SQL_FUNCTIONS);
		initKeyword(highlightingKeywords[KEYWORDSET_USER_DEFINED]
				, PreferenceConstants.PREF_PROC_USER_DEFINED);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static UIActivator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
