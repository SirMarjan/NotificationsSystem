package pl.marcinsobanski.notificationssystem.api.cqrs.replacetemplate;

import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Command;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.models.Rule;

import java.util.List;
import java.util.UUID;

public record ReplaceTemplateCommand(
        UUID id,
        String title,
        String content,
        List<String> receiversEmails,
        List<Rule> rules
) implements Command<Void> {
}
