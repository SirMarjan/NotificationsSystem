package pl.marcinsobanski.notificationssystem.api.cqrs.templatedetails;

import pl.marcinsobanski.notificationssystem.api.cqrs.common.models.Rule;

import java.util.List;

public record TemplateDetailsView(
        String title,
        String content,
        List<String> receiversEmails,
        List<Rule> rules
) {
}