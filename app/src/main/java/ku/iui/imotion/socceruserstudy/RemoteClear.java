package ku.iui.imotion.socceruserstudy;

/**
 * Created by ozymaxx on 04.08.2016.
 */
public class RemoteClear implements Runnable {
    private String receivedContent;
    private boolean flag;
    private CanvasView delegatedCanvas;

    public RemoteClear(CanvasView delegatedCanvas, String receivedContent, boolean flag) {
        this.receivedContent = receivedContent;
        this.flag = flag;
        this.delegatedCanvas = delegatedCanvas;
    }

    @Override
    public void run() {
        delegatedCanvas.clearCanvas(false,receivedContent);
    }
}