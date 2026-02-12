package pl.marcinsobanski.notificationssystem.infrastructure.adapters.email;

public interface EmailSenderHandler {

    void send(String email, String title, String message);
}
