package ku.iui.imotion.socceruserstudy;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by ozymaxx on 21.07.2016.
 */

public class ClearCanvasTask extends AsyncTask<Object,Void,Void> {
    private DataOutputStream out;

    public ClearCanvasTask(DataOutputStream out) {
        this.out = out;
    }

    @Override
    protected Void doInBackground(Object... objects) {
        try {
            String directive = (String) objects[0];
            long nano = (Long) objects[1];
            long milli = (Long) objects[2];
            out.writeChars("("+directive+","+nano+","+milli+")");
        } catch (IOException e) {
            Log.e("StationConn",e.getMessage());
        } catch (NullPointerException e) {
            Log.e("StationConn","Connection denied - " + e.getMessage());
        }

        return null;
    }
}
