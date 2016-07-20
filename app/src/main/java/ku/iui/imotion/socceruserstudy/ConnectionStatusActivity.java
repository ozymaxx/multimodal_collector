package ku.iui.imotion.socceruserstudy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;

public class ConnectionStatusActivity extends AppCompatActivity {
    final static int STATIONPORT = 3440;
    final static String STATIONIP = "172.20.33.42";

    public static Socket client;
    public static OutputStream outToServer;
    public static DataOutputStream out;

    private boolean status;
    private CheckBox otherTablet,station;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_status);
        status = false;
        otherTablet = (CheckBox) findViewById(R.id.otherTabletCheck);
        station = (CheckBox) findViewById(R.id.stationCheck);

        new SocketSubmissionTask(this).execute(STATIONIP,STATIONPORT);
    }

    public void bringSocket(Socket resultSocket) {
        this.client = resultSocket;

        try {
            outToServer = client.getOutputStream();
            out = new DataOutputStream(outToServer);
            station.setChecked(true);

            Toast.makeText(getApplicationContext(),"Connected to the station",Toast.LENGTH_LONG).show();

            if (station.isChecked()) { // && otherTablet.isChecked()) {
                status = true;
            }
        } catch (Exception e) {
            //Log.e("StationConn",e.getMessage());
            outToServer = null;
            client = null;
            out = null;
            Toast.makeText(getApplicationContext(),"Connection error!",Toast.LENGTH_LONG).show();
        }
    }

    public void thirdStep(View view) {
        if (status) {
            //Toast.makeText(getApplicationContext(),"OK :)", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(),"The tablet must connect to the station!",Toast.LENGTH_LONG).show();
        }
    }
}
