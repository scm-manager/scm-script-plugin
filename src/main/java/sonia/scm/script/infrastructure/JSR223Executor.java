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

import sonia.scm.script.domain.ExecutionContext;
import sonia.scm.script.domain.ExecutionResult;
import sonia.scm.script.domain.Executor;
import sonia.scm.script.domain.Script;

import jakarta.inject.Inject;

/**
 * The {@link JSR223Executor} checks permissions and delegates the execution of the script to the
 * {@link JSR223InternalExecutor}.
 */
public class JSR223Executor implements Executor {

  private final JSR223InternalExecutor executor;

  @Inject
  public JSR223Executor(JSR223InternalExecutor executor) {
    this.executor = executor;
  }

  @Override
  public ExecutionResult execute(Script script, ExecutionContext context) {
    ScriptPermissions.checkExecute();
    return executor.execute(script, context);
  }
}
