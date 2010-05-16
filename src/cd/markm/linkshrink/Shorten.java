package cd.markm.linkshrink;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

			// if the URL hasn't changed, show an error
			if (shortURL.equals(longURL))
				Toast.makeText(ctx, getString(R.string.link_shortened_failure), 
						Toast.LENGTH_LONG).show();

			// pass it straight through regardless of the success of the 
			// request so we don't block the use of a URL when something fails 
			// internally - the original URL still gets passed back
			Intent passthru = new Intent();
			passthru.setAction(Intent.ACTION_SEND);
			passthru.setType("text/plain");
			passthru.putExtra(Intent.EXTRA_TEXT, shortURL);
			startActivity(passthru);
			
			handler.sendEmptyMessage(0);
			progress.dismiss();
		}
	}
 }
