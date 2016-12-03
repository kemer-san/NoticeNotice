package com.ametice.noticenotice.setting;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ametice.noticenotice.data.NoticeSaveData;

import java.util.HashMap;

/**
 * クラス名 ：UserNoticeSetting
 * 説明    ：ダイアログで取得した設定値を端末内部へ保存する
 * 最終更新 :2015/11/15
 * @version 1.1
 * @author  Y.Hiyoshi(ametis)
 */
public class UserNoticeSetting {

    Context context;

    /*  曜日リスト    */
    HashMap<Integer,String> dayOfWeekList = new HashMap<Integer,String>();

    /*  曜日チェックの状態    */
    private boolean dayOfWeekChecks[] = new boolean[7];

    /*  開始時刻    */
    private int startTimeHour = 0;
    private int startTimeMinute = 0;

    /*  終了時刻    */
    private int endTimeHour = 0;
    private int endTimeMinute = 0;

    /*  確認間隔   */
    private int interval = 0;

    /**UserNoticeSetting
     * コンストラクタ
     * @param context コンテキスト
     */
    public  UserNoticeSetting(Context context){
        this.context = context;

        /*  曜日リストの生成    */
        dayOfWeekList.put(0, "日");
        dayOfWeekList.put(1, "月");
        dayOfWeekList.put(2, "火");
        dayOfWeekList.put(3, "水");
        dayOfWeekList.put(4, "木");
        dayOfWeekList.put(5, "金");
        dayOfWeekList.put(6, "土");

        /*  前回値を選択済みにする（日〜土[0-6]）    */
        for(int i = 0; i < 7; i++){
            dayOfWeekChecks[i] = new NoticeSaveData(this.context).loadDayOfWeek(i);
        }

        try {
             /*  前回値を設定する  */
            String splitStartTime[] = new NoticeSaveData(this.context).loadStartTime().split(":", 2);
            startTimeHour = Integer.parseInt(splitStartTime[0]);
            startTimeMinute = Integer.parseInt(splitStartTime[1]);
        }catch (Exception e){
            endTimeHour = 0;
            startTimeMinute = 0;
        }

        try {
            /*  前回値を設定する  */
            String splitEndTime[] = new NoticeSaveData(this.context).loadEndTime().split(":", 2);
            endTimeHour = Integer.parseInt(splitEndTime[0]);
            endTimeMinute = Integer.parseInt(splitEndTime[1]);
        }catch (Exception e){
            startTimeHour = 0;
            startTimeMinute = 0;
        }

        try {
            /*  前回値を設定する  */
            interval = Integer.parseInt(new NoticeSaveData(this.context).loadCheckInterval());
        }catch(Exception e){
            interval = 60;
        }
    }

