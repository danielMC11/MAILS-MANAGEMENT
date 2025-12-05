package com.project.camunda.impl;

import com.project.camunda.MailProcessor;
import com.project.mails.Mail;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StartMailProcess implements MailProcessor {


    @Autowired
    private RuntimeService runtimeService;

    @Value("${gmail.address}")
    private String gmailAddress;

    @Override
    public void process(Mail mail) {
        runtimeService.startProcessInstanceByKey("mails-management-process");
    }

    @Override
    public boolean supports(Mail mail) {

        return mail.getFrom().equals(gmailAddress) && mail.getCc().equals(gmailAddress);
    }
}
