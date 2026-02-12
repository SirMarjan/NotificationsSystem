package pl.marcinsobanski.notificationssystem.api.cqrs.createtemplate;

import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Command;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.models.Rule;

import java.util.List;

public record CreateTemplateCommand(
        String title,
        String content,
        List<String> receiversEmails,
        List<Rule> rules
) implements Command<CreateTemplateResult> {
}