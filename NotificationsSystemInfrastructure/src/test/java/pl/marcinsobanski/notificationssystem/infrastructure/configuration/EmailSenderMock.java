package pl.marcinsobanski.notificationssystem.infrastructure.configuration;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import pl.marcinsobanski.notificationssystem.application.sendnewprice.EmailSender;

import java.util.HashSet;
import java.util.Set;

@Configuration
@Primary
public class EmailSenderMock implements EmailSender {

    @Getter
    private final Set<Email> emailsStore = new HashSet<>();

    @Override
    public void send(Set<String> emails, String title, String message) {
        emailsStore.add(
                new Email(
                        emails, title, message
                )
        );
    }

    public void clear() {
        emailsStore.clear();
    }

    public record Email(
            Set<String> emails, String title, String message
    ) {
    }
}
