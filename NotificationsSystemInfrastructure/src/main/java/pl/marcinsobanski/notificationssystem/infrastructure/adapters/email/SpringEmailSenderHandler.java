package pl.marcinsobanski.notificationssystem.infrastructure.adapters.email;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Profile("docker")
@RequiredArgsConstructor
@Slf4j
public class SpringEmailSenderHandler implements EmailSenderHandler {

    private final JavaMailSender javaMailSender;

    @Override
    @Async("mailExecutor")
    public void send(String email, String title, String message) {
        try {
            final var mimeMessage = javaMailSender.createMimeMessage();
            final var helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setFrom("bezimienny@zbiornik.pl");
            helper.setTo(email);
            helper.setSubject(title);
            helper.setText(message, false);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("Error", e);
        }
    }
}
