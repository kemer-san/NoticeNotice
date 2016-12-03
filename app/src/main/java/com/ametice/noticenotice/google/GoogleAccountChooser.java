package com.ametice.noticenotice.google;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.Collection;

/**
 * クラス名 ：GoogleAccountChooser
 * 説明    ：Gmailアカウントを選択する
 * 最終更新 :2015/11/15
 * @version 1.1
 * @author  Y.Hiyoshi(ametis)
 */

public class GoogleAccountChooser extends AppCompatActivity {

	private Context context;
	private GoogleAccountCredential mCredential;
//	public static final int REQUEST_ACCOUNT_CHOOSER = 1;
	private String accountName;

	/**
	 * コンストラクタ
	 * @param context
	 */
	public GoogleAccountChooser(Context context) {
		this.context = context;
	}

	/**
	 * アカウント選択
	 * @param scopes
	 */
	public GoogleAccountCredential showAccountChooser(Collection<String> scopes) {
		mCredential = GoogleAccountCredential.usingOAuth2(context, scopes);

		return mCredential;
	}


//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		switch (requestCode) {
//			case REQUEST_ACCOUNT_CHOOSER:
//				if (resultCode == RESULT_OK && data != null) {
//					accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
//					Log.d("accountName:", accountName);
//					if (accountName != null) {
//						mCredential.setSelectedAccountName(accountName);
//					}
//				}
//				break;
//		}
//	}
}
