package cd.markm.linkshrink;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Prefs {
	public static enum Action { RESHARE, CLIPBOARD };

	private static final String PREF_NAME = "BitlyPrefs";
	private static final String PREF_APIKEY = "apikey";
	private static final String PREF_USERNAME = "username";
	private static final String PREF_ACTION = "action";

	private Prefs() {
	}
	
	public static String getApiLogin(Context ctx) {
		SharedPreferences prefs = ctx.getSharedPreferences(PREF_NAME, 0);
		return prefs.getString(PREF_USERNAME, null);
	}

	public static String getApiKey(Context ctx) {
		SharedPreferences prefs = ctx.getSharedPreferences(PREF_NAME, 0);
		return prefs.getString(PREF_APIKEY, null);
	}
	
	public static Action getShrinkAction(Context ctx) {
		SharedPreferences prefs = ctx.getSharedPreferences(PREF_NAME, 0);
		String action = prefs.getString(PREF_ACTION, Action.RESHARE.toString());
		
		for (Action a : Action.values()) {
			if (a.toString().equals(action.toString()))
				return a;
		}
		
		// should never get here
		return Action.RESHARE;
	}

	/**
	 * Saves API credentials but does NOT validate.
	 * @param ctx app context
	 * @param login
	 * @param apikey
	 */
	public static boolean saveApiCredentials(Context ctx, 
			String login, String apikey) {
		SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, 0);
		Editor editor = sp.edit();
		editor.putString(PREF_USERNAME, login);
		editor.putString(PREF_APIKEY, apikey);
		return editor.commit();
	}

	/**
	 * Saves post-shrink action
	 * @param ctx
	 * @param action
	 */
	public static boolean saveShrinkAction(Context ctx, Action action) {
		SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, 0);
		Editor editor = sp.edit();
		editor.putString(PREF_ACTION, action.toString());
		return editor.commit();
	}
}
