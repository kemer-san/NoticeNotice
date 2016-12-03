package com.ametice.noticenotice.google;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * クラス名 ：GmailSender
 * 説明    ：Gmailアカウントでメールを送信する
 * 最終更新 :2015/11/15
 * @version 1.1
 * @author  Y.Hiyoshi(ametis)
 */

public class GmailSender {

	private String accountName;         ///< アカウント名
	private String toText;              ///< 送信先
	private String subjectText;         ///< タイトル
	private String bodyText;            ///< 本文

	private Gmail mGmail;
	private GoogleAccountCredential mCredential;

    /**
     * コンストラクタ
     * @param credential Googleアカウント
     */
	public GmailSender(GoogleAccountCredential credential){
		this.mCredential = credential;
	}

	/**
     * メール送信
     * @param to 送信先
     * @param from 送信元
     * @param subject タイトル
     * @param bodyText 本文
     */
    public void sendGmail(String to, String from, String subject, String bodyText) {
        if (mGmail == null) {
            mGmail = buildGmail();
        }

	    this.toText = to;
        this.accountName = from;
	    this.subjectText = subject;
	    this.bodyText = bodyText;

        new ShowGmailTask().execute();
    }

    /**
     * Gmail認証
     */
    @NonNull
    private Gmail buildGmail() {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        GsonFactory factory = new GsonFactory();
        return new Gmail.Builder(transport, factory, mCredential).setApplicationName("NoticeNotice").build();
    }

	/**
     * メッセージ作成
     * @param to		送信先
     * @param from		送信元
     * @param subject	タイトル
     * @param bodyText	本文
     */
    public MimeMessage createEmail(String to, String from, String subject, String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText,"ISO-2022-JP");
        return email;
    }

    /**
     * メール送信
     * @param service		Gmailハンドル
     * @param userId		ユーザーID
     * @param emailContent	MimeMessageハンドル
     */
    public Message sendMessage(Gmail service, String userId, MimeMessage emailContent) throws MessagingException, IOException {
        Message message = createMessageWithEmail(emailContent);
        message = service.users().messages().send(userId, message).execute();

        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
        return message;
    }

    /**
     * メッセージ作成
     * @param emailContent	MimeMessageハンドル
     */
    public static Message createMessageWithEmail(MimeMessage emailContent)throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    /**
     * GmailTaskクラス
     */
    private class ShowGmailTask extends AsyncTask<Integer,Integer,Integer> {
        private Intent mIntent;
 		private MimeMessage msg;
        @Override
        protected Integer doInBackground(Integer... params) {
            try {
            	/**	メッセージ作成	*/	
                msg = createEmail(toText, accountName, subjectText, bodyText);

                /**	メール送信	*/
                sendMessage(mGmail, accountName, msg);
            } catch (UserRecoverableAuthIOException e) {
                mIntent = e.getIntent();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute( Integer reslt ) {
            if (mIntent != null) {
//                startActivityForResult(mIntent, REQUEST_AUTHORIZATION_FROM_GMAIL);
            } else {
//                Log.d(TAG, "msg:" + msg.toString());
            }
        }

    }
}
