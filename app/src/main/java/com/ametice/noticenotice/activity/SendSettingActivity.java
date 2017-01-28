package com.ametice.noticenotice.activity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ametice.noticenotice.R;
import com.ametice.noticenotice.data.NoticeSaveData;
import com.ametice.noticenotice.google.GoogleAccountChooser;
import com.ametice.noticenotice.setting.UserNoticeSetting;
import com.ametice.noticenotice.view.ResizableTextView;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.gmail.GmailScopes;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Masato on 2015/09/26.
 * 送信設定画面の動作
 * 最終更新 :2015/11/21 y.hiyoshi
 * @version 1.2
 */
public class SendSettingActivity extends Activity {

    private Context context;
    private UserNoticeSetting uns;
    private String accountName = "";

    public static final int NOTIFICATION_OK_PUSHED = 1;
    public static final int REQUEST_ACCOUNT_CHOOSER = 2;
    private GoogleAccountCredential mCredential;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        uns = new UserNoticeSetting(context);

        setContentView(R.layout.send_setting);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /*******************/
        /**   Gmail        */
        /*******************/
        /*   Gmailボタン   */
        Button sendOwnGmailBtn = (Button)findViewById(R.id.btn_own_gmail);
        sendOwnGmailBtn.setOnClickListener(new SetingBtnClickListener());

        /*  設定情報表示領域のハンドル生成    */
        TextView ownGmailMsg = (TextView)findViewById(R.id.text_own_gmail_msg);
        accountName = new NoticeSaveData(context).loadUserGmailAccount();
        ownGmailMsg.setText(accountName);

        /*******************/
        /**   曜日指定      */
        /*******************/
        /*   曜日指定設定ボタン   */
        Button dayOfWeekBtn = (Button)findViewById(R.id.btn_dayofweek);
        dayOfWeekBtn.setOnClickListener(new SetingBtnClickListener());

        /*  設定情報表示領域のハンドル生成    */
        TextView dayOfWeekMsg = (TextView)findViewById(R.id.text_dayofweekmsg);
        dayOfWeekMsg.setText(getDayOfWeekText());

        /*******************/
        /**  確認開始時刻    */
        /*******************/
        /*   確認開始時刻設定ボタン   */
        Button startTimeBtn = (Button)findViewById(R.id.btn_starttime);
        startTimeBtn.setOnClickListener(new SetingBtnClickListener());

        /*  設定情報表示領域のハンドル生成    */
        TextView startTimeMsg = (TextView)findViewById(R.id.text_starttimemsg);
        startTimeMsg.setText(toTimeFormat(new NoticeSaveData(context).loadStartTime()));

        /*******************/
        /**  確認終了時刻    */
        /*******************/
        /*   確認終了時刻設定ボタン   */
        Button endTimeBtn = (Button)findViewById(R.id.btn_endtime);
        endTimeBtn.setOnClickListener(new SetingBtnClickListener());

        /*  設定情報表示領域のハンドル生成    */
        TextView endTimeMsg = (TextView)findViewById(R.id.text_endtimemsg);
        endTimeMsg.setText(toTimeFormat(new NoticeSaveData(context).loadEndTime()));

        /*******************/
        /**   確認間隔設定   */
        /*******************/
        /*   確認間隔設定ボタン   */
        Button intervalBtn = (Button)findViewById(R.id.btn_interval);
        intervalBtn.setOnClickListener(new SetingBtnClickListener());

        /*  設定情報表示領域のハンドル生成    */
        TextView intervalMsg = (TextView)findViewById(R.id.text_intervalmsg);
        intervalMsg.setText(toIntervalFormat(new NoticeSaveData(context).loadCheckInterval()));

