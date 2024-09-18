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

import sonia.scm.xml.XmlInstantAdapter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
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
