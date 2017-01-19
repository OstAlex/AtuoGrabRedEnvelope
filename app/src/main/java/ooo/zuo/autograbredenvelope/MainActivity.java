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
        builder.append("说明\n");
        builder.setSpan(new ForegroundColorSpan(Color.RED),0,2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new AbsoluteSizeSpan(25,true),0,2,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.append("微信版本： 6.5.3\n其他版本未测试，可能不适用。\n手机系统版本: 5.0及以上，7.0以下（不包括7.0）\n");
        builder.setSpan(new ForegroundColorSpan(Color.RED),8,14,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append("开启后，不要停留在微信界面\n新消息不提示通知是检测不到红包的。\n");
        int length = builder.length();
        builder.append("屏幕常亮！屏幕常亮！屏幕常亮！\n");
        builder.setSpan(new ForegroundColorSpan(Color.RED),length,builder.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append("此程序目前尚未完善，可能存在Bug或者缺陷。\n");
        builder.append("此程序仅供娱乐，勿做他用。\n由此引发的一切后果和损失与程序作者无关。\n");
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
