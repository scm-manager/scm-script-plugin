package sonia.scm.script.infrastructure;

import com.github.legman.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.EagerSingleton;
import sonia.scm.plugin.Extension;
import sonia.scm.script.domain.EventListenerService;
import sonia.scm.script.domain.ExecutionContext;

import javax.inject.Inject;
import java.util.Optional;

@Extension
@EagerSingleton
public class EventListener {

  private static final Logger LOG = LoggerFactory.getLogger(EventListener.class);

  private final EventListenerService listenerService;

  @Inject
  public EventListener(EventListenerService listenerService) {
    this.listenerService = listenerService;
  }

  @Subscribe
  public void subscribeAsync(Object event) {
    handleEvent(event, true);
  }

  @Subscribe(async = false)
  public void subscribeSync(Object event) {
    handleEvent(event, false);
  }

  private void handleEvent(Object event, boolean async) {
    LOG.trace("received event {}, asnc: {}", event.getClass(), async);

    Optional<EventListenerService.Trigger> optionalTrigger = listenerService.createTrigger(event.getClass(), async);
    if (optionalTrigger.isPresent()) {
      LOG.debug("call trigger for event: {}, async: {}", event.getClass(), async);
      EventListenerService.Trigger trigger = optionalTrigger.get();

      ExecutionContext context = ExecutionContext.builder().withAttribute("event", event).build();
      trigger.execute(context);
    } else {
      LOG.trace("no trigger present for event: {}, async: {}", event.getClass(), async);
    }
  }

}
