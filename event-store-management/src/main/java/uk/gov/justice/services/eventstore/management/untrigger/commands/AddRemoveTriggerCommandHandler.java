package uk.gov.justice.services.eventstore.management.untrigger.commands;

import static java.lang.String.format;
import static uk.gov.justice.services.jmx.api.command.AddTriggerCommand.ADD_TRIGGER;
import static uk.gov.justice.services.jmx.api.command.RemoveTriggerCommand.REMOVE_TRIGGER;

import uk.gov.justice.services.eventstore.management.untrigger.process.EventLogTriggerManipulator;
import uk.gov.justice.services.jmx.api.command.AddTriggerCommand;
import uk.gov.justice.services.jmx.api.command.RemoveTriggerCommand;
import uk.gov.justice.services.jmx.command.HandlesSystemCommand;

import javax.inject.Inject;

import org.slf4j.Logger;

public class AddRemoveTriggerCommandHandler {

    @Inject
    private EventLogTriggerManipulator eventLogTriggerManipulator;

    @Inject
    private Logger logger;

    @HandlesSystemCommand(ADD_TRIGGER)
    public void addTriggerToEventLogTable(final AddTriggerCommand addTriggerCommand) {

        logger.info(format("Received command %s", addTriggerCommand.getName()));

        eventLogTriggerManipulator.addTriggerToEventLogTable();
    }

    @HandlesSystemCommand(REMOVE_TRIGGER)
    public void removeTriggerFromEventLogTable(final RemoveTriggerCommand removeTriggerCommand) {

        logger.info(format("Received command %s", removeTriggerCommand.getName()));

        eventLogTriggerManipulator.removeTriggerFromEventLogTable();
    }
}
