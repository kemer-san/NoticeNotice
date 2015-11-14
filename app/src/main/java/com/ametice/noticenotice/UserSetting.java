package com.ametice.noticenotice;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;
import android.widget.TimePicker;
import java.util.HashMap;

/**
 * クラス名 ：UserSetting
 * 説明    ：ダイアログで取得した設定値を端末内部へ保存する
 * 最終更新 :2015/11/15
 * @version 1.0
 * @author  Y.Hiyoshi(ametis)
 */
public class UserSetting {

    Context context;

    HashMap<Integer,String> dayOfWeekList = new HashMap<Integer,String>();

    /*  曜日チェックの状態    */
    static boolean dayOfWeekChecks[] = new boolean[7];

    /*  開始時刻    */
    static int setStartHour;
    static int setStartMinute;

    /*  終了時刻    */
    static int setEndHour;
    static int setEndMinute;

    /*  確認間隔   */
    static int setInterval;

    TextView tv;

    /**
     * コンストラクタ
     * @param context コンテキスト
     */
    public  UserSetting(Context context){
        this.context = context;

        /*  曜日リストの生成    */
        dayOfWeekList.put(0, "月");
        dayOfWeekList.put(1, "火");
        dayOfWeekList.put(2, "水");
        dayOfWeekList.put(3, "木");
        dayOfWeekList.put(4, "金");
        dayOfWeekList.put(5, "土");
        dayOfWeekList.put(6, "日");

        /*  端末内部から既存の設定値を取得    */
        //stub

    }

    /**
     * 曜日指定ダイアログ表示メソッド
     */
    public void onDayOfWeekSetting(TextView tv){

        final TextView msg = tv;

        /*  ダイアログ設定 */
        AlertDialog.Builder checkDlg = new AlertDialog.Builder(this.context);
        checkDlg.setTitle("確認開始時刻");

        /*  項目名 */
        final CharSequence[] chkItems = {"月曜日", "火曜日", "水曜日","木曜日", "金曜日", "土曜日", "日曜日"};

        checkDlg.setMultiChoiceItems(
                chkItems,
                dayOfWeekChecks,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int which, boolean flag) {
                        /*  各曜日のチェック状態を格納  */
                        dayOfWeekChecks[which] = flag;

                        /*   メッセージ変更   */
                        msg.setText(getDayOfWeekText());

                    }
                }
        );
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
                    setStartHour = setHour;
                    setStartMinute = SetMinute;

                    /*   メッセージ変更   */
                    msg.setText(getStartTimeText());
                }
            },
            setStartHour,
            setStartMinute,
            true
        );

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
                        setEndHour = setHour;
                        setEndMinute = SetMinute;

                        /*   メッセージ変更   */
                        msg.setText(getEndTimeText());
                    }
                },
                setEndHour,
                setEndMinute,
                true
        );

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

        listDlg.setTitle("確認間隔");

         /*  項目名 */
        final CharSequence[] items = {"5分", "10分", "15分", "30分", "60分"};

        listDlg.setItems(
                items,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                setInterval = 5;
                                break;
                            case 1:
                                setInterval = 10;
                                break;
                            case 2:
                                setInterval = 15;
                                break;
                            case 3:
                                setInterval = 30;
                                break;
                            case 4:
                                setInterval = 60;
                                break;
                            default:
                                setInterval = 60;
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
        String allDayOfWeek;
        StringBuilder strb = new StringBuilder();

        for(i = 0; i < 7; i++){
            if(dayOfWeekChecks[i] == true){
                strb.append(dayOfWeekList.get(i) + ",");
            }
        }

        allDayOfWeek = strb.toString();

        if(i == 0){
            allDayOfWeek = "none";
        }

        return allDayOfWeek;
    }

    /**
     * 設定された確認開始時刻の文字列取得メソッド
     */
    public String getStartTimeText(){
        return Integer.toString(setStartHour) + ":" + Integer.toString(setStartMinute);
    }

    /**
     * 設定された確認終了時刻の文字列取得メソッド
     */
    public String getEndTimeText(){
        return Integer.toString(setEndHour) + ":" + Integer.toString(setEndMinute);
    }

    /**
     * 設定された確認間隔の文字列取得メソッド
     */
    public String getIntervalText(){
        return Integer.toString(setInterval) + "分";
    }

    /**
     * 端末内部への設定値保存メソッド
     */
    public void saveTimeData(){
        NoticeSaveData nsd = new NoticeSaveData(context);

        /*  端末内部へ曜日設定値の保存   */
        for(int i = 0; i < 7; i++) nsd.saveDayOfWeek(i, dayOfWeekChecks[i]);

        /*  端末内部へ確認開始時刻の保存   */
        nsd.saveStartTime(Integer.toString(setStartHour) + ":" + Integer.toString(setStartMinute));

        /*  端末内部へ確認終了時刻の保存   */
        nsd.saveEndTime(Integer.toString(setEndHour) + ":" + Integer.toString(setEndMinute));

        /*  端末内部へ確認間隔の保存   */
        nsd.saveCheckInterval(Integer.toString(setInterval));
    }
}
