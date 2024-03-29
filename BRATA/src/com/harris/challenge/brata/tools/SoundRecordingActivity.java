package com.harris.challenge.brata.tools;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.harris.challenge.brata.R;

/**
 * This class handles the recording of audio files.
 * 
 * Note that it requires the WRITE_EXTERNAL_STORAGE and RECORD_AUDIO permissions.
 * 
 * Code based on example code from http://developer.android.com/guide/topics/media/audio-capture.html
 * Used under CC BY 2.5 license: http://creativecommons.org/licenses/by/2.5/
 * 
 * @author Jeremy
 *
 */
public class SoundRecordingActivity extends Activity {
	/**
	 * Tag to track errors in LogCat
	 */
	private static final String LOG_TAG = "SoundRecordingActivity";
	
	/**
	 * Directory where recordings are kept
	 */
	private static String mFolder = null;
	
	/**
	 * Handle to the filename text view
	 */
	private TextView mFileName = null;
	
	/**
	 * Recording state (true means ready to record, false means recording)
	 */
	private boolean mStartRecording = true;
	
	/**
	 * Playback state (true means ready to play, false means playing)
	 */
	private boolean mStartPlaying = true;
	
	/**
	 * Handle to the record button
	 */
	private Button mRecordButton = null;
	
	/**
	 * Handle to the MediaRecorder
	 */
    private MediaRecorder mRecorder = null;

    /**
     * Handle to the play button
     */
    private Button mPlayButton = null;
    
    /**
     * Handle to the MediaPlayer
     */
    private MediaPlayer mPlayer = null;
    
    /**
     * Constructor. Sets the directory for recordings storage
     */
    public SoundRecordingActivity() {
    	mFolder = Environment.getExternalStorageDirectory().getAbsolutePath();
    	mFolder += "/brata/";
    	
    	File brataDir = new File(mFolder);
    	brataDir.mkdirs();
    }
    
    /**
     * Handle the record start/stop
     * @param start True to start recording, false to stop.
     */
    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    /**
     * Handle the playback start/stop
     * @param start True to start playback, false to stop.
     */
    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    /**
     * Play the audio
     */
    private void startPlaying() {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(playCompleted);
        try {
            mPlayer.setDataSource(mFolder + mFileName.getText().toString());
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    /**
     * Stop playing the audio
     */
    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    /**
     * Start recording
     */
    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFolder + mFileName.getText().toString());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    /**
     * Stop recording
     */
    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        // Set the content view (can call findViewById after this)
        setContentView(R.layout.activity_sound);

        // Need filename textview to get the filename text
        mFileName = (TextView)findViewById(R.id.TextFileName);
        // Need the record/play buttons to add the listeners and set the text
        mRecordButton = (Button)findViewById(R.id.RecordButton);
        mPlayButton = (Button)findViewById(R.id.PlayButton);
        
        // Set the listeners for clicks
        mRecordButton.setOnClickListener(record);
        mPlayButton.setOnClickListener(play);
        
    }

    @Override
    public void onPause() {
        super.onPause();
        // Free up the resources
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
    
    /**
     * Listener for when the user clicks the record button
     */
	OnClickListener record = new OnClickListener() {
		public void onClick(View v) {
			onRecord(mStartRecording);
            if (mStartRecording) {
                mRecordButton.setText("Stop recording");
            } else {
            	mRecordButton.setText("Start recording");
            }
            mStartRecording = !mStartRecording;
		}
	};
	
    /**
     * Listener for when the user clicks the play button
     */
	OnClickListener play = new OnClickListener() {
		public void onClick(View v) {
            onPlay(mStartPlaying);
            if (mStartPlaying) {
                mPlayButton.setText("Stop playing");
            } else {
            	mPlayButton.setText("Start playing");
            }
            mStartPlaying = !mStartPlaying;
		}
	};
	
	/**
	 * Listener for when playback completed to set button in correct state
	 */
	OnCompletionListener playCompleted = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer arg0) {
			mStartPlaying = true;
			mPlayButton.setText("Start playing");
		}
		
	};
}
