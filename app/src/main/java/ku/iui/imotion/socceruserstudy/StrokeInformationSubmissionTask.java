package ku.iui.imotion.socceruserstudy;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by ozymaxx on 17.07.2016.
 */

public class StrokeInformationSubmissionTask extends AsyncTask<Object,Void,Void> {
    private DataOutputStream out;

    public StrokeInformationSubmissionTask(DataOutputStream out) {
        this.out = out;
    }

    @Override
    protected Void doInBackground(Object... objects) {
        try {
            int width = (int) ((float) objects[1]);
            int r = (Integer) objects[2];
            int g = (Integer) objects[3];
            int b = (Integer) objects[4];
            int a = (Integer) objects[5];
            long timen = (Long) objects[6];
            long timem = (Long) objects[7];
            out.writeChars("("+objects[0]+","+width+","+r+","+g+","+b+","+a+","+timen+","+timem+")");
        } catch (IOException e) {
            Log.e("StationConn",e.getMessage());
        } catch (NullPointerException e) {
            Log.e("StationConn","Connection denied - " + e.getMessage());
        }

        return null;
    }
}
