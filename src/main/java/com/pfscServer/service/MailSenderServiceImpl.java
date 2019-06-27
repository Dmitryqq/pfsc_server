package com.pfscServer.service;

import com.pfscServer.domain.ApplicationUser;
import com.pfscServer.domain.Commit;
import com.pfscServer.domain.Config;
import com.pfscServer.domain.Mark;
import com.pfscServer.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class MailSenderServiceImpl implements MailSenderService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private FilesRepo fileRepo;
    @Autowired
    private MarksRepo markRepo;
    @Autowired
    private ConfigsRepo configRepo;

    @Value("${spring.mail.username}")
    private String username;



    @Override
    public void send(ApplicationUser user, boolean status, Commit commit, String messageText) throws MessagingException, IOException {
        Mark mark = markRepo.findById(commit.getMarkId()).orElse(null);
        sendEngine(status, commit, user, messageText);
        Config rootDir = configRepo.findFirstByName("ABD");
        if (status == true) {
            MimeMessage message = mailSender.createMimeMessage();
            boolean multipart = true;
            MimeMessageHelper mailMessage = new MimeMessageHelper(message, multipart);
            mailMessage.setFrom(username);//поменять
            mailMessage.setTo(rootDir.getValue());//кому передать вычислить
            mailMessage.setSubject("Прошу принять накат");//тему тоже подписать
            String text = mailTemplateFile("ABD.txt");
            text = text.replaceFirst("nameABD", user.getName());
            text = text.replaceFirst("priz", mark.getName());
            text = text.replaceFirst("opic", commit.getDescription());
            mailMessage.setText(text);//описание вытащить из бд
            List<String> allFile = fileRepo.allFiles(commit.getId(), 2l);
            if (allFile.size() != 0) {

                int i = 0;
                for (String tempFile : allFile) {
                    i++;
                    FileSystemResource file = new FileSystemResource(new File(tempFile));
                    mailMessage.addAttachment("Накат файл " + i, file);
                }
            }

            mailSender.send(message);

        }
    }

    private void sendEngine(boolean status, Commit commit, ApplicationUser user, String messageText) throws IOException {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(username);
        mailMessage.setTo(user.getEmail());
        String messageStatus = (status) ? "принят" : "отклонен";
        mailMessage.setSubject("Накат был " + messageStatus);
        String text = mailTemplateFile("prog.txt");
        text = text.replaceFirst("name", user.getName());
        text = text.replaceFirst("NumberCommit", commit.getNumber() + "");
        text = text.replaceFirst("data", commit.getCreateDate() + "");
        text = text.replaceFirst("status", messageStatus);
        if (messageStatus.equals("принят")) {
            text = text.replaceFirst("Becose", "");
        } else {
            text = text.replaceFirst("Becose", messageText);
        }

        mailMessage.setText(text);
        mailSender.send(mailMessage);
    }

    private String mailTemplateFile(String nameFile) throws IOException {
        List<String> tempText = Files.readAllLines(Paths.get("src/main/resources/mailTemplates/" + nameFile), Charset.forName("windows-1251"));
        String text = "";
        for (String temp : tempText) {
            text += temp + "\n";
        }
        return text;
    }

    public void sendOp(String emailTo, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(username);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }

}