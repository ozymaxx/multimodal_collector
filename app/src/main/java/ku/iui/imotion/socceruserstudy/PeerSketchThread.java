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
    private static String STROKE_START = "STRSTART";
    private static String CLEAR_CANVAS = "CLEAR";
    private static String STROKE_END = "STREND";

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
        int width,r,g,b,a;
        float x,y;
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
                    width = Integer.parseInt(delims[1]);
                    r = Integer.parseInt(delims[2]);
                    g = Integer.parseInt(delims[3]);
                    b = Integer.parseInt(delims[4]);
                    a = Integer.parseInt(delims[5]);

                    synchronized (delegatedCanvas) {
                        delegatedCanvas.changePeerColor(width,r,g,b,a);
                    }

                    justStarted = true;
                }
                else if (delims[0].equals(CLEAR_CANVAS)) {
                    synchronized (delegatedCanvas) {
                        delegatedCanvas.parent.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                delegatedCanvas.clearCanvas(false);
                            }
                        });
                    }
                }
                else {
                    if (!(delims[0].equals(STROKE_END))) {
                        x = Float.parseFloat(delims[1]);
                        y = Float.parseFloat(delims[2]);

                        synchronized (delegatedCanvas) {
                            if (justStarted) {
                                delegatedCanvas.startTouch(x, y, false);
                                justStarted = false;
                            } else {
                                delegatedCanvas.moveTouch(x, y, false);
                            }

                            delegatedCanvas.parent.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    delegatedCanvas.invalidate();
                                    //Log.e("xx","yy");
                                }
                            });
                        }
                    }
                    else {
                        synchronized (delegatedCanvas) {
                            delegatedCanvas.parent.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    delegatedCanvas.upTouch(false);
                                    delegatedCanvas.invalidate();
                                    //Log.e("xx","zz");
                                }
                            });
                        }
                    }
                }

                Log.e("PeerData", receivedContent);
                receivedContent = "";
            } catch (EOFException e) {
                break;
            } catch (IOException e) {
                Log.e("PeerConn",e.getMessage());
            }
        }
    }
}
