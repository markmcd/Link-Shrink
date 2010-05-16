package cd.markm.linkshrink;

import cd.markm.linkshrink.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Configure extends Activity {
   
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
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
				BitlyApiCall api = new BitlyApiCall(getApplicationContext());
				if (api.saveApiCredentials(username, apikey)) {
					Toast.makeText(getApplicationContext(), 
							R.string.bitly_validation_succeed, 
							Toast.LENGTH_LONG).show();
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