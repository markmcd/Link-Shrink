package cd.markm.linkshrink;

import cd.markm.linkshrink.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class Configure extends Activity {
	private BitlyApiCall api;
   
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        api = new BitlyApiCall(this);
        
        // load up any saved values
        String login = Prefs.getApiLogin(this);
        String apikey = Prefs.getApiKey(this);
        if (login != null && apikey != null) {
        	EditText loginInput = (EditText) 
        				findViewById(R.id.input_bitlyusername);
        	loginInput.setText(login);
        	EditText keyInput = (EditText) 
        				findViewById(R.id.input_bitlyapikey);
        	keyInput.setText(apikey);
        }
        
        Prefs.Action action = Prefs.getShrinkAction(this);
		RadioButton radio = (RadioButton) findViewById(R.id.ReshareRadio);
        if (action == Prefs.Action.CLIPBOARD)
        	radio = (RadioButton) findViewById(R.id.CopyRadio);
        radio.setChecked(true);
        
        // wire up the 'save' button
        Button saveButton = (Button) findViewById(R.id.savebutton);
        saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// pull values out of the UI
				EditText et = (EditText) findViewById(R.id.input_bitlyusername);
				String username = et.getText().toString();
				et = (EditText) findViewById(R.id.input_bitlyapikey);
				String apikey = et.getText().toString();
				
				Context ctx = getApplicationContext();

				// only validate & save if the login/key have been provided
				// and have changed (compared to what's already in prefs)
				if ((username.length() > 0 || apikey.length() > 0) &&
						(!(username.equals(Prefs.getApiLogin(ctx)) && 
						apikey.equals(Prefs.getApiKey(ctx))))) {
					// TODO run this in a thread w/ProgressDialog
					if (!api.saveApiCredentials(username, apikey)) {
						Toast.makeText(getApplicationContext(), 
								R.string.bitly_validation_failed, 
								Toast.LENGTH_LONG).show();
						return;
					}
				}
				
				// save the post-shrink action
				// default to re-sharing
				Prefs.Action a = Prefs.Action.RESHARE;
				RadioButton radio = (RadioButton) findViewById(R.id.CopyRadio);
				if (radio.isChecked())
					a = Prefs.Action.CLIPBOARD;
				Prefs.saveShrinkAction(ctx, a);
				Toast.makeText(getApplicationContext(), R.string.prefs_saved, 
						Toast.LENGTH_LONG).show();
				finish();

			}
		});

        
        
    }
}