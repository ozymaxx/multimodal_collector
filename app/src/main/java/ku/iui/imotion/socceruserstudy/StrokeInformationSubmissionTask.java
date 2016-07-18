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

public class StrokeInformationSubmissionTask extends AsyncTask<String,Void,Void> {
    private DataOutputStream out;

    public StrokeInformationSubmissionTask(DataOutputStream out) {
        this.out = out;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            out.writeChars("("+strings[0]+")");
        } catch (IOException e) {
            Log.e("StationConn",e.getMessage());
        } catch (NullPointerException e) {
            Log.e("StationConn","Connection denied - " + e.getMessage());
        }

        return null;
    }
}