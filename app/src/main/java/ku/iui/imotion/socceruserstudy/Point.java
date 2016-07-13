package ku.iui.imotion.socceruserstudy;

/**
 * Created by ozymaxx on 12.07.2016.
 */

public class Point implements JSONable {
    private double x, y;
    private long timestamp;
    private String pid;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
        timestamp = System.currentTimeMillis();
        pid = "" + timestamp;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getPid() {
        return pid;
    }

    public String jsonString() {
        return "{\"pid\":\""+pid+"\"\"time\":"+timestamp+",\"x\":"+x+",\"y\":"+y+"}";
    }
}
