package cd.markm.linkshrink;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class BitlyApiCall {
	private static final String TAG = BitlyApiCall.class.getSimpleName();
	
	private static final String BITLY_API_BASEURL = "http://api.bit.ly/v3/";
	private static final String BITLY_USERNAME = "linkshrink";
	private static final String BITLY_APIKEY = "R_7c68806f4d99c4751b18ab2aa2261875";

	private Context ctx;
	private String username;
	private String apikey;

	/**
	 * Create a new bit.ly API instance
	 * @param context context of the caller
	 */
	public BitlyApiCall(Context context) {
		ctx = context;
		username = Prefs.getApiLogin(context);
		apikey = Prefs.getApiKey(context);
	}

	/**
	 * Store the provided API credentials in persistent internal storage after
	 * validating first against the bit.ly API
	 * @param login bit.ly login
	 * @param apikey bit.ly API key
	 * @return true if credentials are valid & saved, false otherwise
	 */
	public boolean saveApiCredentials(String login, String apikey) {
		if (validateApiKey(login, apikey)) {
			this.username = login;
			this.apikey = apikey;
			Log.i(TAG, "Saved API credentials [l: "+login+", k:"+apikey+"]");
			return Prefs.saveApiCredentials(ctx, login, apikey);
		}
		else {
			Log.w(TAG, "API credentials failed validation");
			return false;
		}
	}
		
	/**
	 * Shrink a URL using the bit.ly API
	 * @param longURL URL to be shortened
	 * @return shortened URL
	 */
	public String shortenURL(String longURL) {
		String shortURL = longURL;

		String apiUrl = BITLY_API_BASEURL +"shorten?login="+ BITLY_USERNAME +
			"&apiKey="+ BITLY_APIKEY +"&format=json&longUrl="+ 
			URLEncoder.encode(longURL);

		// append user's API settings
		if (username != null && apikey != null)
			apiUrl += "&x_login="+ username +"&x_apiKey="+ apikey;

		// do the request
		try {
			JSONObject data = getApiData(apiUrl);
			if (data != null)
				shortURL = data.getString("url");
			Log.i(TAG, "Shortened to: "+shortURL);
		} catch (JSONException e) {
			Log.e(TAG, "JSON Exception :(", e);
			e.printStackTrace();
		}

		return shortURL;
	}

	/**
	 * verify that an API login & key pair are valid, using the API
	 * @param username
	 * @param apikey
	 * @return
	 */
	public static boolean validateApiKey(String username, String apikey) {
		String apiUrl = BITLY_API_BASEURL +"validate?login="+ BITLY_USERNAME +
			"&apiKey="+ BITLY_APIKEY +"&format=json&x_login="+ username +
			"&x_apiKey="+ apikey;
		
		boolean valid = false;
		
		try {
			JSONObject data = getApiData(apiUrl);
			if (data != null) {
				int result = data.getInt("valid");
				if (result == 1)
					valid = true;
			}
		} catch (JSONException e) {
			Log.e(TAG, "JSON Exception :(", e);
			e.printStackTrace();
		}
		
		return valid;
	}
	
	/**
	 * Retrieve the data portion of an API call, in JSON
	 * @param url the full API URL to use (GET)
	 * @return
	 * @throws JSONException
	 */
	private static JSONObject getApiData(String url) throws JSONException {
		HttpClient hc = new DefaultHttpClient();
		HttpGet hg = new HttpGet(url);
		try {
			HttpResponse response = hc.execute(hg);
			Log.i(TAG, "Got API response: "+ response.getStatusLine());

			if (response.getStatusLine().getStatusCode() == 200) {
				// turn the response into a string for the JSON parser
				InputStreamReader isr = new InputStreamReader(
						response.getEntity().getContent());
				BufferedReader br = new BufferedReader(isr);
				StringBuilder sb = new StringBuilder();
				
				String line = br.readLine();
				while (line != null) {
					sb.append(line);
					line = br.readLine();
				}
				
				// parse the JSON response
				JSONObject json = new JSONObject(sb.toString());
				if (json.getInt("status_code") == 200) {
					return json.getJSONObject("data");
				}
				else {
					Log.e(TAG, "JSON returned well-formed but status is "+
							json.getString("status_txt") +" ("+
							json.getInt("status_code") +")");
				}
			}
			else {
				Log.e(TAG, "Status code no good :(");
			}
		} catch (ClientProtocolException e) {
			Log.e(TAG, "Client Protocol Exception :(", e);
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "IO Exception :(", e);
			e.printStackTrace();
		}
		
		return null;
	}
}
