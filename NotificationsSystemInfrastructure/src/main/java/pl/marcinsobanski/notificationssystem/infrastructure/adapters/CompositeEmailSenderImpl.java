package pl.marcinsobanski.notificationssystem.infrastructure.adapters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.marcinsobanski.notificationssystem.application.sendnewprice.EmailSender;
import pl.marcinsobanski.notificationssystem.infrastructure.adapters.email.EmailSenderHandler;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CompositeEmailSenderImpl implements EmailSender {

    private final List<EmailSenderHandler> emailSenderHandlers;

    @Override
    public void send(Set<String> emails, String title, String message) {
        emailSenderHandlers.forEach(
                emailSenderHandler -> emails.forEach(
                        email -> emailSenderHandler.send(email, title, message)
                )
        );
    }
}
