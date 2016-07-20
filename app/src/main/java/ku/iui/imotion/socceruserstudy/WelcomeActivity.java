package ku.iui.imotion.socceruserstudy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void secondStep(View view) {
        Intent intent = new Intent(this,AgreementActivity.class);
        startActivity(intent);
        finish();
    }
}
