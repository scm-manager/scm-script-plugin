package sonia.scm.script.domain;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Script {

  private Id id;
  private Type type;
  private Description description;
  private Content content;
  private List<Listener> listeners = new ArrayList<>();

  public Script(Type type, Description description, Content content) {
    this.type = type;
    this.description = description;
    this.content = content;
  }

  public Script(Id id, Type type, Description description, Content content, List<Listener> listeners) {
    this.id = id;
    this.type = type;
    this.description = description;
    this.content = content;
    this.listeners = listeners;
  }

  public Optional<Id> getId() {
    return Optional.ofNullable(id);
  }

  public Type getType() {
    return type;
  }

  public Description getDescription() {
    return description;
  }

  public Content getContent() {
    return content;
  }

  public List<Listener> getListeners() {
    return ImmutableList.copyOf(listeners);
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

  public void changeDescription(Description description) {
    this.description = description;
  }
}
