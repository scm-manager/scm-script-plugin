package sonia.scm.script.domain;

import sonia.scm.xml.XmlInstantAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.Duration;
import java.time.Instant;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ExecutionResult {

  private boolean success;
  private String output;

  @XmlJavaTypeAdapter(XmlInstantAdapter.class)
  private Instant started;
  @XmlJavaTypeAdapter(XmlInstantAdapter.class)
  private Instant ended;

  ExecutionResult() {
  }

  public ExecutionResult(boolean success, String output, Instant started, Instant ended) {
    this.success = success;
    this.output = output;
    this.started = started;
    this.ended = ended;
  }

  public boolean isSuccess() {
    return success;
  }

  public String getOutput() {
    return output;
  }

  public Instant getStarted() {
    return started;
  }

  public Instant getEnded() {
    return ended;
  }

  @Override
  public String toString() {
    return new StringBuilder(success ? "success" : "failed")
      .append(" in ")
      .append(Duration.between(started, ended).toMillis()).append("ms")
      .append(" with output: ")
      .append(output)
      .toString();
  }
}
