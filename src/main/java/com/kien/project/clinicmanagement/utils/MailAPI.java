package com.kien.project.clinicmanagement.utils;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class MailAPI {

    // Tài khoản Email và App password sau khi bật bảo mật 2 lớp
    private static final String FROM_EMAIL = "20221608@eaut.edu.vn";
    private static final String APP_PASSWORD = "fani ruby dnui fqho";

    public static boolean sendResetPasswordEmail(String toEmail, String newPassword) {
        return sendEmail(toEmail,
                "Your New Password - Clinic Management System",
                "Dear user,\n\n"
                        + "Your password has been reset.\n\n"
                        + "New Password: " + newPassword
                        + "\n\nPlease login and change your password as soon as possible.\n\n"
                        + "Regards,\nClinic Management System Team");
    }

    // ✅ Thêm hàm gửi mã xác minh
    public static boolean sendVerificationCode(String toEmail, String verificationCode) {
        return sendEmail(toEmail,
                "Your Verification Code - Clinic Management System",
                "Dear user,\n\n"
                        + "Your verification code is: " + verificationCode
                        + "\n\nPlease use this code to complete your verification.\n\n"
                        + "Regards,\nClinic Management System Team");
    }

    // Hàm chung để gửi email
    private static boolean sendEmail(String toEmail, String subject, String content) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, "Clinic Management"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
