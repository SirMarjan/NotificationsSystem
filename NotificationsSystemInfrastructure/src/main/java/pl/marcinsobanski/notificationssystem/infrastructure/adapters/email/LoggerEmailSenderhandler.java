package pl.marcinsobanski.notificationssystem.infrastructure.adapters.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoggerEmailSenderhandler implements EmailSenderHandler {

    @Override
    public void send(String email, String title, String message) {
        log.info("""
                Odbiorca: {}
                Tytuł : „{}”
                Treść: „{}”
                """, email, title, message);

    }
}
