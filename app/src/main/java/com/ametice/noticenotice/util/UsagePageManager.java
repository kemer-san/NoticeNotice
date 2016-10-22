package com.ametice.noticenotice.util;

/**
 * 使い方ページ管理クラス(Singleton)
 * TODO:外部xmlファイルから設定を取得できるようにする
 * Created by kenzo on 2016/02/20.
 */
public class UsagePageManager {

    //region "定数・変数"

    /**
     * 唯一のインスタンス
     */
    private static final UsagePageManager Instance = new UsagePageManager();

    /**
     * ディレクトリパス
     */
    private static String DIRECTORY_PATH = "file:///android_asset/pages/";

    /**
     * ファイル名
     */
    private static String[] FileNames = {
            "about.html",
            "usage.html",
            "security.html"
    };

    /**
     * タイトル
     */
    private static String[] Titles = {
            "概要",
            "使い方",
            "セキュリティ"
    };

    //endregion

    /**
     * コンストラクタ
     */
    private UsagePageManager() {

    }

    //region "メソッド"

    public static UsagePageManager getInstance() {
        return Instance;
    }

    /**
     * セクション番号からファイル名を取得します。
     *
     * @param sectionNumber セクション番号
     * @return ファイル名
     */
    public String getPageFileName(int sectionNumber) {
        if (sectionNumber < 0 || FileNames.length < sectionNumber) return null;

        return FileNames[sectionNumber - 1];
    }

    /**
     * セクション番号からタイトルを取得します。
     *
     * @param sectionNumber セクション番号
     * @return ページタイトル
     */
    public String getPageTitle(int sectionNumber) {
        if (sectionNumber < 0 || Titles.length < sectionNumber) return null;

        return Titles[sectionNumber - 1];
    }

    /**
     * セクション番号からファイルのURLを取得します。
     *
     * @param sectionNumber セクション番号
     * @return ファイルURL
     */
    public String getPageFileUrl(int sectionNumber) {
        return DIRECTORY_PATH.concat(getPageFileName(sectionNumber));
    }

    /**
     * 総ページ数を取得します。
     *
     * @return ページ数
     */
    public int getPageCount() {
        return FileNames.length;
    }

    //endregion
}

