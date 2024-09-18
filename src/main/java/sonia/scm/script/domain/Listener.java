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

import lombok.Setter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@Setter
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Listener {

  private String eventType;
  private boolean asynchronous;

  public Listener() {
  }

  public Listener(Class<?> eventClass, boolean asynchronous) {
    this(eventClass.getName(), asynchronous);
  }

  public Listener(String eventType, boolean asynchronous) {
    this.eventType = eventType;
    this.asynchronous = asynchronous;
  }

  public String getEventType() {
    return eventType;
  }

  public boolean isAsynchronous() {
    return asynchronous;
  }

}
