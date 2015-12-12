package com.ametice.noticenotice;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Masato on 2015/11/21.
 */
public class InputPassCodeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //レイアウトファイルを指定
        setContentView(R.layout.input_passcode);
    }
}