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

package sonia.scm.script.domain;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Setter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

@Setter
@XmlRootElement(name = "script")
@XmlAccessorType(XmlAccessType.FIELD)
public final class StorableScript extends Script {

  static final int CAPTURE_LIMIT = 25;

  @XmlTransient
  private String id;
  private String title;
  private String description;
  private List<Listener> listeners = new ArrayList<>();
  private boolean storeListenerExecutionResults = false;
  private Queue<ExecutionHistoryEntry> executionHistory = EvictingQueue.create(CAPTURE_LIMIT);

  public StorableScript() {
  }

  public StorableScript(String type, String content) {
    super(type, content);
  }

  public Optional<String> getId() {
    return Optional.ofNullable(id);
  }

  public Optional<String> getTitle() {
    return Optional.ofNullable(title);
  }

  public Optional<String> getDescription() {
    return Optional.ofNullable(description);
  }

  public List<Listener> getListeners() {
    return ImmutableList.copyOf(listeners);
  }

  public boolean isStoreListenerExecutionResults() {
    return storeListenerExecutionResults;
  }

  public boolean captureListenerExecution(Listener listener, ExecutionResult executionResult) {
    if (storeListenerExecutionResults) {
      this.executionHistory.add(new ExecutionHistoryEntry(listener, executionResult));
      return true;
    }
    return false;
  }

  public List<ExecutionHistoryEntry> getExecutionHistory() {
    return Lists.reverse(ImmutableList.copyOf(executionHistory));
  }

  public void addListener(Listener listener) {
    listeners.add(listener);
  }

  public void removeListener(Listener listener) {
    listeners.remove(listener);
  }

  public boolean isListeningSynchronous(String eventType) {
    Listener listener = findListener(eventType);
    return listener != null && !listener.isAsynchronous();
  }

  public boolean isListeningAsynchronous(String eventType) {
    Listener listener = findListener(eventType);
    return listener != null && listener.isAsynchronous();
  }

  private Listener findListener(String eventType) {
    for (Listener listener : listeners) {
      if (eventType.equals(listener.getEventType())) {
        return listener;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return String.format("%s (%s)", title, getType());
  }
}
