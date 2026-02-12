package pl.marcinsobanski.notificationssystem.domain.template;

import lombok.Builder;
import lombok.Data;
import pl.marcinsobanski.notificationssystem.domain.template.rules.Rule;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class Template {

    private UUID id;
    private String title;
    private String content;
    private List<String> receiversEmails;
    private List<Rule> rules;
    private Instant creationTime;

}