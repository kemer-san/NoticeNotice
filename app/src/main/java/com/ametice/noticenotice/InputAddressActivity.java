package com.ametice.noticenotice;

import android.app.Activity;
import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;

/**
 * Created by Masato on 2015/09/26.
 * 送信先設定の動作
 */
public class InputAddressActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.input_address);
    }






//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.input_address);
//
//        //次へボタン押下時のアクション
//        Button btnSendMessage = (Button) findViewById(R.id.btnSendMessage);
//        btnSendMessage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //メッセージ画面の起動へ
//                startMessageActivity();
//
//            }
//        });
//
//    }
}
