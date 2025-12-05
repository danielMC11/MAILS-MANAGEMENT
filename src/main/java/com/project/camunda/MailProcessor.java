package com.project.camunda;

import com.project.mails.Mail;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.stereotype.Component;


public interface MailProcessor {

    boolean supports(Mail mail);

    void process(Mail mail);

}
