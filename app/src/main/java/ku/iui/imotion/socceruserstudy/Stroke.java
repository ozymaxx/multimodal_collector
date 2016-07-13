package ku.iui.imotion.socceruserstudy;

import java.util.ArrayList;

/**
 * Created by ozymaxx on 12.07.2016.
 */

public class Stroke implements JSONable {
    private ArrayList<Point> points;
    private String sid;

    public Stroke() {
        points = new ArrayList<Point>();
        sid = System.currentTimeMillis() + "";
    }

    public void addPoint(double x, double y) {
        points.add( new Point(x,y) );
    }

    public String jsonString() {
        String result = "";

        for (int i = 0; i < points.size(); i++) {
            result += points.get(i).jsonString();

            if ( i < points.size() - 1) {
                result += ",";
            }
        }

        result = "[" + result + "]";
        result = "{\"id\":\""+sid+"\",\"points\":"+result+"}";

        return result;
    }
}
