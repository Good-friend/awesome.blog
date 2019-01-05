package org.awesome.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

/**
 * 邮件工具
 */
@Component
public class EmailUtils {

    private static final Logger LOG = LoggerFactory.getLogger(EmailUtils.class);

    /**
     * 使用QQ邮箱服务器发送邮件
     *
     * @param content 发送内容
     * @param title   发送标题
     * @param to      接收者邮箱
     */
    public void sendByQQ(String content, String title, String... to) {
        //发件人邮箱地址
        final String from = "xxxx@qq.com";
        //发件人称号，同邮箱地址
        final String user = "xxxx@qq.com";
        //发件人邮箱客户端授权码
        final String password = "*********";

        Properties properties = new Properties();
        //连接协议
        properties.put("mail.transport.protocol", "smtp");
        //主机名
        properties.put("mail.smtp.host", "smtp.qq.com");
        //端口号
        properties.put("mail.smtp.port", 465);
        //开启验证
        properties.put("mail.smtp.auth", "true");
        //使用ssl安全连接
        properties.put("mail.smtp.ssl.enable", "true");
        //设置调试显示
        properties.put("mail.debug", "true");
        //构建session
        Session session = Session.getInstance(properties);
        //获取邮件对象
        Message message = new MimeMessage(session);
        //设置发件人邮箱地址
        try {
            message.setFrom(new InternetAddress(from));
            // 设置收件人邮箱地址
            InternetAddress[] internetAddresses = new InternetAddress[to.length];
            for (int i = 0, len = internetAddresses.length; i < len; i++) {
                internetAddresses[i] = new InternetAddress(to[i]);
            }
            message.setRecipients(Message.RecipientType.TO, internetAddresses);
            // 设置邮件标题
            message.setSubject(title);
            // 设置邮件内容
            message.setText(content);
            // 得到邮差对象
            Transport transport = session.getTransport();
            // 连接自己的邮箱账户,密码为QQ邮箱开通的stmp服务后得到的客户端授权码
            transport.connect(from, password);
            // 发送邮件
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (MessagingException e) {
            LOG.error("Send email by QQ error. ", e.getMessage());
        }
    }

    /**
     * 使用网易邮箱服务器发送邮件
     *
     * @param content 发送内容
     * @param title   发送标题
     * @param to      接收者邮箱
     */
    public void sendBy163(String content, String title, String... to) {
        //发件人邮箱地址
        final String from = "geyaoln@163.com";
        //发件人称号，同邮箱地址
        final String user = "葛耀的小站管理员";
        //发件人邮箱客户端授权码
        final String password = "gy323898163";
        final String mailHost = "smtp.163.com";

        Properties props = new Properties();
        props.put("mail.smtp.host", mailHost);
        props.put("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(props);
        session.setDebug(true);
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(from));
            InternetAddress[] internetAddresses = new InternetAddress[to.length];
            for (int i = 0, len = internetAddresses.length; i < len; i++) {
                internetAddresses[i] = new InternetAddress(to[i]);
            }
            message.addRecipients(Message.RecipientType.TO, internetAddresses);
            //设置在发送给收信人之前给自己（发送方）抄送一份，不然会被当成垃圾邮件，报554错
            message.addRecipients(MimeMessage.RecipientType.CC, InternetAddress.parse(from));
            message.setSubject(title);//加载标题
            //向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
            Multipart multipart = new MimeMultipart();
            //设置邮件的文本内容
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setText(content);
            multipart.addBodyPart(contentPart);
            //将multipart对象放到message中
            message.setContent(multipart);
            //保存邮件
            message.saveChanges();
            //发送邮件
            Transport transport = session.getTransport("smtp");
            //连接服务器的邮箱
            transport.connect(mailHost, from, password);
            //发送
            transport.sendMessage(message, message.getAllRecipients());
            //关闭连接
            transport.close();
        } catch (Exception e) {
            LOG.error("Send email by 163 error. ", e.getMessage());
        }
    }
}
