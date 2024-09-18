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

package sonia.scm.script.infrastructure;

import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;
import lombok.Getter;
import lombok.Setter;
import sonia.scm.script.domain.ExecutionHistoryEntry;

import java.util.List;

@Getter
@Setter
@SuppressWarnings("squid:S2160") // we need no equals or hashCode
public class ExecutionHistoryDto extends HalRepresentation {

  private List<ExecutionHistoryEntry> entries;

  public ExecutionHistoryDto() {
  }

  public ExecutionHistoryDto(Links links, List<ExecutionHistoryEntry> entries) {
    super(links);
    this.entries = entries;
  }
}