        /*******************/
        /**   設定完了      */
        /*******************/
        /*   設定完了ボタン   */
        Button sendSettingBtn = (Button)findViewById(R.id.btn_sendsetting);
        sendSettingBtn.setOnClickListener(new SetingBtnClickListener());

    }

    /*      ボタンイベントリスナー  */
    private class SetingBtnClickListener implements View.OnClickListener {
        public void onClick(View v) {
            ResizableTextView tv;
            switch (v.getId()) {
                case R.id.btn_own_gmail:
                    /*  ダイアログ生成 */
                    GoogleAccountChooser gac = new GoogleAccountChooser(getApplicationContext());
                    mCredential = gac.showAccountChooser(Arrays.asList(GmailScopes.GMAIL_SEND));
                    startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_CHOOSER);

                    break;

                case R.id.btn_dayofweek:
                    /*  設定情報表示領域のハンドル生成    */
                    tv = (ResizableTextView)findViewById(R.id.text_dayofweekmsg);

                    /*  ダイアログ生成 */
                    uns.onDayOfWeekSetting(tv);
                    break;

                case R.id.btn_starttime:
                    /*  設定情報表示領域のハンドル生成    */
                    tv = (ResizableTextView)findViewById(R.id.text_starttimemsg);

                    /*  ダイアログ生成 */
                    uns.onStartTimeSetting(tv);
                    break;

                case R.id.btn_endtime:
                    /*  設定情報表示領域のハンドル生成    */
                    tv = (ResizableTextView)findViewById(R.id.text_endtimemsg);

                    /*  ダイアログ生成 */
                    uns.onEndTimeSetting(tv);

                    break;

                case R.id.btn_interval:
                    /*  設定情報表示領域のハンドル生成    */
                    tv = (ResizableTextView)findViewById(R.id.text_intervalmsg);

                    /*  ダイアログ生成 */
                    uns.onIntervalSetting(tv);

                    break;
                case R.id.btn_sendsetting:
                    /*   端末内部へ設定値の一括保存   */
                    uns.saveTimeData();

                    /*   Gmailアカウントの保存   */
                    uns.saveUserGmailData(accountName);

                    /*  遷移元に値を返す（OKボタンが押下されました）    */
                    setResult(NOTIFICATION_OK_PUSHED);

                    /*   アクティビティを破棄し遷移前画面に戻る   */
                    finish();

                    break;

                default:
                    /*  DO NOTHING  */
                    break;
            }
        }
    }

    /**
     * 設定された曜日の文字列取得メソッド
     */
    public String getDayOfWeekText(){

        int cnt = 0;

        /*  曜日チェックの状態    */
        boolean dayOfWeekChecks[] = new boolean[7];
        String allDayOfWeek;
        StringBuilder strb = new StringBuilder();

        /*  曜日リスト    */
        HashMap<Integer,String> dayOfWeekList = new HashMap<Integer,String>();

        /*  曜日リストの生成    */
        dayOfWeekList.put(0, "日");
        dayOfWeekList.put(1, "月");
        dayOfWeekList.put(2, "火");
        dayOfWeekList.put(3, "水");
        dayOfWeekList.put(4, "木");
        dayOfWeekList.put(5, "金");
        dayOfWeekList.put(6, "土");

        /*  前回値を表示する（日〜土[0-6]）    */
        for(int i = 0; i < 7; i++){
            if(new NoticeSaveData(context).loadDayOfWeek(i) == true){
                /*  表示用に曜日の文字を保持   */
                strb.append(dayOfWeekList.get(i));

                /*  前回値の設定数をカウント    */
                cnt++;
            }
        }

        /*  表示用の曜日を格納   */
        allDayOfWeek = strb.toString();

        /* 曜日指定なし */
        if(cnt == 0) {allDayOfWeek = "none";}
        /* 毎日指定 */
        if(cnt == 7) {allDayOfWeek = "everyday";}

        return allDayOfWeek;
    }

    /**
     * 表示用時刻フォーマット変換
     * @param time 時刻文字列
     * @return 表示用時刻文字列
     */
    private String toTimeFormat(String time){
        int hour;
        int minute;
        try {
             /*  文字列分割   */
            String splitTime[] = time.split(":", 2);
            hour = Integer.parseInt(splitTime[0]);
            minute = Integer.parseInt(splitTime[1]);
        }catch (Exception e){
            hour = 0;
            minute = 0;
        }

        /*  2ケタの時刻文字列を生成   */
        return String.format("%1$02d", hour)+ ":" + String.format("%1$02d", minute);
    }

    /**
     * 確認間隔フォーマット変換
     * @param interval 確認間隔
     * @return 表示用確認間隔
     */
    private String toIntervalFormat(String interval){

        String intervalText;

        if (interval.equals("0") == true || interval.equals("") == true){
            intervalText = "none";
        }else{
            intervalText = interval + "分";
        }

        return intervalText;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            /** Gmailアカウント選択時のコールバック    */
            case REQUEST_ACCOUNT_CHOOSER:
                if (resultCode == RESULT_OK && data != null) {
                    /** 選択したアカウント名を取得    */
                    accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        Log.d("accountName:", accountName);

                        /** 選択したアカウント名をOAuthに設定    */
                        mCredential.setSelectedAccountName(accountName);

                        /** Debug用（アカウント設定時にメール送信がされる）  */
//                        GmailSender gs = new GmailSender(mCredential);
//                        Log.d("gmail::accountName", accountName);
//                        gs.sendGmail(accountName, accountName, "test", "testest");

                        /*  設定表示領域にアカウント名表示    */
                        ResizableTextView tv = (ResizableTextView)findViewById(R.id.text_own_gmail_msg);
                        tv.setText(accountName);
                    }
                }
                break;
        }
    }
}
