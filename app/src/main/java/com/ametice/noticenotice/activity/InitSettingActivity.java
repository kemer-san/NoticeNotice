package com.ametice.noticenotice.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ametice.noticenotice.service.MonitorService;
import com.ametice.noticenotice.data.NoticeSaveData;
import com.ametice.noticenotice.R;

import java.util.ArrayList;
import java.util.List;

public class InitSettingActivity extends Activity {

    //region "GUI部品"

    private Button btnUsage;
    private Button btnSendSetting;
    private Button btnInputAddress;
    private CompoundButton swOnOff;

    /*  常駐サービスのハンドル */
    private MonitorService monitorService;

    /**
     * 部品のハンドラを取得します。
     */
    private void findComponents() {
        btnUsage = (Button) findViewById(R.id.btnUsage);
        btnSendSetting = (Button) findViewById(R.id.btnSendSetting);
        btnInputAddress = (Button) findViewById(R.id.btnInputAddress);
        swOnOff = (CompoundButton) findViewById(R.id.swOnOff);
    }

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //レイアウトファイルを指定
        setContentView(R.layout.init_setting);

        // 部品のハンドラを取得
        findComponents();

        // 使い方ボタン押下時のアクション
        btnUsage.setOnClickListener(btnUsageOnClickListener);

        //送信間隔ボタン押下時のアクション
        btnSendSetting.setOnClickListener(btnSendSettingOnClickListener);

        //送信先設定ボタン押下時のアクション
        btnInputAddress.setOnClickListener(btnInputAddressOnClickListener);

        // OnOffスイッチのアクションをセット
        swOnOff.setOnCheckedChangeListener(swOnOffOnCheckedChangeListener);

        /*  ユーザーが認証済み且つ、常駐サービスが実行中の場合はONにする  */
        boolean isService = isRunService(MonitorService.class.getName(), getApplicationContext());
        boolean isRegister = new NoticeSaveData(getApplicationContext()).loadUserRegistration();
        if(isService == true && isRegister == true) swOnOff.setChecked(true);

        /*   ユーザーの設定値のチェックと初期化   */
        NoticeSaveData nsd = new NoticeSaveData(this);
        nsd.checkUserData();
    }

    private final Button.OnClickListener btnUsageOnClickListener = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            // 使い方画面の起動
            Intent intent = new Intent(getApplicationContext(), UsageActivity.class);
            startActivity(intent);

//            onDebug();
        }
    };

    private void onDebug(){
        int cnt =0;
        boolean dayOfWeekChecks[] = new boolean[7];
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 7; i++) {
            if (new NoticeSaveData(getApplicationContext()).loadDayOfWeek(i) == true) {
            /*  前回値の設定数をカウント    */
                dayOfWeekChecks[i] = new NoticeSaveData(getApplicationContext()).loadDayOfWeek(i);
                cnt++;
                sb.append(Integer.toString(i)+",");
            }
        }
        Log.d("cnt:",Integer.toString(cnt));
        Log.d("checks:",sb.toString());
        Toast.makeText(InitSettingActivity.this, "cnt:" + Integer.toString(cnt) , Toast.LENGTH_SHORT).show();
        Toast.makeText(InitSettingActivity.this, "checks:" + sb.toString(), Toast.LENGTH_SHORT).show();
    }

    private final Button.OnClickListener btnSendSettingOnClickListener = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            // 送信間隔設定画面の起動
            Intent intent = new Intent(getApplicationContext(), SendSettingActivity.class);
            //startActivity(intent);

            /*  遷移先の返却値 */
            int result = 0;

            /*  設定画面に遷移 */
            startActivityForResult(intent, result);

            /*  設定画面のOKボタン押下の有無を判定    */
            if(result == SendSettingActivity.NOTIFICATION_OK_PUSHED){

                /*  常駐サービスが実行中の場合  */
                boolean isService = isRunService(MonitorService.class.getName(), getApplicationContext());
                if(isService == true){
                    /*  インターバル再設定  */
                    monitorService.setCheckInterval();
                }
            }
        }
    };

    private final Button.OnClickListener btnInputAddressOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 送信先設定画面の起動
            Intent intent = new Intent(getApplicationContext(),InputAddressActivity.class);
            startActivity(intent);
        }
    };

    private final CompoundButton.OnCheckedChangeListener swOnOffOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        //On-Offスイッチのアクション
        /* <memo by kemer-san>
        ソースコード上ではSwitchもToggleButtonも一括してCompoundButtonとして扱います。
        CompoundButtonはSwitchのように2つの状態を持つViewの抽象クラスで、状態の取得やリスナ関連の処理などが集約されており、
        Switch、ToggleButton、CheckBoxなどはいずれもCompoundButtonを継承したサブクラスになっています。*/
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            /*  常駐サービスのインテントを生成    */
            Intent service = new Intent(InitSettingActivity.this, MonitorService.class);

            // 状態が変更されたときのアクション
            if (isChecked) {
                /*  トーストを表示  */
                Toast.makeText(InitSettingActivity.this, "通知機能がONになりました", Toast.LENGTH_SHORT).show();

                /*  常駐サービスを起動   */
                startService(service);

                /*  常駐サービスをバインド */
                bindService(service, connect, BIND_AUTO_CREATE);

            } else {
                /*  トーストを表示  */
                Toast.makeText(InitSettingActivity.this, "通知機能がOFFになりました", Toast.LENGTH_SHORT).show();

                /*  通知取得サービスの停止 */
                monitorService.offNotice();

                /*  常駐サービスのバインドを解除 */
                unbindService(connect);

                /*  常駐サービスを停止   */
                stopService(service);
            }
        }
    };


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

    /*  サービス接続のハンドル */
    private ServiceConnection connect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            /*  常駐サービスのハンドルを生成  */
            monitorService = ((MonitorService.MonitorServiceLocalBinder) service).getService();

            /*  通知取得サービスの起動  */
            monitorService.onNotice();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //monitorService = null;
        }
    };

    /**
     * サービスが起動しているかを確認する
     * @param serviceName 確認したいサービスのクラス名
     * @param context コンテキスト
     */
    public boolean isRunService(String serviceName,Context context){
        /*  サービスの一覧を取得  */
        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = am.getRunningServices(Integer.MAX_VALUE);

        /*  クラス名リスト */
        ArrayList<String> serviceNameList = new ArrayList<String>();

        /*  クラス名を取得しリストに格納 */
        for(ActivityManager.RunningServiceInfo serviceInfo : serviceList){
            serviceNameList.add(serviceInfo.service.getClassName());
        }

        /*  引数のクラス名が含まれていたらtrueを返す  */
        return serviceNameList.contains(serviceName);
    }
}
