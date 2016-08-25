package org.globaltester.base;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

/**
 * Helper class providing methods for accessing eclipse preferences.
 * 
 * @author mboonk
 *
 */
public class PreferenceHelper {
	private static IScopeContext context = InstanceScope.INSTANCE;

	/**
	 * Read the preference value from the {@link InstanceScope} of the given
	 * bundle.
	 * 
	 * @param bundle
	 *            the bundle ID to use
	 * @param key
	 *            the preference key
	 * @return the value of the preference or <code>null</code> if not existent
	 */
	public static String getPreferenceValue(String bundle, String key) {
		IEclipsePreferences preferences = context.getNode(bundle);
		return preferences.get(key, null);
	}

	/**
	 * Set the preference value from the {@link InstanceScope} of the given
	 * bundle.
	 * 
	 * @param bundle
	 *            the bundle ID to use
	 * @param key
	 *            the preference key
	 * @param value
	 *            the {@link String} representation of the value to set
	 * @return the value of the preference or <code>null</code> if not existent
	 */
	public static void setPreferenceValue(String bundle, String key, String value) {
		IEclipsePreferences preferences = context.getNode(bundle);
		preferences.put(key, value);
	}
}
