package com.ametice.noticenotice;

import android.content.Context;

/**
 * クラス名 ：NoticeNotice内部データ参照クラス
 * 説明    ：NOticeNoticeで使用するデータの保存・読み出しを行う
 * @version 1.0
 * @author  Y.Hiyoshi(ametis)
 */
public class NoticeSaveData extends PreferencesManager {

    /*  アプリデータ保存先   */
    public static final String NOTICE_DATALIST = "NOTICE_DATALIST";

    /*  メールアドレス   */
    public static final String NOTICE_USER_ADDRESS = "USER_ADDRESS";

    /*  パスコード   */
    public static final String NOTICE_USER_PASSCODE = "USER_PASSCODE";

    /*  開始時刻   */
    public static final String NOTICE_SETTING_STARTTIME = "NOTICE_SETTING_STARTTIME";

    /*  終了時刻   */
    public static final String NOTICE_SETTING_ENDTIME = "NOTICE_SETTING_ENDTIME";

    /*  チェック間隔   */
    public static final String NOTICE_SETTING_CHECK_INTERVAL = "NOTICE_SETTING_CHECK_INTERVAL";

    /*  曜日   */
    public static final int MONDAY = 0;
    public static final int THUSDAY = 1;
    public static final int WEDNESDAY = 2;
    public static final int THURSDAY = 3;
    public static final int FRIDAY = 4;
    public static final int SATURDAY = 5;
    public static final int SUNDAY = 6;

    /*  曜日キー   */
    private static final String NOTICE_SETTING_MONDAY = "NOTICE_SETTING_MONDAY";
    private static final String NOTICE_SETTING_THUSDAY = "NOTICE_SETTING_THUSDAY";
    private static final String NOTICE_SETTING_WEDNESDAY = "NOTICE_SETTING_WEDNESDAY";
    private static final String NOTICE_SETTING_THURSDAY = "NOTICE_SETTING_THURSDAY";
    private static final String NOTICE_SETTING_FRIDAY = "NOTICE_SETTING_FRIDAY";
    private static final String NOTICE_SETTING_SATURDAY = "NOTICE_SETTING_SATURDAY";
    private static final String NOTICE_SETTING_SUNDAY = "NOTICE_SETTING_SUNDAY";

    /**
     * コンストラクタ
     * @param context コンテキスト
     */
    public NoticeSaveData(Context context){
        super(context, NOTICE_DATALIST);
    }

    /************/
    /*  setter  */
    /************/

    /**
     * メールアドレスの保存
     * @param address メールアドレス
     */
    public void saveUserAddress(String address) {
        super.saveStringData(NOTICE_USER_ADDRESS, address);
    }

    /**
     * パスコードの保存
     * @param passcode パスコード
     */
    public void saveUserPassCode(String passcode) {
        super.saveStringData(NOTICE_USER_PASSCODE, passcode);
    }

    /**
     * 開始時刻の保存
     * @param startTime 開始時刻
     */
    public void saveStartTime(String startTime) {
        super.saveStringData(NOTICE_SETTING_STARTTIME, startTime);
    }

    /**
     * 終了時刻の保存
     * @param endTime 終了時刻
     */
    public void saveEndTime(String endTime) {
        super.saveStringData(NOTICE_SETTING_ENDTIME, endTime);
    }

    /**
     * チェック間隔の保存
     * @param checkInterval チェック間隔
     */
    public void saveCheckInterval(String checkInterval) {
        super.saveStringData(NOTICE_SETTING_CHECK_INTERVAL, checkInterval);
    }

    /**
     * 曜日のチェックフラグの保存
     * @param dayOfWeek 曜日
     * @param checkFlag チェックフラグ
     */
    public void saveDayOfWeek(int dayOfWeek, boolean checkFlag) {
        super.saveBooleanData(toDayOfWeekKey(dayOfWeek), checkFlag);
    }

    /************/
    /*  getter  */
    /************/

    /**
     * メールアドレスの読み込み
     */
    public String loadUserAddress(){
        return  super.loadStringData(NOTICE_USER_ADDRESS);
    }

    /**
     * パスコードの読み込み
     */
    public String loadUserPassCode(){
        return  super.loadStringData(NOTICE_USER_PASSCODE);
    }

    /**
     * 開始時刻の読み込み
     */
    public String loadStartTime(){
        return  super.loadStringData(NOTICE_SETTING_STARTTIME);
    }

    /**
     * 終了時刻の読み込み
     */
    public String loadEndTime(){
        return  super.loadStringData(NOTICE_SETTING_ENDTIME);
    }

    /**
     * チェック間隔の読み込み
     */
    public String loadCheckInterval(){
        return  super.loadStringData(NOTICE_SETTING_CHECK_INTERVAL);
    }

    /**
     * 曜日のチェックフラグの読み込み
     * @param dayOfWeek 曜日
     */
    public boolean loadDayOfWeek(int dayOfWeek) {
        return super.loadBooleanData(toDayOfWeekKey(dayOfWeek));
    }

    /**
     * 曜日のキーの生成
     * @param dayOfWeek 曜日
     * @return 曜日キー
     */
    private String toDayOfWeekKey(int dayOfWeek){
        switch (dayOfWeek) {
            case MONDAY:return  "NOTICE_SETTING_MONDAY";
            case THUSDAY:return "NOTICE_SETTING_THUSDAY";
            case WEDNESDAY:return "NOTICE_SETTING_WEDNESDAY";
            case THURSDAY:return "NOTICE_SETTING_THURSDAY";
            case FRIDAY:return "NOTICE_SETTING_FRIDAY";
            case SATURDAY:return "NOTICE_SETTING_SATURDAY";
            case SUNDAY:return "NOTICE_SETTING_SUNDAY";
            default:return "";
        }
    }
}