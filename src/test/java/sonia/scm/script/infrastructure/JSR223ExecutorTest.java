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

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import sonia.scm.script.domain.ExecutionContext;
import sonia.scm.script.domain.StorableScript;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JSR223ExecutorTest {

  @Mock
  private JSR223InternalExecutor internalExecutor;

  @InjectMocks
  private JSR223Executor executor;

  @Mock
  private Subject subject;

  @BeforeEach
  void beforeEach() {
    ThreadContext.bind(subject);
  }

  @AfterEach
  void afterEach() {
    ThreadContext.unbindSubject();
  }

  @Test
  void shouldDelegateExecution() {
    StorableScript script = new StorableScript("Groovy", "print \"Don't Panic\"");
    ExecutionContext context = ExecutionContext.empty();
    executor.execute(script, context);

    verify(internalExecutor).execute(script, context);
  }

  @Test
  void shouldThrowAuthorizationException() {
    doThrow(AuthorizationException.class).when(subject).checkPermission("script:execute");

    StorableScript script = new StorableScript("Groovy", "print \"Don't Panic\"");
    assertThrows(AuthorizationException.class, () -> executor.execute(script, ExecutionContext.empty()));
  }

}
