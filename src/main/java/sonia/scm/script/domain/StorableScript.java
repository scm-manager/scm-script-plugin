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
