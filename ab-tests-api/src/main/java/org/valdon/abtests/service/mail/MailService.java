package org.valdon.abtests.service.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.valdon.abtests.dto.user.UserCreatedEvent;
import org.valdon.abtests.config.props.MailProperties;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final MailProperties mailProperties;

    @SneakyThrows
    public void sendRegistrationMail(UserCreatedEvent event) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

        helper.setSubject("Добро пожаловать, " + event.name());
        helper.setFrom(mailProperties.getUsername(), "Сервис A/B тестирования");
        helper.setTo(event.username());

        String emailContent = getRegistrationContent(event);
        helper.setText(emailContent, true);

        mailSender.send(mimeMessage);
    }

    private String getRegistrationContent(UserCreatedEvent event) {
        Context context = new Context();
        context.setVariable("name", event.name());
        context.setVariable("token", event.token());
        return templateEngine.process("registration", context);
    }

}