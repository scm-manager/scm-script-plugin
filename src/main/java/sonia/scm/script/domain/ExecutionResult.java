/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package sonia.scm.script.domain;

import sonia.scm.xml.XmlInstantAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static java.util.Optional.of;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ExecutionResult {

  private boolean success;
  private String output;

  @XmlJavaTypeAdapter(XmlInstantAdapter.class)
  private Instant started;
  @XmlJavaTypeAdapter(XmlInstantAdapter.class)
  private Instant ended;
  @XmlTransient
  private Throwable exception;

  ExecutionResult() {
  }

  public ExecutionResult(String output, Instant started, Instant ended) {
    this(true, output, started, ended, null);
  }

  public ExecutionResult(Throwable exception, String output, Instant started, Instant ended) {
    this(false, output, started, ended, exception);
  }

  private ExecutionResult(boolean success, String output, Instant started, Instant ended, Throwable exception) {
    this.success = success;
    this.output = output;
    this.started = started;
    this.ended = ended;
    this.exception = exception;
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

  public Optional<Throwable> getException() {
    return of(exception);
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
