package com.authentication.authenticationapp.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {
        try {
            // Construct the email
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("senderauthapp@bnr.uat");
            mailMessage.setTo(to);
            mailMessage.setSubject("Your OTP");
            mailMessage.setText("Your OTP is: " + otp + ". It will expire in 5 minutes.");

            // Send the email
            mailSender.send(mailMessage);
        }
        catch (MailException e){
            e.printStackTrace();
        }

    }
}

