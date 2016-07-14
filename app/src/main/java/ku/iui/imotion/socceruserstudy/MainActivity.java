package ku.iui.imotion.socceruserstudy;

import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aq.3gp";

    private CanvasView queryCanvas;
    private MediaRecorder soundRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queryCanvas = (CanvasView) findViewById(R.id.queryCanvas);
        startRecording();
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
            Log.e("AudioRecording", "prepare() failed");
        }

        soundRecorder.start();
    }

    public void stopRecording(View v) {
        soundRecorder.stop();
        soundRecorder.release();
        soundRecorder = null;

        v.setClickable(false);
    }


}
