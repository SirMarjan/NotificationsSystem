package pl.marcinsobanski.notificationssystem.application.sendnewprice;

import java.util.Set;

public interface EmailSender {

    void send(Set<String> emails, String title, String message);
}