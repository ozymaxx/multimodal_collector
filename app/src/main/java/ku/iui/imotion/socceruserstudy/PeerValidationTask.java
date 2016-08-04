package ku.iui.imotion.socceruserstudy;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by ozymaxx on 22.07.2016.
 */

public class PeerValidationTask extends AsyncTask<Void,Void,Socket> {
    final static int PORT = 3440;

    private DataInputStream in;
    private ConnectionStatusActivity delegatedActivity;

    private ServerSocket serverSocket;

    public PeerValidationTask(ConnectionStatusActivity delegatedActivity) {
        this.delegatedActivity = delegatedActivity;
    }

    @Override
    protected Socket doInBackground(Void... voids) {
        try {
            serverSocket = new ServerSocket(PORT);

            Socket client = serverSocket.accept();

            return client;
        } catch (IOException e) {
            Log.e("ExperimenterConn",e.getMessage());
            return null;
        }
    }

    protected void onPostExecute(Socket result) {
        if (result != null) {
            synchronized (delegatedActivity) {
                delegatedActivity.bringSocket(result);
            }
        }
    }
}
