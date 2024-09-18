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

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public class ExecutionContext {

  private final Map<String, Object> attributes;

  public ExecutionContext(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  public static ExecutionContext empty() {
    return builder().build();
  }

  public Map<String, Object> getAttributes() {
    return ImmutableMap.copyOf(attributes);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private Map<String, Object> attributes = new HashMap<>();

    public Builder withAttribute(String key, Object value) {
      attributes.put(key, value);
      return this;
    }

    public ExecutionContext build() {
      return new ExecutionContext(attributes);
    }

  }
}