    /**
     * 曜日指定ダイアログ表示メソッド
     */
    public void onDayOfWeekSetting(TextView tv){

        final TextView msg = tv;

        /*  確定前曜日情報 */
        final boolean tempChecks[] = new boolean[7];

        /*  既存の曜日情報をコピー  */
        for(int i = 0; i < 7; i++){
            tempChecks[i] = dayOfWeekChecks[i];
        }

        /*  ダイアログ設定 */
        AlertDialog.Builder checkDlg = new AlertDialog.Builder(this.context);

        /*  タイトル設定  */
        checkDlg.setTitle("曜日指定");

        /*  項目名 */
        final CharSequence[] chkItems = {"日曜日", "月曜日", "火曜日", "水曜日","木曜日", "金曜日", "土曜日"};

        /*  曜日選択ダイアログの生成   */
        checkDlg.setMultiChoiceItems(
                chkItems,
                tempChecks,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int which, boolean flag) {
                        /*  各曜日のチェック状態を格納  */
                        tempChecks[which]=flag;
                    }
                }
        );

        /*  OKボタンを定義    */
        checkDlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                /*  曜日情報を格納  */
                for(int i = 0; i < 7; i++){
                    dayOfWeekChecks[i] = tempChecks[i];
                }
                /*   メッセージ変更   */
                msg.setText(getDayOfWeekText());
            }
        });

        /*  ダイアログ表示  */
        checkDlg.create().show();
    }

    /**
     * 確認開始時刻ダイアログ表示メソッド
     */
    public void onStartTimeSetting(TextView tv){

        final TextView msg = tv;

        /*  時間選択ダイアログの生成    */
        TimePickerDialog timepickDlg= new TimePickerDialog(
                context,
                new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int setHour, int SetMinute) {
                    /*   開始時刻を格納   */
                        startTimeHour = setHour;
                        startTimeMinute = SetMinute;

                    /*   メッセージ変更   */
                        msg.setText(getStartTimeText());
                    }
                },
                startTimeHour,
                startTimeMinute,
                true
        );

        /*  タイトル設定  */
        timepickDlg.setTitle("確認開始時刻");

        /*  ダイアログ表示  */
        timepickDlg.show();
    }

    /**
     * 確認終了時刻ダイアログ表示メソッド
     */
    public void onEndTimeSetting(TextView tv){

        final TextView msg = tv;

        /*  時間選択ダイアログの生成    */
        TimePickerDialog timepickDlg= new TimePickerDialog(
                context,
                new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int setHour, int SetMinute) {
                        /*   開始時刻を格納   */
                        endTimeHour = setHour;
                        endTimeMinute = SetMinute;

                        /*   メッセージ変更   */
                        msg.setText(getEndTimeText());
                    }
                },
                endTimeHour,
                endTimeMinute,
                true
        );

        /*  タイトル設定  */
        timepickDlg.setTitle("確認終了時刻");

        /*  ダイアログ表示  */
        timepickDlg.show();
    }

    /**
     * 確認間隔設定ダイアログ表示メソッド
     */
    public void onIntervalSetting(TextView tv){

        final TextView msg = tv;

        /*  ダイアログ設定 */
        AlertDialog.Builder listDlg = new AlertDialog.Builder(context);

        /*  タイトル設定  */
        listDlg.setTitle("確認間隔");

         /*  項目名 */
        final CharSequence[] items = {"5分", "10分", "15分", "30分", "60分"};

        /*  確認間隔設定ダイアログ生成   */
        listDlg.setItems(
                items,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                interval = 5;
                                break;
                            case 1:
                                interval = 10;
                                break;
                            case 2:
                                interval = 15;
                                break;
                            case 3:
                                interval = 30;
                                break;
                            case 4:
                                interval = 60;
                                break;
                            default:
                                interval = 60;
                                break;
                        }

                        /*   メッセージ変更   */
                        msg.setText(getIntervalText());
                    }
                });

        // 表示
        listDlg.create().show();
    }

    /**
     * 設定された曜日の文字列取得メソッド
     */
    public String getDayOfWeekText(){

        int i = 0;
        int cnt = 0;
        String allDayOfWeek;
        StringBuilder strb = new StringBuilder();

        for(i = 0; i < 7; i++){
            if(dayOfWeekChecks[i] == true){
                strb.append(dayOfWeekList.get(i));
                cnt++;
            }
        }

        /*  文字列のコピー */
        allDayOfWeek = strb.toString();

        /* 曜日指定なし */
        if(cnt == 0) {allDayOfWeek = "none";}
        /* 毎日指定 */
        if(cnt == 7) {allDayOfWeek = "everyday";}

        return allDayOfWeek;
    }

    /**
     * 設定された確認開始時刻の文字列取得メソッド
     */
    public String getStartTimeText(){
        return String.format("%1$02d", startTimeHour) + ":" + String.format("%1$02d", startTimeMinute);
    }

    /**
     * 設定された確認終了時刻の文字列取得メソッド
     */
    public String getEndTimeText(){
        return String.format("%1$02d", endTimeHour) + ":" + String.format("%1$02d", endTimeMinute);
    }

    /**
     * 設定された確認間隔の文字列取得メソッド
     */
    public String getIntervalText(){
        return Integer.toString(interval) + "分";
    }

    /**
     * 端末内部への設定値保存メソッド
     */
    public void saveTimeData(){
        NoticeSaveData nsd = new NoticeSaveData(context);

        /*  端末内部へ曜日設定値の保存   */
        for(int i = 0; i < 7; i++) nsd.saveDayOfWeek(i, dayOfWeekChecks[i]);

        /*  端末内部へ確認開始時刻の保存   */
        nsd.saveStartTime(toTimeFormat(startTimeHour, startTimeMinute));

        /*  端末内部へ確認終了時刻の保存   */
        nsd.saveEndTime(toTimeFormat(endTimeHour, endTimeMinute));

        /*  端末内部へ確認間隔の保存   */
        nsd.saveCheckInterval(Integer.toString(interval));
    }

    /**
     * 端末内部へのGmailアカウント設定
     */
    public void saveUserGmailData(String gmailAccount){
        NoticeSaveData nsd = new NoticeSaveData(context);

        /*  端末内部へ確認間隔の保存   */
        nsd.saveUserGmailAccount(gmailAccount);
    }

    /**
     * 表示用時刻フォーマット変換
     * @param hour 時間
     * @param minute 分
     * @return 表示用時刻文字列
     */
    private String toTimeFormat(int hour, int minute){
        return Integer.toString(hour) + ":" + Integer.toString(minute);
    }
}
