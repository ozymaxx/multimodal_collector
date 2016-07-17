package ku.iui.imotion.socceruserstudy;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by ozymaxx on 17.07.2016.
 */

public class StrokeInformationSubmissionTask extends AsyncTask<String,Void,Void> {
    private InetAddress addr;
    private DatagramSocket socket;
    private int port;
    private String ip;

    public StrokeInformationSubmissionTask(InetAddress addr, DatagramSocket socket, int port, String ip) {
        this.addr = addr;
        this.socket = socket;
        this.port = port;
        this.ip = ip;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            byte[] buffer = ("("+strings[0]+")").getBytes();
            socket.send(new DatagramPacket(buffer,buffer.length,addr,port));
        } catch (IOException e) {
            Log.e("StationConn",e.getMessage());
        } catch (NullPointerException e) {
            Log.e("StationConn","Connection denied - " + e.getMessage());
        }

        return null;
    }
}
