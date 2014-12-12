package com.example.ronnie.inf133_android;

import java.util.List;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


//citation: http://android-er.blogspot.com/2010/08/detect-rotation-around-x-y-z-axis-using.html //
public class MainActivity extends Activity implements SensorEventListener{

	TextView textviewX, textviewY, textviewZ, songTitle;
	private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private MediaPlayer mp;
    private boolean playingSound;
    AssetFileDescriptor afd;
    private float[] Direction1 = new float[3];
    private float[] Direction2 = new float[3];
    boolean setEqual = false;

    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;

    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textviewX = (TextView)findViewById(R.id.textViewX);
        textviewY = (TextView)findViewById(R.id.textViewY);
        textviewZ = (TextView)findViewById(R.id.textViewZ);
        songTitle = (TextView)findViewById(R.id.song_playing);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        
        mp = new MediaPlayer();
		
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLastAccelerometerSet = false;
        mLastMagnetometerSet = false;
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        
     
        
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            Log.i("OrientationTestActivity", String.format("Orientation: %f, %f, %f",
                                                           mOrientation[0], mOrientation[1], mOrientation[2]));
            textviewX.setText("X: " + mOrientation[0]);
            textviewY.setText("Y " + mOrientation[1]);
            textviewZ.setText("Z: " + mOrientation[2]);
            setMusic(mOrientation[0],mOrientation[1],mOrientation[2]);
            Direction2[0] = mOrientation[0];
            Direction2[1] = mOrientation[1];
            Direction2[2] = mOrientation[2];
            
            if (!setEqual){
            	setEqual = true;
            	 Direction1[0] = mOrientation[0];
                 Direction1[1] = mOrientation[1];
                 Direction1[2] = mOrientation[2];
            }
            
        }
        //compareDirection(Direction1, Direction2);
     
    }
    
   synchronized void playAudio(AssetFileDescriptor afd){
	   if(mp.isPlaying()){
		   return;
	   }
	   mp.reset();
	   try{
		   mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
		   mp.prepare();
	   }
	   catch(Exception e){
		   Log.d("playAudio", "Exception:"+e.getStackTrace()[0].toString()+" afd: " +afd.toString());
	   }
	   mp.start();
   }
    
   public boolean compareDirection(float[] Direction1, float[] Direction2){
	   if(Math.abs(Direction1[0]-Direction2[0]) > 0.01){
		   Direction1[0] = Direction2[0];
           Direction1[1] = Direction2[1];
           Direction1[2] = Direction2[2];
           onResume();
		   return true;
	   }
	   else if(Math.abs(Direction1[1]-Direction2[1]) > 0.01){
		   Direction1[0] = Direction2[0];
           Direction1[1] = Direction2[1];
           Direction1[2] = Direction2[2];
           onResume();
		   return true;
	   }
	   else if(Math.abs(Direction1[2]-Direction2[2]) > 0.01){
		   Direction1[0] = Direction2[0];
           Direction1[1] = Direction2[1];
           Direction1[2] = Direction2[2];
           onResume();
		   return true;
	   }
	   onPause();
	   return false;
   }
   
   public void setMusic(float x, float y, float z){
	   
	   
	  // if (compareDirection(Direction1,Direction2)){
		   //playingSound = true;
		   //tilt facedown
		   if ((y >-.5 && y <.5) && ((z >-3.5 && z <-2.5) ||(z >2.5 && z <3.5) )){
			   afd = getApplicationContext().getResources().openRawResourceFd(R.raw.piano_1);
			   songTitle.setText("Piano 1");
			   playAudio(afd);
			   playingSound = false;
			//   onPause();
			   
		   }
		   //tilt up
		   else if ((y >-.5 && y <.5) && (z >-.5 && z <.5) ){
			   afd = getApplicationContext().getResources().openRawResourceFd(R.raw.glockenspiel_5);
			   songTitle.setText("Glockenspiel 5");
			   playAudio(afd);
			   playingSound = false;
			//   onPause();
			   
		   }
		   //tilt left
		   else if ((z >-2 && z <-1) ){
			   afd = getApplicationContext().getResources().openRawResourceFd(R.raw.harp_14);
			   songTitle.setText("Harp 14");
			   playAudio(afd);
			   playingSound = false;
			//   onPause();
			   
		   }
		   //tilt forward
		   else if ((y >-2 && y <-1)){
			   afd = getApplicationContext().getResources().openRawResourceFd(R.raw.kalimba_2);
			   songTitle.setText("Kalimba 2");
			   playAudio(afd);
			   playingSound = false;
			   onPause();
		   }
		   //tilt right
		   else if ( (z <2 && z >1) ){
			   afd = getApplicationContext().getResources().openRawResourceFd(R.raw.music_box_1);
			   songTitle.setText("Music Box");
			   playAudio(afd);
			   playingSound = false;
			  // onPause();
			   
		   }
		   else{
			   songTitle.setText("No Sound");
			   playingSound = false;
			   onResume();
		   }
		   
		   onResume();

   }
}
