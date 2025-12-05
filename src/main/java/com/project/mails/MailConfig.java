package com.project.mails;

import com.project.camunda.MailProcessor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.function.Consumer;

@Configuration
public class MailConfig {


    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MailConfig.class);


    @Autowired
    private List<MailProcessor> mailProcessors;

    @Bean
    public Consumer<Mail> mailHandler() {
        return mail -> {

            LOG.info(mail.toString());

            mailProcessors.stream()
                    .filter(p -> p.supports(mail))
                    .findFirst()
                    .ifPresentOrElse(
                            processor -> processor.process(mail),
                            () -> LOG.info("No action defined for this mail")
                    );
        };
    }
}