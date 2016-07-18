package ku.iui.imotion.socceruserstudy;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by ozymaxx on 16.07.2016.
 */

public class PointSubmissionTask extends AsyncTask<Float,Void,Void> {
    private DataOutputStream out;

    public PointSubmissionTask(DataOutputStream out) {
        this.out = out;
    }

    @Override
    protected Void doInBackground(Float... floats) {
        float x = floats[0];
        float y = floats[1];
        long timestamp = (long) ((float) floats[2]);

        try {
            out.writeChars("("+x+","+y+","+timestamp+")");
        } catch (IOException e) {
            Log.e("StationConn",e.getMessage());
        } catch (NullPointerException e) {
            Log.e("StationConn","Connection denied - " + e.getMessage());
        }

        return null;
    }
}
