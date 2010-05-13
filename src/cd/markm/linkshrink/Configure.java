package cd.markm.linkshrink;

import cd.markm.linkshrink.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class Configure extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toast.makeText(getApplicationContext(), "Config", Toast.LENGTH_SHORT)
        	.show();
    }
}