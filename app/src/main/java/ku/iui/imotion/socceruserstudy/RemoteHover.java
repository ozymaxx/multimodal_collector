package ku.iui.imotion.socceruserstudy;

/**
 * Created by ozymaxx on 23.07.2016.
 */

public class RemoteHover implements Runnable {
    private CanvasView delegatedCanvas;
    private float x, y;
    private String receivedContent;

    public RemoteHover(CanvasView canvas,float x,float y,String receivedContent) {
        this.delegatedCanvas = canvas;
        this.x = x;
        this.y = y;
        this.receivedContent = receivedContent;
    }

    @Override
    public void run() {
        delegatedCanvas.hoverRemote(x,y,receivedContent);
    }
}
