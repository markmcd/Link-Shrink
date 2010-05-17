package cd.markm.linkshrink;

import cd.markm.linkshrink.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Configure extends Activity {
	private BitlyApiCall api;
   
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        api = new BitlyApiCall(this);
        
        // load up any saved values
        String login = api.getApiLogin();
        String apikey = api.getApiKey();
        if (login != null && apikey != null) {
        	EditText loginInput = (EditText) 
        				findViewById(R.id.input_bitlyusername);
        	loginInput.setText(login);
        	EditText keyInput = (EditText) 
        				findViewById(R.id.input_bitlyapikey);
        	keyInput.setText(apikey);
        }
        
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
				
				// TODO run this in a thread w/ProgressDialog
				if (api.saveApiCredentials(username, apikey)) {
					Toast.makeText(getApplicationContext(), 
							R.string.bitly_validation_succeed, 
							Toast.LENGTH_LONG).show();
					finish();
				}
				else {
					Toast.makeText(getApplicationContext(), 
							R.string.bitly_validation_failed, 
							Toast.LENGTH_LONG).show();
				}
			}
		});

        
        
    }
}