package uk.gov.justice.services.eventstore.management.commands;

import uk.gov.justice.services.jmx.api.command.BaseSystemCommand;

public class RebuildCommand extends BaseSystemCommand {

    public static final String REBUILD = "REBUILD";
    private static final String DESCRIPTION = "Rebuilds PublishedEvents and renumbers the Events";

    public RebuildCommand() {
        super(REBUILD, DESCRIPTION);
    }
}
