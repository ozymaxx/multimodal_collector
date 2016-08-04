package ku.iui.imotion.socceruserstudy;

/**
 * Created by ozymaxx on 23.07.2016.
 */

public class RemoteDraw implements Runnable {
    private CanvasView delegatedCanvas;
    private float x,y;
    private int type;
    private String receivedContent;

    public RemoteDraw(CanvasView canvas,float x,float y,int type,String receivedContent) {
        this.x = x;
        this.y = y;
        this.delegatedCanvas = canvas;
        this.type = type;
        this.receivedContent = receivedContent;
    }

    @Override
    public void run() {
        delegatedCanvas.remoteTouchEvent(x,y,type,receivedContent);
    }
}
