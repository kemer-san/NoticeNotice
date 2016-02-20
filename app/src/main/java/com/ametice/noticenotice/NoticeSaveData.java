package com.ametice.noticenotice;

import android.content.Context;

/**
 * クラス名 ：NoticeSaveData
 * 説明    ：NoticeNoticeで使用するデータの保存・読み出しを行う
 * 最終更新 :2015/11/21
 * @version 1.2
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

    /*  認証の有無   */
    public static final String NOTICE_SETTING_USER_REGISTRATION = "NOTICE_SETTING_USER_REGISTRATION";

    /*  曜日   */
    public static final int SUNDAY = 1;
    public static final int MONDAY = 2;
    public static final int THUSDAY = 3;
    public static final int WEDNESDAY = 4;
    public static final int THURSDAY = 5;
    public static final int FRIDAY = 6;
    public static final int SATURDAY = 7;

    /*  曜日キー   */
    private static final String NOTICE_SETTING_MONDAY = "NOTICE_SETTING_MONDAY";
    private static final String NOTICE_SETTING_THUSDAY = "NOTICE_SETTING_THUSDAY";
    private static final String NOTICE_SETTING_WEDNESDAY = "NOTICE_SETTING_WEDNESDAY";
    private static final String NOTICE_SETTING_THURSDAY = "NOTICE_SETTING_THURSDAY";
    private static final String NOTICE_SETTING_FRIDAY = "NOTICE_SETTING_FRIDAY";
    private static final String NOTICE_SETTING_SATURDAY = "NOTICE_SETTING_SATURDAY";
    private static final String NOTICE_SETTING_SUNDAY = "NOTICE_SETTING_SUNDAY";

    /*  初期化用    */
    private static final String INIT_STARTTIME = "9:0";
    private static final String INIT_ENDTIME = "18:0";
    private static final String INIT_INTERVAL = "60";

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

    /**
     * 登録の有無を保存
     * @param registFlag 登録フラグ
     */
    public void saveUserRegistration(boolean registFlag) {
        super.saveBooleanData(NOTICE_SETTING_USER_REGISTRATION, registFlag);
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
     * 登録の有無を読み込み
     */
    public boolean loadUserRegistration() {
        return super.loadBooleanData(NOTICE_SETTING_USER_REGISTRATION);
    }

    /**
     * 曜日のキーの生成
     * @param dayOfWeek 曜日
     * @return 曜日キー
     */
    private String toDayOfWeekKey(int dayOfWeek){
        switch (dayOfWeek) {
            case SUNDAY:return "NOTICE_SETTING_SUNDAY";
            case MONDAY:return  "NOTICE_SETTING_MONDAY";
            case THUSDAY:return "NOTICE_SETTING_THUSDAY";
            case WEDNESDAY:return "NOTICE_SETTING_WEDNESDAY";
            case THURSDAY:return "NOTICE_SETTING_THURSDAY";
            case FRIDAY:return "NOTICE_SETTING_FRIDAY";
            case SATURDAY:return "NOTICE_SETTING_SATURDAY";
            default:return "";
        }
    }

    /**
     * 端末内の設定値のチェック
     */
    public void checkUserData(){

        try {
            int cnt = 0;
            boolean dayOfWeek[] = new boolean[7];

            /*  曜日の設定状況の取得  */
            for(int i = 0; i < 7; i++){
                if(loadDayOfWeek(i) == true)cnt++;
            }

            /*  未設定の場合  */
            if(cnt == 0) {
                /*  全ての曜日の保存領域の生成  */
                for(int i = 0; i < 7; i++) saveDayOfWeek(i, false);
                /*  月曜日を設定  */
                saveDayOfWeek(MONDAY, true);
            }

        /*  エラーの場合  */
        }catch (Exception e){
            /*  全ての曜日の保存領域の生成  */
            for(int i = 0; i < 7; i++) saveDayOfWeek(i, false);
            /*  月曜日を設定  */
            saveDayOfWeek(MONDAY, true);
        }

        try {
            /*  開始時刻の設定状況の取得  */
            if(loadStartTime().equals("") == true){
                /*  未設定の場合は固定の時刻を設定   */
                saveStartTime(INIT_STARTTIME);
            }
        }catch (Exception e){
            /*  エラーの場合は固定の時刻を設定   */
            saveStartTime(INIT_STARTTIME);
        }

        try {
            /*  終了時刻の設定状況の取得  */
            if(loadEndTime().equals("") == true){
                /*  未設定の場合は任意の時刻を設定   */
                saveEndTime(INIT_ENDTIME);
            }
        }catch (Exception e){
            /*  エラーの場合は固定の時刻を設定   */
            saveEndTime(INIT_ENDTIME);
        }

        try {
            /*  確認間隔の設定状況の取得  */
            if(loadCheckInterval().equals("") == true){
               /*  未設定の場合は固定の確認間隔を設定   */
                saveCheckInterval(INIT_INTERVAL);
            }
        }catch (Exception e){
            /*  エラーの場合は固定の確認間隔を設定   */
            saveCheckInterval(INIT_INTERVAL);
        }

        try {
            /*  登録有無の設定状況の取得  */
            loadUserRegistration();
        }catch (Exception e){
            /*  エラーの場合は未登録を設定   */
            saveUserRegistration(false);
        }
    }
}