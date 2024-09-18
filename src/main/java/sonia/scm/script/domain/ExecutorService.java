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

import jakarta.inject.Inject;

public class ExecutorService {

  private final StorableScriptRepository scriptRepository;
  private final Executor executor;

  @Inject
  public ExecutorService(StorableScriptRepository scriptRepository, Executor executor) {
    this.scriptRepository = scriptRepository;
    this.executor = executor;
  }

  public void execute(String id, ExecutionContext context) {
    StorableScript script = scriptRepository.findById(id).orElseThrow(() -> new ScriptNotFoundException(id));
    executor.execute(script, context);
  }
}
