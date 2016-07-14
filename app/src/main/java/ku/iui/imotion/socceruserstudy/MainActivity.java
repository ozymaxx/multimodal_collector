package ku.iui.imotion.socceruserstudy;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aq.3gp";
    private static String videoFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aqv.mp4";

    private CanvasView queryCanvas;
    private MediaRecorder soundRecorder;
    private MediaRecorder videoRecorder;
    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queryCanvas = (CanvasView) findViewById(R.id.queryCanvas);
        startRecording();
        startVideoRecording();
    }

    public void clearCanvas(View v) {
        queryCanvas.clearCanvas();
    }

    public void changeColor(View v) {
        int vid = v.getId();

        switch (vid) {
            case R.id.highlightBlue:
                queryCanvas.changeModeAndColor(3);
                break;
            case R.id.highlightPurple:
                queryCanvas.changeModeAndColor(4);
                break;
            case R.id.highlightRed:
                queryCanvas.changeModeAndColor(1);
                break;
            case R.id.highlightYellow:
                queryCanvas.changeModeAndColor(2);
                break;
            case R.id.highlightBrown:
                queryCanvas.changeModeAndColor(5);
                break;
            case R.id.drawingMode:
                queryCanvas.changeModeAndColor(6);
                break;
        }
    }

    private void startRecording() {
        soundRecorder = new MediaRecorder();
        soundRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        soundRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        soundRecorder.setOutputFile(mFileName);
        soundRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            soundRecorder.prepare();
        } catch (IOException e) {
            stopRecordingHelper();
            Log.e("AudioRecording", "prepare() failed");
        }

        soundRecorder.start();
    }

    private void stopRecordingHelper() {
        soundRecorder.stop();
        soundRecorder.release();
        soundRecorder = null;
    }

    public void stopRecording(View v) {
        stopRecordingHelper();
        releaseVideoRecorder();
        v.setClickable(false);
    }

    private void releaseVideoRecorder() {
        if (videoRecorder != null) {
            videoRecorder.reset(); // clear recorder configuration
            videoRecorder.release(); // release the recorder object
            videoRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    private void startVideoRecording() {
        int cameraId = findFrontFacingCamera();

        if (cameraId >= 0) {
            // open the backFacingCamera
            // set a picture callback
            // refresh the preview
            mCamera = Camera.open(cameraId);
        }

        videoRecorder = new MediaRecorder();
        mCamera.unlock();
        videoRecorder.setCamera(mCamera);
        //videoRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        videoRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //videoRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
        Log.d("videorecord","opened");
        videoRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        videoRecorder.setOutputFile(videoFileName);
        //videoRecorder.setMaxDuration(600000); // set maximum duration
        //videoRecorder.setMaxFileSize(50000000); // set maximum file size

        try {
            videoRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseVideoRecorder();
            Log.e("VideoRecording", "prepare() failed");
        } catch (IOException e) {
            releaseVideoRecorder();
            Log.e("VideoRecording", "cam i/o failed");
        }
    }
}
