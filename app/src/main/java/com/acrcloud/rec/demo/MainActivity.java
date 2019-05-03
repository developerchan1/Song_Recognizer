package com.acrcloud.rec.demo;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.acrcloud.rec.sdk.ACRCloudConfig;
import com.acrcloud.rec.sdk.ACRCloudClient;
import com.acrcloud.rec.sdk.IACRCloudListener;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements IACRCloudListener {
	private static final int RECORD_REQUEST_CODE = 101;
	//NOTE: You can also implement IACRCloudResultWithAudioListener, replace "onResult(String result)" with "onResult(ACRCloudResult result)"

	private ACRCloudClient mClient;
	private ACRCloudConfig mConfig;
	
	private TextView mResult;
	private ImageView image;
	
	private boolean mProcessing = false;
	private boolean initState = false;
	
	private String path = "";

	private long startTime = 0;
	private long stopTime = 0;
	public Vibrator v;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		image = findViewById(R.id.image);
		Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in);
		image.startAnimation(animation);

		v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CALENDAR)
				!= PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
					Manifest.permission.RECORD_AUDIO)) {
			} else {
				// No explanation needed; request the permission
				ActivityCompat.requestPermissions(MainActivity.this,
						new String[]{Manifest.permission.RECORD_AUDIO},
						RECORD_REQUEST_CODE);


			}
			// Permission is not granted
		}

		path = Environment.getExternalStorageDirectory().toString()
				+ "/acrcloud/model";
		
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}

		mResult = (TextView) findViewById(R.id.result);
		
		Button startBtn = (Button) findViewById(R.id.start);

		findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				start();
			}
		});


        this.mConfig = new ACRCloudConfig();
        this.mConfig.acrcloudListener = this;
        
        // If you implement IACRCloudResultWithAudioListener and override "onResult(ACRCloudResult result)", you can get the Audio data.
        //this.mConfig.acrcloudResultWithAudioListener = this;
        
        this.mConfig.context = this;
        this.mConfig.host = "identify-ap-southeast-1.acrcloud.com";
        this.mConfig.dbPath = path; // offline db path, you can change it with other path which this app can access.
        this.mConfig.accessKey = "81e9c0908a281e9b0b8b14f041671ba5";
        this.mConfig.accessSecret = "EfwbwmfzJvpCok7Q7f6F1O2l8xooByl2D2Pm7Mas";
        this.mConfig.protocol = ACRCloudConfig.ACRCloudNetworkProtocol.PROTOCOL_HTTPS; // PROTOCOL_HTTP
        this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_REMOTE;
        //this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_LOCAL;
        //this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_BOTH;

        this.mClient = new ACRCloudClient();
        // If reqMode is REC_MODE_LOCAL or REC_MODE_BOTH,
        // the function initWithConfig is used to load offline db, and it may cost long time.
        this.initState = this.mClient.initWithConfig(this.mConfig);
        if (this.initState) {
//            this.mClient.startPreRecord(3000); //start prerecord, you can call "this.mClient.stopPreRecord()" to stop prerecord.
        }
	}

	
	public void start() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
		} else {
			v.vibrate(500);
		}
        if (!this.initState) {
            Toast.makeText(this, "init error", Toast.LENGTH_SHORT).show();
            return;
        }
		
		if (!mProcessing) {
			mProcessing = true;
			mResult.setText("Please Wait");
			if (this.mClient == null || !this.mClient.startRecognize()) {
				mProcessing = false;
				mResult.setText("start error!");
			}
            startTime = System.currentTimeMillis();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    // Old api
	@Override
	public void onResult(String result) {	
		if (this.mClient != null) {
			this.mClient.cancel();
			mProcessing = false;
		} 
		
		String tres = "\n";
		
		try {
		    JSONObject j = new JSONObject(result);
		    JSONObject j1 = j.getJSONObject("status");
		    int j2 = j1.getInt("code");
		    if(j2 == 0){
		    	Intent itt= new Intent(MainActivity.this, Description.class);
		    	itt.putExtra("Result",result);
		    	startActivity(itt);
		    	tres = "Succesful Recognize the Song";
		    }else{
		    	tres = "Can't Recognize the Song";
		    }
		} catch (JSONException e) {
			tres = result;
		    e.printStackTrace();
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
		} else {
			//deprecated in API 26
			v.vibrate(500);
		}
		mResult.setText(tres);	
	}

	@Override
	public void onVolumeChanged(double volume) {

	}
	
	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
        Log.e("MainActivity", "release");
        if (this.mClient != null) {
        	this.mClient.release();
        	this.initState = false;
        	this.mClient = null;
        }
    } 
}
