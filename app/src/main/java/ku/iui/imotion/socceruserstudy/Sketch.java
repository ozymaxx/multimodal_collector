package ku.iui.imotion.socceruserstudy;

import java.util.ArrayList;

/**
 * Created by ozymaxx on 12.07.2016.
 */

public class Sketch implements JSONable {
    private ArrayList<Stroke> strokes;
    private String skid;
    private Stroke curStroke;

    public Sketch() {
        skid = System.currentTimeMillis() + "";
        strokes = new ArrayList<Stroke>();
    }

    public void newStroke() {
        curStroke = new Stroke();
    }

    public void addPoint(double x, double y) {
        curStroke.addPoint(x,y);
    }

    public void leaveStroke() {
        strokes.add(curStroke);
    }

    public String jsonString() {
        String result = "";

        for (int i = 0; i < strokes.size(); i++) {
            result += strokes.get(i).jsonString();

            if (i < strokes.size() - 1) {
                result += ",";
            }
        }

        result = "[" + result + "]";
        result = "{\"id\":\""+skid+"\",\"strokes\":"+result+"}";

        return result;
    }
}
