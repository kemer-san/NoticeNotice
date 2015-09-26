package com.ametice.noticenotice;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Masato on 2015/09/26.
 */
public class MessageActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.message);

        //送信ボタン押下時のアクション
        Button btnInputPasscode = (Button) findViewById(R.id.btnInputPasscode);
        btnInputPasscode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //メッセージ画面の起動へ
                startInputPasscodeActivity();

            }
        });

    }

}
