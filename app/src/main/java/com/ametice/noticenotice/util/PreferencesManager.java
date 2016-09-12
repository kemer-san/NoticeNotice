package com.ametice.noticenotice.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * クラス名 ：内部データ管理
 * 説明    ：Android内部へデータの保存・読み出しを行う
 * @version 1.0
 * @author  Y.Hiyoshi(ametis)
 */
public class PreferencesManager {

    SharedPreferences prefs;
    Context context;
    String listName;

    /**
     * コンストラクタ
     * @param context コンテキスト
     * @param listName リスト名
     */
    public PreferencesManager(Context context, String listName){
        /*   値の初期化   */
        this.context = context;
        this.listName = listName;

        /*   MODE_MULTI_PROCESSに設定   */
        this.prefs = context.getSharedPreferences(listName, Context.MODE_MULTI_PROCESS);
    }

    /**
     * 内部への文字列の保存
     * @param keyword キー
     * @param inputWord 入力文字列
     */
    public void saveStringData(String keyword, String inputWord) {

        /*   文字列の格納   */
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(keyword, inputWord);

        /*   保存領域へコミット   */
        editor.apply();
    }

    /**
     * 内部から文字列の取り出し
     * @param keyword キー
     * @return 文字列
     */
    public String loadStringData(String keyword){
        return  prefs.getString(keyword, "");
    }

    /**
     * 内部への数値の保存
     * @param keyword キー
     * @param inputvalue 数値
     */
    public void saveIntData(String keyword, int inputvalue) {

        /*   数値の格納   */
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(keyword, inputvalue);

        /*   数値のコミット   */
        editor.apply();
    }

    /**
     * 内部から数値の取り出し
     * @param keyword キー
     * @return 数値
     */
    public int loadIntData(String keyword){
        return  prefs.getInt(keyword, 0);
    }

    /**
     * 内部へのbool値の保存
     * @param keyword キー
     * @param b       bool値
     */
    public void saveBooleanData(String keyword, boolean b) {

        /*   文字列の格納   */
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(keyword, b);

        /*   保存領域へコミット   */
        editor.apply();
    }

    /**
     * 内部からbool値の取り出し
     * @param keyword キー
     * @return bool値
     */
    public boolean loadBooleanData(String keyword){
        return  prefs.getBoolean(keyword, false);
    }
}
