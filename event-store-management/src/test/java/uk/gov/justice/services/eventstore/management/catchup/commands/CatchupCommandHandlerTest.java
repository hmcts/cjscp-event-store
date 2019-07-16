package uk.gov.justice.services.eventstore.management.catchup.commands;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import uk.gov.justice.services.common.util.UtcClock;
import uk.gov.justice.services.eventstore.management.catchup.events.CatchupCompletedEvent;
import uk.gov.justice.services.eventstore.management.catchup.events.CatchupRequestedEvent;
import uk.gov.justice.services.jmx.api.command.CatchupCommand;
import uk.gov.justice.services.jmx.api.command.SystemCommand;
import uk.gov.justice.services.management.shuttering.events.ShutteringCompleteEvent;
import uk.gov.justice.services.management.shuttering.events.ShutteringRequestedEvent;
import uk.gov.justice.services.management.shuttering.events.UnshutteringRequestedEvent;

import java.time.ZonedDateTime;

import javax.enterprise.event.Event;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class CatchupCommandHandlerTest {

    @Mock
    private Event<ShutteringRequestedEvent> shutteringRequestedEventFirer;

    @Mock
    private Event<CatchupRequestedEvent> catchupRequestedEventFirer;

    @Mock
    private Event<UnshutteringRequestedEvent> unshutteringRequestedEventFirer;

    @Mock
    private UtcClock clock;

    @Mock
    private Logger logger;

    @InjectMocks
    private CatchupCommandHandler catchupCommandHandler;

    @Test
    public void shouldCallCatchupOnHandlingCommand() throws Exception {

        final ZonedDateTime now = new UtcClock().now();
        final CatchupCommand catchupCommand = new CatchupCommand();

        when(clock.now()).thenReturn(now);

        catchupCommandHandler.doCatchupWhilstShuttered(catchupCommand);

        final InOrder inOrder = inOrder(logger, shutteringRequestedEventFirer);

        inOrder.verify(logger).info("Catchup requested. Shuttering application first");
        inOrder.verify(shutteringRequestedEventFirer).fire(new ShutteringRequestedEvent(
                catchupCommand,
                now));
    }

    @Test
    public void shouldKickOffCatchupWhenShutteringCompleteIfCommandIsShutterCatchupCommand() throws Exception {

        final ShutteringCompleteEvent shutteringCompleteEvent = mock(ShutteringCompleteEvent.class);
        final CatchupCommand catchupCommand = new CatchupCommand();

        when(shutteringCompleteEvent.getTarget()).thenReturn(catchupCommand);

        catchupCommandHandler.onShutteringComplete(shutteringCompleteEvent);

        final InOrder inOrder = inOrder(logger, catchupRequestedEventFirer);

        inOrder.verify(logger).info("Received ShutteringComplete event. Now firing CatchupRequested event");
        inOrder.verify(catchupRequestedEventFirer).fire(new CatchupRequestedEvent(
                catchupCommand,
                clock.now()));
    }

    @Test
    public void shouldNotKickOffCatchupIfCommandIsNotShutterCatchupCommand() throws Exception {

        final ShutteringCompleteEvent shutteringCompleteEvent = mock(ShutteringCompleteEvent.class);
        final SystemCommand notAShutterCatchupCommand = mock(SystemCommand.class);

        when(shutteringCompleteEvent.getTarget()).thenReturn(notAShutterCatchupCommand);

        catchupCommandHandler.onShutteringComplete(shutteringCompleteEvent);

        verifyZeroInteractions(logger);
        verifyZeroInteractions(catchupRequestedEventFirer);
    }

    @Test
    public void shouldKickOffUnshutteringWhenCatchupCompleteIfCommandIsShutterCatchupCommand() throws Exception {

        final CatchupCompletedEvent catchupCompletedEvent = mock(CatchupCompletedEvent.class);
        final CatchupCommand catchupCommand = new CatchupCommand();

        when(catchupCompletedEvent.getTarget()).thenReturn(catchupCommand);

        catchupCommandHandler.onCatchupComplete(catchupCompletedEvent);

        final InOrder inOrder = inOrder(logger, unshutteringRequestedEventFirer);

        inOrder.verify(logger).info("Received CatchupCompleted event. Now firing UnshutteringRequested event");
        inOrder.verify(unshutteringRequestedEventFirer).fire(new UnshutteringRequestedEvent(
                catchupCommand,
                clock.now()));
    }
}
