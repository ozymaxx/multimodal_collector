package ku.iui.imotion.socceruserstudy;

import android.util.Log;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by ozymaxx on 22.07.2016.
 */

public class PeerSketchThread extends Thread {
    private static String STROKE_START = "STRSTART";
    private static String CLEAR_CANVAS = "CLEAR";
    private static String STROKE_END = "STREND";
    private static String VIDEO_OPEN = "VIDEOOPEN";

    private DataInputStream in;
    private CanvasView delegatedCanvas;

    public PeerSketchThread(DataInputStream in, CanvasView delegatedCanvas) {
        this.in = in;
        this.delegatedCanvas = delegatedCanvas;
    }

    @Override
    public void run() {
        String receivedContent = "";
        String[] delims;
        int r,g,b,a;
        boolean erase;
        float x,y,width;
        boolean justStarted = false;

        while (true) {
            char ch;
            try {
                ch = in.readChar();
                receivedContent += ch;

                while (ch != ')') {
                    ch = in.readChar();
                    receivedContent += ch;
                }

                receivedContent = receivedContent.substring(1,receivedContent.length() - 1);
                delims = receivedContent.split(",");

                if (delims[0].equals(STROKE_START)) {
                    width = Float.parseFloat(delims[1]);
                    r = Integer.parseInt(delims[2]);
                    g = Integer.parseInt(delims[3]);
                    b = Integer.parseInt(delims[4]);
                    a = Integer.parseInt(delims[5]);
                    erase = Boolean.parseBoolean(delims[6]);

                    delegatedCanvas.changePeerColor(width,r,g,b,a,erase,receivedContent);

                    justStarted = true;
                }
                else if (delims[0].equals(CLEAR_CANVAS)) {
                    delegatedCanvas.parent.runOnUiThread(new RemoteClear(delegatedCanvas,receivedContent,false));
                }
                else if (!(delims[0].equals(VIDEO_OPEN))) {
                    if (!(delims[0].equals(STROKE_END))) {
                        x = Float.parseFloat(delims[0]);
                        y = Float.parseFloat(delims[1]);

                        synchronized (delegatedCanvas) {
                            if (justStarted) {
                                delegatedCanvas.parent.runOnUiThread(new RemoteDraw(delegatedCanvas,x,y,CanvasView.REMOTE_START,receivedContent));
                                justStarted = false;
                            } else {
                                delegatedCanvas.parent.runOnUiThread(new RemoteDraw(delegatedCanvas,x,y,CanvasView.REMOTE_MOVE,receivedContent));
                            }
                        }
                    }
                    else {
                        synchronized (delegatedCanvas) {
                            delegatedCanvas.parent.runOnUiThread(new RemoteDraw(delegatedCanvas,0,0,CanvasView.REMOTE_UP,receivedContent));
                        }
                    }
                }
                else {
                    delegatedCanvas.addOwnStream(receivedContent,false);
                }

                receivedContent = "";
            } catch (EOFException e) {
                break;
            } catch (IOException e) {
                Log.e("PeerConn",e.getMessage());
            }
        }
    }
}
