package ku.iui.imotion.socceruserstudy;

/**
 * Created by ozymaxx on 12.07.2016.
 * taken from https://examples.javacodegeeks.com/android/core/graphics/canvas-graphics/android-canvas-example/
 * why would I invent the wheel from scratch? :D
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;
import static android.graphics.Color.YELLOW;

public class CanvasView extends ImageView {

    final static int REMOTE_START = 1;
    final static int REMOTE_MOVE = 2;
    final static int REMOTE_UP = 3;

    final static String stationIp = "172.20.32.153";
    //final static String stationIp = "212.175.32.131";
    final static int stationPort = 3440;
    final static float THINNER = 4f;
    final static float THICKER = 8f;

    public int width;
    public int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private ArrayList<Path> mPaths;
    private ArrayList<Path> peerPaths;
    Context context;
    private ArrayList<Paint> mPaints;
    private ArrayList<Paint> peerPaints;
    private float mX, mY;
    private float pX, pY;
    private static final float TOLERANCE = 0;
    private Socket client;
    private OutputStream outToServer;
    private DataOutputStream out;
    private DataInputStream in;
    private float curStrokeWidth;
    private float peerStrokeWidth;
    private int curr,curg,curb,cura;
    private int pr,pg,pb,pa;
    private PrintWriter sketchStream;

    public MainActivity parent;

    private Sketch sketch;

    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;

        mPaths = new ArrayList<Path>();
        mPaints = new ArrayList<Paint>();

        peerPaths = new ArrayList<Path>();
        peerPaints = new ArrayList<Paint>();

        // we set a new Path
        Path mPath = new Path();
        mPaths.add(mPath);

        Path pPath = new Path();
        peerPaths.add(pPath);

        // and we set a new Paint with the desired attributes
        curStrokeWidth = THINNER;
        cura = 255;curr = 255;curg = 255;curb = 255;
        mPaints.add(newPaint(WHITE,curStrokeWidth));
        peerPaints.add(newPaint(WHITE,curStrokeWidth));

        sketch = new Sketch();

        client = ConnectionStatusActivity.client;
        outToServer = ConnectionStatusActivity.outToServer;
        out = ConnectionStatusActivity.out;
        in = ConnectionStatusActivity.in;

        // DIKKAT
        //new SocketSubmissionTask(this).execute(stationIp,stationPort);
    }

    public void setParent(MainActivity activity) {
        this.parent = activity;
    }

    public void setStreamAndStart(PrintWriter sketchStream) {
        this.sketchStream = sketchStream;
        new PeerSketchThread(in,this).start();
    }

    public void bringSocket(Socket resultSocket) {
        this.client = resultSocket;

        try {
            outToServer = client.getOutputStream();
            out = new DataOutputStream(outToServer);
        } catch (IOException e) {
            Log.e("StationConn",e.getMessage());
            Toast.makeText(parent.getApplicationContext(),"Network socket initialization error!",Toast.LENGTH_LONG).show();
            outToServer = null;
            client = null;
            out = null;
        }
    }

    public Paint newPaint(int c,float strokeWidth) {
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(c);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(strokeWidth);

        return mPaint;
    }

    public synchronized void addOwnStream(String receivedContent,boolean self) {
        if (self) {
            sketchStream.println("0,"+receivedContent+","+System.nanoTime());
        }
        else {
            sketchStream.println("1,"+receivedContent+","+System.nanoTime());
        }
    }

    // override onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // your Canvas will draw onto the defined Bitmap
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw the mPath with the mPaint on the canvas when onDraw

        // many strokes
        for (int i = 0; i < mPaths.size(); i++) {
            canvas.drawPath(mPaths.get(i), mPaints.get(i));
        }

        // peer sketch
        for (int i = 0; i < peerPaths.size(); i++) {
            canvas.drawPath(peerPaths.get(i), peerPaints.get(i));
            //Log.e("PeerDebug",""+i);
        }
    }

    private void sendLog( String receivedContent) {
        //new PointSubmissionTask(stationAddr,clientSocket,stationPort,stationIp).execute(x,y,(float) timestamp);
        if (out != null) {
            new LogTask(out).execute(receivedContent);
            //new PointSubmissionTask(out).execute(x,y,(float) timestamp, (float) timem);
        }
        else {
            Log.e("StationConn","Connection problem");
            Toast.makeText(parent.getApplicationContext(),"Connection problem with the station and peer!",Toast.LENGTH_LONG).show();
        }
    }

    public void endConnection() {
        try {
            if (client != null) {
                client.close();
            }
        } catch (IOException e) {
            Log.e("StationConn",e.getMessage());
            Toast.makeText(parent.getApplicationContext(),"Connection problem with the station and peer!",Toast.LENGTH_LONG).show();
        }
    }

    // when ACTION_DOWN start touch according to the x,y values
    public void startTouch(float x, float y, boolean self) {
        if (self) {
            mPaths.get(mPaths.size() - 1).moveTo(x, y);
            mX = x;
            mY = y;

            sketch.newStroke();

            String recc = "STRSTART,"+curStrokeWidth+","+curr+","+curg+","+curb+","+cura;
            sendLog(recc);
            addOwnStream(recc,true);

            sketch.addPoint(x, y);
            String receivedContent = x+","+y;
            sendLog(receivedContent);
            addOwnStream(receivedContent,true);
        }
        else {
            peerPaths.get(peerPaths.size() - 1).moveTo(x,y);
            pX = x;
            pY = y;
        }
    }

    // when ACTION_MOVE move touch according to the x,y values
    public void moveTouch(float x, float y, boolean self) {
        if (self) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOLERANCE || dy >= TOLERANCE) {
                mPaths.get(mPaths.size() - 1).quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;

                sketch.addPoint(x, y);

                String receivedContent = x+","+y;
                sendLog(receivedContent);
                addOwnStream(receivedContent,true);
            }
        }
        else {
            float dx = Math.abs(x - pX);
            float dy = Math.abs(y - pY);
            if (dx >= TOLERANCE || dy >= TOLERANCE) {
                peerPaths.get(peerPaths.size() - 1).quadTo(pX, pY, (x + pX) / 2, (y + pY) / 2);
                pX = x;
                pY = y;
            }
        }
    }

    public synchronized void clearCanvas(boolean self,String receivedContent) {
        for (Path mPath : mPaths) {
            mPath.reset();
        }

        for (Path pPath : peerPaths) {
            pPath.reset();
        }

        mPaths = new ArrayList<Path>();
        mPaths.add(new Path());

        mPaints = new ArrayList<Paint>();
        curStrokeWidth = THINNER;
        curr = 255;curg = 255;curb = 255;cura = 255;
        mPaints.add(newPaint(WHITE,curStrokeWidth));

        peerPaths = new ArrayList<Path>();
        peerPaths.add(new Path());

        peerPaints = new ArrayList<Paint>();
        peerPaints.add(newPaint(WHITE,curStrokeWidth));

        addOwnStream(receivedContent,false);

        invalidate();

        sketch = new Sketch();

        if (self) {
            String rec = "CLEAR";
            sendLog(rec);
            addOwnStream(rec,true);
        }
    }

    // when ACTION_UP stop touch
    public void upTouch(boolean self) {
        if (self) {
            mPaths.get(mPaths.size() - 1).lineTo(mX, mY);

            sketch.addPoint(mX, mY);

            mPaths.add(new Path());

            Paint mPaint = mPaints.get(mPaints.size() - 1);
            mPaints.add(newPaint(mPaint.getColor(), mPaint.getStrokeWidth()));

            String receivedContent = "STREND";
            sendLog(receivedContent);
            addOwnStream(receivedContent,true);
        }
        else {
            peerPaths.get(peerPaths.size() - 1).lineTo(pX, pY);
            peerPaths.add(new Path());

            Paint pPaint = peerPaints.get(peerPaints.size() - 1);
            peerPaints.add(newPaint(pPaint.getColor(), pPaint.getStrokeWidth()));
        }
    }

    //override the onTouchEvent
    @Override
    public synchronized boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y, true);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y, true);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                upTouch(true);
                invalidate();
                break;
        }
        return true;
    }

    public synchronized void remoteTouchEvent(float x,float y,int type,String receivedContent) {
        switch (type) {
            case REMOTE_MOVE:
                addOwnStream(receivedContent,false);
                moveTouch(x,y,false);
                invalidate();
                break;
            case REMOTE_START:
                addOwnStream(receivedContent,false);
                startTouch(x,y,false);
                invalidate();
                break;
            case REMOTE_UP:
                addOwnStream(receivedContent,false);
                upTouch(false);
                invalidate();
                break;
        }
    }

    public synchronized void changeModeAndColor(int color) {
        Paint mPaint = mPaints.get(mPaints.size() - 1);

        switch (color) {
            case 1:
                cura = 150;
                curr = 0xcc;
                curg = 0;
                curb = 0;
                mPaint.setColor(Color.argb(cura, curr, curg, curb));
                curStrokeWidth = THICKER;
                mPaint.setStrokeWidth(curStrokeWidth);
                break;
            case 2:
                cura = 150;
                curr = 0xff;
                curg = 0xff;
                curb = 0;
                mPaint.setColor(Color.argb(cura, curr, curg, curb));
                curStrokeWidth = THICKER;
                mPaint.setStrokeWidth(curStrokeWidth);
                break;
            case 3:
                cura = 150;
                curr = 0;
                curg = 0x99;
                curb = 0xcc;
                mPaint.setColor(Color.argb(cura, curr, curg, curb));
                curStrokeWidth = THICKER;
                mPaint.setStrokeWidth(curStrokeWidth);
                break;
            case 4:
                cura = 150;
                curr = 0xaa;
                curg = 0x66;
                curb = 0xcc;
                mPaint.setColor(Color.argb(cura, curr, curg, curb));
                curStrokeWidth = THICKER;
                mPaint.setStrokeWidth(curStrokeWidth);
                break;
            case 5:
                cura = 150;
                curr = 0x70;
                curg = 0x06;
                curb = 0x06;
                mPaint.setColor(Color.argb(cura, curr, curg, curb));
                curStrokeWidth = THICKER;
                mPaint.setStrokeWidth(curStrokeWidth);
                break;
            case 6:
                cura = 255;
                curr = 255;
                curg = 255;
                curb = 255;
                mPaint.setColor(WHITE);
                curStrokeWidth = THINNER;
                mPaint.setStrokeWidth(curStrokeWidth);
                break;
        }
    }

    public synchronized void changePeerColor(float width,int r,int g,int b,int a,String receivedContent) {
        Paint pPaint = peerPaints.get(peerPaints.size() - 1);

        peerStrokeWidth = width;
        pr = r;
        pg = g;
        pb = b;
        pa = a;

        pPaint.setColor(Color.argb(pa,pr,pg,pb));
        pPaint.setStrokeWidth(1f*peerStrokeWidth);

        addOwnStream(receivedContent,false);
    }
}
