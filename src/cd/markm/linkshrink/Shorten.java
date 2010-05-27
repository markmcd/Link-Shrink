package cd.markm.linkshrink;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.util.Log;
import android.widget.Toast;

public class Shorten extends Activity {
	private static final String TAG = Shorten.class.getSimpleName();
	
	private ProgressDialog progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        if (Intent.ACTION_SEND.equals(intent.getAction())
				&& "text/plain".equals(intent.getType())) {
        	
        	String longURL = intent.getStringExtra(Intent.EXTRA_TEXT);

        	Log.d(TAG, "Shortening URL: "+longURL);

        	progress = ProgressDialog.show(this, getString(R.string.app_name), 
        			getString(R.string.shortening), true);

        	new RequestThread(this, longURL).start();
        	
        }
        else {
        	Log.e(TAG, "Unknown intent type / data.");
        }
 
    }

    // thread message handler
	private Handler handler = new Handler() {
         @Override
         public void handleMessage(Message msg) {
        	 super.handleMessage(msg);
        	 Bundle b = msg.getData();
        	 String output = b.getString("message");
        	 if (b.getBoolean("success")) {
        		 Log.i(TAG, "Link shortened successfully");
        		 Toast.makeText(getApplicationContext(), output, 
        				 Toast.LENGTH_SHORT).show();
        	 }
        	 else {
        		 Log.i(TAG, "Link shortening failed");
        		 Toast.makeText(getApplicationContext(), output, 
        				 Toast.LENGTH_LONG).show();
        	 }
        	 
        	 finish();
         }
	};
	
	private class RequestThread extends Thread {
		private Context ctx;
		private String longURL;
		
		public RequestThread(Context ctx, String longURL) {
			this.ctx = ctx;
			this.longURL = longURL;
		}
		
		public void run() {
			BitlyApiCall api = new BitlyApiCall(ctx);
			String shortURL = api.shortenURL(longURL) ;
			boolean success = true;
			String message = "";

			// if the URL hasn't changed, show an error
			if (shortURL.equals(longURL)) {
				message =  getString(R.string.link_shortened_failure); 
				success = false;
			}

			switch (Prefs.getShrinkAction(ctx)) {
			case CLIPBOARD:
				// copy to clipboard & let the user know
				ClipboardManager cb = (ClipboardManager) 
						ctx.getSystemService(CLIPBOARD_SERVICE);
				cb.setText(shortURL);
				message = getString(R.string.link_copied);
				break;
			case RESHARE:
			default:
				// pass it straight through regardless of the success of the 
				// request so we don't block the use of a URL when something 
				// fails internally - the original URL still gets passed back
				Intent passthru = new Intent();
				passthru.setAction(Intent.ACTION_SEND);
				passthru.setType("text/plain");
				passthru.putExtra(Intent.EXTRA_TEXT, shortURL);
				startActivity(passthru);
				message = getString(R.string.link_shortened_success);
				break;
			}
			
			Bundle b = new Bundle();
			b.putBoolean("success", success);
			b.putString("message", message);
			Message msg = new Message();
			msg.setData(b);
			handler.sendMessage(msg);
			progress.dismiss();
		}
	}
 }
