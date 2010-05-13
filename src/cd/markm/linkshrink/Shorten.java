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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class Shorten extends Activity {
	private final String TAG = "Shorten";
	
	// TODO add user's override into the config UI
	private final String BITLY_USERNAME = "linkshrink";
	private final String BITLY_APIKEY = "R_7c68806f4d99c4751b18ab2aa2261875";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        if (Intent.ACTION_SEND.equals(intent.getAction())
				&& "text/plain".equals(intent.getType())) {
        	String longURL = intent.getStringExtra(Intent.EXTRA_TEXT);
        	Log.d(TAG, "Shortening URL: "+longURL);

        	// TODO pull all this out into separate threads & classes
        	String apiUrl = "http://api.bit.ly/v3/shorten?login="+
        		BITLY_USERNAME +"&apiKey="+ BITLY_APIKEY +
        		"&format=json&longUrl="+ URLEncoder.encode(longURL);
        	
        	HttpClient hc = new DefaultHttpClient();
        	HttpGet hg = new HttpGet(apiUrl);
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
					
					JSONObject json = new JSONObject(sb.toString());
					if (json.getInt("status_code") == 200) {
						JSONObject data = json.getJSONObject("data");
	
						String shortURL = data.getString("url");
						Log.i(TAG, "Shortened to: "+shortURL);
			
						// TODO report this back to the UI for copy & paste
						Toast.makeText(getApplicationContext(), shortURL, 
								Toast.LENGTH_LONG).show();
					}
					else {
						Log.e(TAG, "JSON returned well-formed but status is "+
								json.getString("status_txt") +" ("+
								json.getInt("status_code") +")");
					}
				}
				else {
					Toast.makeText(getApplicationContext(), 
							"Error while contacting API", 
							Toast.LENGTH_LONG).show();
					Log.e(TAG, "Status code no good :(");
				}
			} catch (ClientProtocolException e) {
				Log.e(TAG, "Client Protocol Exception :(", e);
				e.printStackTrace();
			} catch (IOException e) {
				Log.e(TAG, "IO Exception :(", e);
				e.printStackTrace();
			} catch (JSONException e) {
				Log.e(TAG, "JSON Exception :(", e);
				e.printStackTrace();
			}
        }
        else {
        	Log.e(TAG, "Unknown intent type / data.");
        }
 
    }

}
