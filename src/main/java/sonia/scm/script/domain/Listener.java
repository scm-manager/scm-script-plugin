package sonia.scm.script.domain;

import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Setter
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Listener {

  private Class<?> eventType;
  private boolean asynchronous;

  public Listener() {
  }

  public Listener(Class<?> eventType, boolean asynchronous) {
    this.eventType = eventType;
    this.asynchronous = asynchronous;
  }

  public Class<?> getEventType() {
    return eventType;
  }

  public boolean isAsynchronous() {
    return asynchronous;
  }

}
