package course.elec4010b.lab_4_4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class NetworkConnectionActivity extends Activity {
	public static final String TAG_HOST = "tagHost";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
        setContentView(R.layout.network_connection);

        Button btnOkay   = (Button) findViewById(R.id.btnOkay);
        btnOkay.setOnClickListener(new OnClickListener() {
        	public void onClick(View view) {
    			EditText etHost = (EditText) findViewById(R.id.etHost);
    			String host = etHost.getText().toString();
    			
        		Intent intent = new Intent();
        		intent.putExtra(TAG_HOST, host);
        		
        		setResult(RESULT_OK, intent);
        		finish();
        	}
        });
        
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new OnClickListener() {
        	public void onClick(View view) {
        		setResult(RESULT_CANCELED);
        		finish();
        	}
        });
    }
}
