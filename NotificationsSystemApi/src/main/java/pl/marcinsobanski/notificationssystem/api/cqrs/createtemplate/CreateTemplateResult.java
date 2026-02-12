package pl.marcinsobanski.notificationssystem.api.cqrs.createtemplate;

import java.util.UUID;

public record CreateTemplateResult(
        UUID templateId
) {
}