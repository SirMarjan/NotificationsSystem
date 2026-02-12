package pl.marcinsobanski.notificationssystem.domain.template;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class SimpleTemplate {

    private UUID id;
    private String title;
    private String content;
    private List<String> receiversEmails;

}