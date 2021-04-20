package cn.cqray.demo.captcha;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import cn.cqray.android.widget.captcha.CodeCaptcha;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView iv = findViewById(R.id.iv);

        final CodeCaptcha cc = CodeCaptcha.builder()
                .backgroundColor(Color.WHITE)
                .backgroundRadius(5)
                .interfereSize(2)
                .codeBold(true)
                .capitalEnable(false)
                .codeLength(4)
                .build();

        cc.setCodeInto(iv);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cc.setCodeInto(iv);
            }
        });
    }
}