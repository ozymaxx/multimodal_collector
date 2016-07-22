package ku.iui.imotion.socceruserstudy;

import android.util.Log;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by ozymaxx on 22.07.2016.
 */

public class PeerSketchThread extends Thread {
    private DataInputStream in;

    public PeerSketchThread(DataInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        String receivedContent = "";

        while (true) {
            char ch = 0;
            try {
                ch = in.readChar();
                receivedContent += ch;

                while (ch != ')') {
                    ch = in.readChar();
                    receivedContent += ch;
                }

                Log.e("PeerData", receivedContent);
            } catch (EOFException e) {
                break;
            } catch (IOException e) {
                Log.e("PeerConn",e.getMessage());
            }
        }
    }
}
