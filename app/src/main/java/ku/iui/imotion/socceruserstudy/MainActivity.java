package ku.iui.imotion.socceruserstudy;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aq.3gp";
    private static String videoFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aqv.mp4";

    private CanvasView queryCanvas;
    private MediaRecorder soundRecorder;
    private MediaRecorder videoRecorder;
    private Camera mCamera;
    private SurfaceView mPreview;
    private SurfaceHolder mHolder;
    private boolean mInitSuccesful;
    private Camera.Parameters parameters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queryCanvas = (CanvasView) findViewById(R.id.queryCanvas);
        mPreview = (SurfaceView) findViewById(R.id.surfaceView);
        mHolder = mPreview.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mInitSuccesful = false;
        //startRecording();
        //startVideoRecording();

        queryCanvas.setParent(this);
    }

    public void clearCanvas(View v) {
        queryCanvas.clearCanvas(true);
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
        //stopRecordingHelper();
        releaseMediaRecorder();
        releaseCamera();
        v.setClickable(false);
    }

    private void releaseMediaRecorder(){
        if (videoRecorder != null) {
            // clear recorder configuration
            videoRecorder.reset();
            // release the recorder object
            videoRecorder.release();
            videoRecorder = null;
            // Lock camera for later use i.e taking it back from MediaRecorder.
            // MediaRecorder doesn't need it anymore and we will release it if the activity pauses.
            mCamera.lock();
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            // release the camera for other applications
            mCamera.release();
            mCamera = null;
        }
    }

    private void startVideoRecording(){
        int cameraId = findFrontFacingCamera();

        if (cameraId >= 0) {
            // open the backFacingCamera
            // set a picture callback
            // refresh the preview
            mCamera = Camera.open(cameraId);
        }

        Log.e("videorec",mCamera.toString());

        // We need to make sure that our preview and recording video size are supported by the
        // camera. Query camera to find all the sizes and choose the optimal size given the
        // dimensions of our preview surface.
        parameters = mCamera.getParameters();
        //List<Camera.Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
        //List<Camera.Size> mSupportedVideoSizes = parameters.getSupportedVideoSizes();

        // Use the same size for recording profile.
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);

        // likewise for the camera object itself.
        parameters.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
        parameters.setPictureSize(profile.videoFrameWidth, profile.videoFrameHeight);
        mCamera.setParameters(parameters);

        try {
            // Requires API level 11+, For backward compatibility use {@link setPreviewDisplay}
            // with {@link SurfaceView}
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.e("videorec", "Surface texture is unavailable or unsuitable" + e.getMessage());
        }

        // BEGIN_INCLUDE (configure_media_recorder)
        videoRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        videoRecorder.setCamera(mCamera);

        // Step 2: Set sources
        videoRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        videoRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        videoRecorder.setProfile(profile);
        videoRecorder.setVideoFrameRate(15);

        videoRecorder.setOutputFile(videoFileName);
        // END_INCLUDE (configure_media_recorder)

        // Step 5: Prepare configured MediaRecorder
        try {
            videoRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d("videorec", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
        } catch (IOException e) {
            Log.d("videorec", "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
        }

        videoRecorder.start();
        mInitSuccesful = true;
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        Log.d("VideoRec","# of cams = " + numberOfCameras);
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

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            if(!mInitSuccesful)
                startVideoRecording();
        } catch (Exception e) {
            Log.e("videorec",e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        //mCamera.lock();

        // set preview size and make any resize, rotate or
        // reformatting changes here
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        parameters.setPictureSize(profile.videoFrameWidth, profile.videoFrameHeight);
        mCamera.setParameters(parameters);

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d("videorec", "Error starting camera preview: " + e.getMessage());
        }

        //mCamera.unlock();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        releaseMediaRecorder();
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseMediaRecorder();
        releaseCamera();
        queryCanvas.endConnection();
    }

    /*
    private void releaseVideoRecorder() {
        if (videoRecorder != null) {
            videoRecorder.reset(); // clear recorder configuration
            videoRecorder.release(); // release the recorder object
            videoRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }

    private void startVideoRecording() {
        releaseVideoRecorder();
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
        videoRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        videoRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        videoRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
        //videoRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //videoRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
        //videoRecorder.setVideoSize(640, 480);
        //videoRecorder.setVideoFrameRate(15);
        videoRecorder.setOutputFile(videoFileName);
        //videoRecorder.setMaxDuration(600000); // set maximum duration
        //videoRecorder.setMaxFileSize(50000000); // set maximum file size

        try {
            videoRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseVideoRecorder();
            Log.e("VideoRecording", e.getMessage());
        } catch (IOException e) {
            releaseVideoRecorder();
            Log.e("VideoRecording", e.getMessage());
        }

        Log.e("videorecord","opened");
        videoRecorder.start();
    }
    */
}
