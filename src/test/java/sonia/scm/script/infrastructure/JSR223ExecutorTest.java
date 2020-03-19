/**
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
