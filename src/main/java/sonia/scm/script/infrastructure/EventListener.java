/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package sonia.scm.script.infrastructure;

import com.github.legman.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.EagerSingleton;
import sonia.scm.plugin.Extension;
import sonia.scm.script.domain.EventListenerService;
import sonia.scm.script.domain.ExecutionContext;
import sonia.scm.web.security.AdministrationContext;

import jakarta.inject.Inject;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Extension
@EagerSingleton
public class EventListener {

  private static final Logger LOG = LoggerFactory.getLogger(EventListener.class);

  private final EventListenerService listenerService;

  // we need the internal executor, because we have to skip permission checks for the execution
  private final JSR223InternalExecutor executor;

  // we need the AdministrationContext to read all scripts, even if the user does not have the privileges
  private final AdministrationContext administrationContext;

  @Inject
  public EventListener(EventListenerService listenerService, JSR223InternalExecutor executor, AdministrationContext administrationContext) {
    this.listenerService = listenerService;
    this.executor = executor;
    this.administrationContext = administrationContext;
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
    Optional<EventListenerService.Trigger> optionalTrigger = createTrigger(event, async);
    if (optionalTrigger.isPresent()) {
      LOG.debug("call trigger for event: {}, async: {}", event.getClass(), async);
      EventListenerService.Trigger trigger = optionalTrigger.get();

      ExecutionContext context = ExecutionContext.builder().withAttribute("event", event).build();
      trigger.execute(executor, context);
      store(trigger);
    } else {
      LOG.trace("no trigger present for event: {}, async: {}", event.getClass(), async);
    }
  }

  private void store(EventListenerService.Trigger trigger) {
    // use administration context to write all executed scripts, even without script:modify
    administrationContext.runAsAdmin(trigger::store);
  }

  private Optional<EventListenerService.Trigger> createTrigger(Object event, boolean async) {
    // use administration context to read all scripts and skip the permission check
    AtomicReference<Optional<EventListenerService.Trigger>> optionalTrigger = new AtomicReference<>();
    administrationContext.runAsAdmin(() -> optionalTrigger.set(listenerService.createTrigger(event.getClass(), async)));
    return optionalTrigger.get();
  }


}
