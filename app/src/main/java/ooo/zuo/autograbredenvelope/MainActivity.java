package ooo.zuo.autograbredenvelope;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import static android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.tv_app_info);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("适用：\n");
        builder.setSpan(new ForegroundColorSpan(Color.RED),0,3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new AbsoluteSizeSpan(25,true),0,3,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.append("微信版本： 6.5.4\nAndroid版本: 5.0 - 6.0.1\n");
        builder.setSpan(new ForegroundColorSpan(Color.RED),9,15,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append("此程序目前尚未完善，可能存在Bug或者缺陷。\n");
        builder.append("此程序仅供娱乐，勿做他用。\n 由此造成的一切后果与程序作者无关\n\n");
        builder.append("Created by Kever \n\n");
        builder.append("↓↓↓点击按钮，进入设置开启功能↓↓↓");


        if (textView != null) {
            textView.setText(builder);
        }
    }

    public void jumpToSetting(View view) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setAction(ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }
}
