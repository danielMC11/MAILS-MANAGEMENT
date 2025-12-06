package com.project.camunda.impl;

import com.project.camunda.MailProcessor;
import com.project.mails.Mail;

public class RemitirGestor implements MailProcessor {

    @Override
    public boolean supports(Mail mail) {
        return false;
    }

    @Override
    public void process(Mail mail) {

    }
}
