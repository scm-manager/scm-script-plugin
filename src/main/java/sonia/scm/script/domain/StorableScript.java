package sonia.scm.script.domain;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
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

  public boolean isListeningSynchronous(Class<?> eventType) {
    Listener listener = findListener(eventType);
    return listener != null && !listener.isAsynchronous();
  }

  public boolean isListeningAsynchronous(Class<?> eventType) {
    Listener listener = findListener(eventType);
    return listener != null && listener.isAsynchronous();
  }

  private Listener findListener(Class<?> eventType) {
    for (Listener listener : listeners) {
      if (listener.getEventType().isAssignableFrom(eventType)) {
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
