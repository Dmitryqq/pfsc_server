package com.pfscServer.service;

import com.pfscServer.domain.ApplicationUser;
import com.pfscServer.domain.Commit;

import javax.mail.MessagingException;
import java.io.IOException;

public interface MailSenderService {
    void send(ApplicationUser user, boolean status, Commit commit, String messageText)throws MessagingException, IOException;
}
