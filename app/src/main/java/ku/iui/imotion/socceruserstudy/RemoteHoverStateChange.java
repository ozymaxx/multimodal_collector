package ku.iui.imotion.socceruserstudy;

/**
 * Created by ozymaxx on 23.07.2016.
 */

public class RemoteHoverStateChange implements Runnable {
    private CanvasView delegatedCanvas;
    private boolean remoteHovered;
    private String receivedContent;

    public RemoteHoverStateChange(CanvasView canvas, boolean remoteHovered,String receivedContent) {
        this.delegatedCanvas = canvas;
        this.remoteHovered = remoteHovered;
        this.receivedContent = receivedContent;
    }

    @Override
    public void run() {
        delegatedCanvas.setRemoteHovered(remoteHovered,receivedContent);
    }
}
