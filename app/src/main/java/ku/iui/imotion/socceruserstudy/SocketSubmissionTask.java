package ku.iui.imotion.socceruserstudy;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by ozymaxx on 17.07.2016.
 */

public class SocketSubmissionTask extends AsyncTask<Object,Void,Socket> {
    private CanvasView delegate;
    private ConnectionStatusActivity delegatedActivity;

    /*
    public SocketSubmissionTask(CanvasView delegate) {
        this.delegate = delegate;
    }
    */

    public SocketSubmissionTask(ConnectionStatusActivity delegatedActivity) {
        this.delegatedActivity = delegatedActivity;
    }

    @Override
    protected Socket doInBackground(Object... objects) {
        String ip = (String) objects[0];
        int port = (Integer) objects[1];

        try {
            return new Socket(ip,port);
        } catch (IOException e) {
            Log.e("StationConn",e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(Socket socket) {
        if (delegatedActivity != null) {
            delegatedActivity.bringSocket(socket);
        }
        else {
            Log.e("StationConn","Delegation problem");
        }
    }
}
