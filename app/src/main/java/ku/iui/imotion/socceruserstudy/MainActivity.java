package ku.iui.imotion.socceruserstudy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private CanvasView queryCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queryCanvas = (CanvasView) findViewById(R.id.queryCanvas);
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
}
