package com.ametice.noticenotice;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.app.Activity;


public class InitSettingActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //レイアウトファイルを指定
        setContentView(R.layout.init_setting);

        //送信間隔ボタン押下時のアクション
        final Button btnSendSetting = (Button) findViewById(R.id.btnSendSetting);
        btnSendSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //送信間隔設定画面の起動へ
                Intent intent = new Intent(getApplicationContext(),SendSettingActivity.class);
                startActivity(intent);
            }
        });

        //送信先設定ボタン押下時のアクション
<<<<<<< HEAD
        Button btnInputAddress2 = (Button) findViewById(R.id.btnInputAddress);
        btnInputAddress2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //送信先設定画面の起動へ
                Intent intent2 = new Intent();
                intent2.setClassName("com.ametice.noticenotice", "com.ametice.noticenotice.InputAddressActivity");
                startActivity(intent2);
=======
        final Button btnInputAddress = (Button) findViewById(R.id.btnInputAddress);
        btnInputAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            //送信間隔設定画面の起動へ
                Intent intent = new Intent(getApplicationContext(),InputAddressActivity.class);
                startActivity(intent);
>>>>>>> kemer-san/master

            }
        });

        //On-Offスイッチのアクション
        /* <memo by kemer-san>
        ソースコード上ではSwitchもToggleButtonも一括してCompoundButtonとして扱います。
        CompoundButtonはSwitchのように2つの状態を持つViewの抽象クラスで、状態の取得やリスナ関連の処理などが集約されており、
        Switch、ToggleButton、CheckBoxなどはいずれもCompoundButtonを継承したサブクラスになっています。*/
        CompoundButton swOnOff = (CompoundButton)findViewById(R.id.swOnOff);
        swOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 状態が変更されたときのアクション
                if(isChecked) {
                    Toast.makeText(InitSettingActivity.this, "通知機能がONになりました", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(InitSettingActivity.this, "通知機能がOFFになりました", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_init_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
