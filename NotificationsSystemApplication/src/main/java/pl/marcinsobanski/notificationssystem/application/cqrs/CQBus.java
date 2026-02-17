package pl.marcinsobanski.notificationssystem.application.cqrs;

import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Command;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Query;

public interface CQBus {

    <RESULT> RESULT executeCommand(Command<RESULT> command);

    <RESULT> RESULT executeQuery(Query<RESULT> query);
}
