package org.un.myworld.data.sync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
/**
 * 
 * @author kinyua
 * @description - Test activity to mimic working of the service.
 */
public class Main extends Activity {
	private static final String TAG = "com.data.sync";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    //    setContentView(R.layout.main);
 
      //  Button btnStatus = (Button) findViewById(R.id.btn_check);
 
        /**
         * Testing commence..
         * */
    /*    btnStatus.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View v) {
            	startService(new Intent(Main.this,Sync.class));
            	Log.d(TAG, "Service Started");
            }
 
        });*/
 
    }
}
