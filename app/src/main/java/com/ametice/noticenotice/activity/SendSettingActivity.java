package com.ametice.noticenotice.activity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ametice.noticenotice.R;
import com.ametice.noticenotice.data.NoticeSaveData;
import com.ametice.noticenotice.google.GoogleAccountChooser;
import com.ametice.noticenotice.view.ResizableTextView;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.gmail.GmailScopes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Masato on 2015/09/26.
 * 送信設定画面の動作
 * 最終更新 :2015/11/21 y.hiyoshi
 * @version 1.2
 */
public class SendSettingActivity extends Activity {


    public static final int NOTIFICATION_OK_PUSHED = 1;
    public static final int REQUEST_ACCOUNT_CHOOSER = 2;

    private GoogleAccountCredential credential;

    /*  Gmailアカウント名    */
    private String accountName = "";

    /*  曜日チェックの状態    */
    private DayOfWeek dayOfWeekChecks = new DayOfWeek();

    /*  開始時刻    */
    private Time startTime = new Time();

    /*  終了時刻    */
    private Time endTime = new Time();

    /*  確認間隔   */
    private String interval = "60";

    /*  設定項目表示領域   */
    private TextView dayOfWeekField;
    private TextView startTimeField;
    private TextView endTimeField;
    private TextView intervalField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_setting);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /*******************/
        /**   Gmail        */
        /*******************/
        /*   Gmailボタン   */
        Button sendOwnGmailBtn = (Button)findViewById(R.id.btn_own_gmail);
        sendOwnGmailBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickGmailButton(v);
            }
        });

        /*  設定情報表示領域のハンドル生成    */
        TextView ownGmailItem = (TextView)findViewById(R.id.text_own_gmail_msg);
        accountName = new NoticeSaveData(this).loadUserGmailAccount();
        ownGmailItem.setText(accountName);

        /*******************/
        /**   曜日指定      */
        /*******************/
        /*   曜日指定設定ボタン   */
        Button dayOfWeekBtn = (Button)findViewById(R.id.btn_dayofweek);
        dayOfWeekBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickDayOfWeekButton(v);
            }
        });

        /*  前回値を選択済みにする（日〜土[0-6]）    */
        for(int i = 0; i < dayOfWeekChecks.size(); i++){
            dayOfWeekChecks.set(i, new NoticeSaveData(this).loadDayOfWeek(i));
        }

        /*  設定情報表示領域のハンドル生成    */
        dayOfWeekField = (TextView)findViewById(R.id.text_dayofweekmsg);
        dayOfWeekField.setText(createDayOfWeekText(dayOfWeekChecks));

        /*******************/
        /**  確認開始時刻    */
        /*******************/
        /*   確認開始時刻設定ボタン   */
        Button startTimeBtn = (Button)findViewById(R.id.btn_starttime);
        startTimeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickStartTimeButton(v);
            }
        });

        /*  前回値を設定する  */
        String splitStartTime[] = new NoticeSaveData(this).loadStartTime().split(":", 2);
        startTime.setTime(Integer.parseInt(splitStartTime[0]), Integer.parseInt(splitStartTime[1]));

        /*  設定情報表示領域のハンドル生成    */
        startTimeField = (TextView)findViewById(R.id.text_starttimemsg);
        startTimeField.setText(startTime.toFormat());

        /*******************/
        /**  確認終了時刻    */
        /*******************/
        /*   確認終了時刻設定ボタン   */
        Button endTimeBtn = (Button)findViewById(R.id.btn_endtime);
        endTimeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickEndTimeButton(v);
            }
        });

        /*  前回値を設定する  */
        String splitEndTime[] = new NoticeSaveData(this).loadEndTime().split(":", 2);
        endTime.setTime(Integer.parseInt(splitEndTime[0]), Integer.parseInt(splitEndTime[1]));

        /*  設定情報表示領域のハンドル生成    */
        endTimeField = (TextView)findViewById(R.id.text_endtimemsg);
        endTimeField.setText(endTime.toFormat());

        /*******************/
        /**   確認間隔設定   */
        /*******************/
        /*   確認間隔設定ボタン   */
        Button intervalBtn = (Button)findViewById(R.id.btn_interval);
        intervalBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickIntevalButton(v);
            }
        });

        /*  前回値を設定する  */
        interval = new NoticeSaveData(this).loadCheckInterval();

        /*  設定情報表示領域のハンドル生成    */
        intervalField = (TextView)findViewById(R.id.text_intervalmsg);

        /*  確認間隔設定    */
        String interval =  new NoticeSaveData(getApplicationContext()).loadCheckInterval();
        String intervalFieldtext = (interval.equals("0") == true || interval.equals("") == true)?
                getApplicationContext().getString(R.string.dayofweek_none):
                interval + getApplicationContext().getString(R.string.minute);
        intervalField.setText(intervalFieldtext);

        /*******************/
        /**   設定完了      */
        /*******************/
        /*   設定完了ボタン   */
        Button sendSettingBtn = (Button)findViewById(R.id.btn_sendsetting);
        sendSettingBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickCompleteButton(v);
            }
        });
    }

    /**
     * 曜日指定ダイアログを表示する
     */
    public void showDayOfWeekSetting(DayOfWeek dayOfWeekChecks){

        /*  既存の曜日情報をコピー  */
        final DayOfWeek selectedDays = (DayOfWeek) dayOfWeekChecks.clone();

        /*  ダイアログ設定 */
        AlertDialog.Builder checkDlg = new AlertDialog.Builder(SendSettingActivity.this);

        /*  タイトル設定  */
        checkDlg.setTitle(getApplicationContext().getString(R.string.list_item_name_dayofweek));

        /*  定義ファイルから選択可能アイテム（曜日）を取得 */
        final CharSequence[] chkItems = getResources().getStringArray(R.array.noticenotice_dayofweek_setting_selectable_items);

        /*  曜日選択ダイアログの生成   */
        checkDlg.setMultiChoiceItems(
            chkItems,
            selectedDays.toBoolArray(),
            new DialogInterface.OnMultiChoiceClickListener() {
                public void onClick(DialogInterface dialog, int which, boolean selected) {
                    /*  各曜日のチェック状態を格納  */
                    selectedDays.set(which, selected);
                }
            }
        );

        /*  OKボタンを定義    */
        checkDlg.setPositiveButton(getApplicationContext().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                /*   メッセージ変更   */
                dayOfWeekField.setText(createDayOfWeekText(selectedDays));

                /*  曜日情報を格納  */
                SendSettingActivity.this.dayOfWeekChecks = selectedDays;
            }
        });

        /*  ダイアログ表示  */
        checkDlg.create().show();
    }

    /**
     * 確認終了時刻ダイアログを表示する
     */
    public void showStartTimeSetting(){
        /*  時間選択ダイアログの生成    */
        TimePickerDialog timepickDlg= new TimePickerDialog(
            SendSettingActivity.this,
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int setHour, int SetMinute) {
                    /*   開始時刻を格納   */
                    startTime.setTime(setHour, SetMinute);

                    /*   メッセージ変更   */
                    startTimeField.setText(startTime.toFormat());
                }
            },
            startTime.getHour(), startTime.getMinute(), true
        );

        /*  タイトル設定  */
        timepickDlg.setTitle(getApplicationContext().getString(R.string.list_item_name_starttime));

        /*  ダイアログ表示  */
        timepickDlg.show();
    }

    /**
     * 確認終了時刻ダイアログを表示する
     */
    public void showEndTimeSetting(){
        /*  時間選択ダイアログの生成    */
        TimePickerDialog timepickDlg= new TimePickerDialog(
            SendSettingActivity.this,
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int setHour, int SetMinute) {
                    /*   開始時刻を格納   */
                    endTime.setTime(setHour, SetMinute);

                    /*   メッセージ変更   */
                    endTimeField.setText(endTime.toFormat());
                }
            },
            endTime.getHour(), endTime.getMinute(), true
        );

        /*  タイトル設定  */
        timepickDlg.setTitle(getString(R.string.list_item_name_endtime));

        /*  ダイアログ表示  */
        timepickDlg.show();
    }

    /**
     * 確認間隔設定ダイアログを表示する
     */
    public void showIntervalSettingDialog(){

        /*  ダイアログ設定 */
        AlertDialog.Builder intervalSelectDialog = new AlertDialog.Builder(SendSettingActivity.this);

        /*  タイトル設定  */
        intervalSelectDialog.setTitle(getString(R.string.list_item_name_interval));

        /*  定義ファイルから選択可能時間（分単位）を取得 */
        final String[] selectableItems = getResources().getStringArray(R.array.noticenotice_interval_setting_selectable_items);

        /*  選択可能時間（分単位）の語尾に「分」を追加したリストを生成する */
        List<String> selectableDisplayItems = new ArrayList<String>();
        for (String item:selectableItems)selectableDisplayItems.add(item + getString(R.string.minute));

        /*  確認間隔設定ダイアログ生成   */
        intervalSelectDialog.setItems((String[])selectableDisplayItems.toArray(new String[]{}),new DialogInterface.OnClickListener() {
            /*  リスト選択時のリスナー   */
            public void onClick(DialogInterface dialog, int which) {
                /*  選択したアイテムを行に表示  */
                interval = selectableItems[which];
                intervalField.setText(selectableItems[which] + getApplicationContext().getString(R.string.minute));
            }
        });

        // 表示
        intervalSelectDialog.create().show();
    }

    /**
     * 曜日表示領域の文字列を生成する
     *
     * @return      曜日領域表示文字列
     *              1〜6個：曜日の連結文字列
     *              0個："none"
     *              全て："everyday"
     */
    public String createDayOfWeekText(DayOfWeek dayOfWeekChecks){

        /*  定義ファイルから選択可能アイテム（曜日）を取得 */
        final String[] dayOfWeekText = getResources().getStringArray(R.array.noticenotice_dayofweek_setting_display_items);

        /*  曜日チェックの状態    */
        StringBuilder rtnText = new StringBuilder();

        /*  チェックがONになっている曜日の数を算出する   */
        int checkedCount = 0;
        for (boolean dayChecked : dayOfWeekChecks) {
            if(dayChecked)checkedCount++;
        }

        /* 曜日が１つも選択されていない場合 */
        if(checkedCount == 0)return getString(R.string.dayofweek_none);

        /* 曜日が全て選択されている場合 */
        if(checkedCount == 7)return getString(R.string.dayofweek_everyday);

        /* 曜日を連結した文字列を生成する */
        for (int i = 0; i < dayOfWeekChecks.size(); i++) {
            if(dayOfWeekChecks.get(i))rtnText.append(dayOfWeekText[i]);
        }

        return rtnText.toString();
    }

    /**
     * Gmailアカウント設定ボタンを押下した時のコールバックメソッド
     * @param v View
     */
    private void onClickGmailButton(View v){
        /*  ダイアログ生成 */
        GoogleAccountChooser gac = new GoogleAccountChooser(SendSettingActivity.this);
        this.credential = gac.showAccountChooser(Arrays.asList(GmailScopes.GMAIL_SEND));
        startActivityForResult(this.credential.newChooseAccountIntent(), REQUEST_ACCOUNT_CHOOSER);
    }

    /**
     * 曜日設定ボタンを押下した時のコールバックメソッド
     * @param v View
     */
    private void onClickDayOfWeekButton(View v){
        /*  ダイアログ生成 */
        showDayOfWeekSetting(this.dayOfWeekChecks);
    }

    /**
     * 確認開始時刻設定ボタンを押下した時のコールバックメソッド
     * @param v View
     */
    private void onClickStartTimeButton(View v){
        /*  ダイアログ生成 */
        showStartTimeSetting();
    }

    /**
     * 確認終了設定ボタンを押下した時のコールバックメソッド
     * @param v View
     */
    private void onClickEndTimeButton(View v){
        /*  ダイアログ生成 */
        showEndTimeSetting();
    }

    /**
     * 確認間隔設定ボタンを押下した時のコールバックメソッド
     * @param v View
     */
    private void onClickIntevalButton(View v){
        /*  ダイアログ生成 */
        showIntervalSettingDialog();
    }

    /**
     * 完了ボタンを押下した時のコールバックメソッド
     * @param v View
     */
    private void onClickCompleteButton(View v){
         /*   端末内部へ設定値の一括保存   */
        saveUserData();

        /*  遷移元に値を返す（OKボタンが押下されました）    */
        setResult(NOTIFICATION_OK_PUSHED);

        /*   アクティビティを破棄し遷移前画面に戻る   */
        finish();
    }

    /**
     * 端末内部へユーザの設定値を保存する
     */
    private void saveUserData(){
        NoticeSaveData nsd = new NoticeSaveData(getApplicationContext());

        /*  端末内部へ曜日設定値の保存   */
        boolean[] dayOfWeek = dayOfWeekChecks.toBoolArray();
        for(int i = 0; i < dayOfWeek.length; i++) nsd.saveDayOfWeek(i, dayOfWeek[i]);

        /*  端末内部へ確認開始時刻の保存   */
        nsd.saveStartTime(startTime.toFormat());

        /*  端末内部へ確認終了時刻の保存   */
        nsd.saveEndTime(endTime.toFormat());

        /*  端末内部へ確認間隔の保存   */
        nsd.saveCheckInterval(interval);

        /*  端末内部へGmailアカウント名の保存   */
        nsd.saveUserGmailAccount(accountName);
    }

    /**
     * Gmailアカウントを選択した時のコールバックメソッド
     * @param data Intent
     */
    private void onSelectedGmailAccount(Intent data){
        /** 選択したアカウント名を取得    */
        accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        if (accountName != null) {
            Log.d("accountName:", accountName);

            /** 選択したアカウント名をOAuthに設定    */
            this.credential.setSelectedAccountName(accountName);

            /*  設定表示領域にアカウント名表示    */
            ResizableTextView tv = (ResizableTextView)findViewById(R.id.text_own_gmail_msg);
            tv.setText(accountName);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            /** Gmailアカウント選択時のコールバック    */
            case REQUEST_ACCOUNT_CHOOSER:
                if (resultCode == RESULT_OK && data != null) {
                    onSelectedGmailAccount(data);
                }
            break;
        }
    }

    /*****************************************************/
    /** インナークラス                                     */
    /*****************************************************/
    /**
     * 曜日毎の設定状態を格納するクラス（日〜土[0-6]）
     */
    private class DayOfWeek extends ArrayList<Boolean>{

        /**
         * コンストラクタ
         */
        DayOfWeek(){
            addAll(Arrays.asList(new Boolean[7]));
        }

        /**
         * 曜日の配列をboolean型の配列で返却する
         * @return boolean型の曜日毎の設定状態の配列
         */
        public boolean[] toBoolArray() {

            boolean boolArray[] = new boolean[7];
            for(int i = 0; i < boolArray.length; i++){
                boolArray[i] = super.get(i);
            }

            return boolArray;
        }
    }

    /**
     * 時刻クラス
     */
    private class Time{

        private int hour;   //時間
        private int minute; //分

        /**
         * コンストラクタ
         */
        Time(){
           this(0, 0);
        }

        /**
         * コンストラクタ
         * @param hour 時間
         * @param minute 分
         */
        Time(int hour, int minute){
            this.hour = hour;
            this.minute = minute;
        }

        /**
         * 時間と分を設定する
         * @param hour 時間
         * @param minute 分
         */
        public void setTime(int hour, int minute){
            this.hour = hour;
            this.minute = minute;
        }

        /**
         * 時間を取得する
         * @return 時間
         */
        public int getHour(){
            return hour;
        }

        /**
         * 分を取得する
         * @return 分
         */
        public int getMinute(){
            return minute;
        }

        /**
         * 時間と分をhh:mmの書式で返却する
         * @return フォーマット済み文字列
         */
        public String toFormat(){
            return String.format("%1$02d", hour) + ":" + String.format("%1$02d", minute);
        }
    }
}
