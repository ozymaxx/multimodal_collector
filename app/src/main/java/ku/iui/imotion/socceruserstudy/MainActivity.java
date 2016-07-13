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
}
