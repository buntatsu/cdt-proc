package buntatsun.cdt.proc;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "buntatsun.cdt.proc";

	private static Activator plugin;
	private ResourceBundle resourceBundle;

	public Activator() {
		Assert.isTrue(plugin == null);
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle(PLUGIN_ID + ".Resources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	public static String getPluginId() {
		return PLUGIN_ID;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}
}
