package ku.iui.imotion.socceruserstudy;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by ozymaxx on 22.07.2016.
 */

public class PeerValidationTask extends AsyncTask<Void,Void,Boolean> {
    private static String READY = "PEERSREADY";

    private DataInputStream in;
    private ConnectionStatusActivity delegatedActivity;

    public PeerValidationTask(DataInputStream in,ConnectionStatusActivity delegatedActivity) {
        this.in = in;
        this.delegatedActivity = delegatedActivity;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        String receivedContent = "";

        try {
            char ch = in.readChar();
            receivedContent += ch;

            while (ch != ')') {
                ch = in.readChar();
                receivedContent += ch;
            }

            receivedContent = receivedContent.substring(1,receivedContent.length()-1);

            if (receivedContent.equals(READY)) {
                return true;
            }
            else {
                return false;
            }
        } catch (IOException e) {
            Log.e("PeerConn",e.getMessage());
            return false;
        }
    }

    protected void onPostExecute(Boolean result) {
        synchronized (delegatedActivity) {
            delegatedActivity.updatePeerStatus(result);
        }
    }
}
