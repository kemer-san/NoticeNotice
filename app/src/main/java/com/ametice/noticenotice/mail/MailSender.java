package com.ametice.noticenotice.mail;

import android.content.Context;

import com.ametice.noticenotice.R;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


/**
 * Created by hiro on 2016/02/20.
 */

public class MailSender {
    private Properties properties;  //システムプロパティ
    private Context context;        //アプリケーションコンテキスト


    /**
     * メール送信クラス
     * @param context   アプリケーションコンテキスト
     */
    public MailSender(Context context){
        this.context = context;
        properties = System.getProperties();
    }

    public void send(String MailAd,String subject, String body){
        // 送信用アドレス・パスワード
        String smtp_email = context.getString(R.string.smtp_email);
        String smtp_password = context.getString(R.string.smtp_password);

        try {
            //以下メール送信
            properties.put("mail.smtp.host",                "smtp.gmail.com");
            properties.put("mail.host",                     "smtp.gmail.com");
            properties.put("mail.smtp.port",                "465");
            properties.put("mail.smtp.socketFactory.port",  "465");
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

            // セッション
            Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator(){
                @Override
                protected PasswordAuthentication getPasswordAuthentication(){
                    return new PasswordAuthentication("smtp_email", "smtp_password");
                }
            });

            MimeMessage mimeMsg = new MimeMessage(session);

            mimeMsg.setSubject(subject, "utf-8");
            mimeMsg.setFrom(new InternetAddress(MailAd));
            mimeMsg.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(MailAd));

            final MimeBodyPart txtPart = new MimeBodyPart();
            txtPart.setText(body, "utf-8");

            final Multipart mp = new MimeMultipart();
            mp.addBodyPart(txtPart);
            mimeMsg.setContent(mp);

            // メール送信する。
            final Transport transport = session.getTransport("smtp");
            transport.connect(smtp_email,smtp_password);
            transport.sendMessage(mimeMsg, mimeMsg.getAllRecipients());
            transport.close();

        } catch (MessagingException e) {
            System.out.println("exception = " + e);

        } /*catch (UnsupportedEncodingException e) {
            必要あるのか不明
        }*/ finally {
            System.out.println("finish sending email");
        }
    }
}
