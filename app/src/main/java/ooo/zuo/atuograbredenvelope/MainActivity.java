package ooo.zuo.atuograbredenvelope;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import static android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void jumpToSetting(View view) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setAction(ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }
}
