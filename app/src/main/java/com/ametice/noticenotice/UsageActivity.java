package com.ametice.noticenotice;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class UsageActivity extends Activity {

    //region "GUI部品"
    private Button btnClose;

    /**
     * 部品のハンドラを取得します。
     */
    private void findComponents() {
        btnClose = (Button) findViewById(R.id.btnClose);
    }
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usage);

        findComponents();

        btnClose.setOnClickListener(btnCloseOnClick);

    }

    /**
     * 閉じるボタン押下時のアクション
     *
     * */
    private Button.OnClickListener btnCloseOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

}
