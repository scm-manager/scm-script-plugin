package sonia.scm.script.domain;

import com.google.common.collect.ImmutableList;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Setter
@XmlRootElement(name = "script")
@XmlAccessorType(XmlAccessType.FIELD)
public final class Script {

  private String id;
  private String type;
  private String title;
  private String description;
  private String content;
  private List<Listener> listeners = new ArrayList<>();

  public Script() {
  }

  public Script(String type, String content) {
    this.type = type;
    this.content = content;
  }

  public Script(String id, String type, String title, String description, String content, List<Listener> listeners) {
    this.id = id;
    this.type = type;
    this.title = title;
    this.description = description;
    this.content = content;
    this.listeners = listeners;
  }

  public Optional<String> getId() {
    return Optional.ofNullable(id);
  }

  public String getType() {
    return type;
  }

  public Optional<String> getTitle() {
    return Optional.ofNullable(title);
  }

  public Optional<String> getDescription() {
    return Optional.ofNullable(description);
  }

  public String getContent() {
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
}
