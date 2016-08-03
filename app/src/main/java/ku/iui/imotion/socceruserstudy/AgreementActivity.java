package ku.iui.imotion.socceruserstudy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class AgreementActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
    }

    public void fourthStep(View view) {
        Intent intent = new Intent(this,ConnectionStatusActivity.class);
        startActivity(intent);
        finish();
    }
}
