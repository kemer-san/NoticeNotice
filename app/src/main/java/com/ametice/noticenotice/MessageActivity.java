package com.ametice.noticenotice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Masato on 2015/09/26.
 */
public class MessageActivity extends Activity {

    // 画面モード
    public enum Mode {
        Confirm,
        Complete,
    }

    // 画面モード
    public Mode _Mode;


    private TextView lblAddress;

    /**
     * コンポーネントのハンドラを取得します。
     */
    private void findComponents() {
        lblAddress = (TextView) findViewById(R.id.lblMessageValue);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // レイアウトを設定
        setContentView(R.layout.message);

        // コンポーネントのハンドラを取得
        findComponents();

        // インテントを取得
        Intent intent = getIntent();

        // メールアドレスを取得
        String Address = intent.getStringExtra("EmailAddress");

        // 表示用ラベルにセット
        lblAddress.setText(Address);


        //送信ボタン押下時のアクション
//        Button btnInputPasscode = (Button) findViewById(R.id.btnInputPasscode);
//        btnInputPasscode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //メッセージ画面の起動へ
//                startInputPasscodeActivity();
//
//            }
//        });

    }

}
