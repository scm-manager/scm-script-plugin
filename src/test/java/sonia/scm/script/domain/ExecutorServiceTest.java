package sonia.scm.script.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.script.ScriptTestData;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExecutorServiceTest {

  @Mock
  private Executor executor;

  @Mock
  private StorableScriptRepository repository;

  @Mock
  private ExecutionContext executionContext;

  @InjectMocks
  private ExecutorService service;

  @Test
  void shouldExecuteTheScript() {
    StorableScript script = ScriptTestData.createHelloWorld();

    when(repository.findById("42")).thenReturn(Optional.of(script));

    service.execute("42", executionContext);

    verify(executor).execute(script, executionContext);
  }

  @Test
  void shouldThrowScriptNotFoundException() {
    when(repository.findById("42")).thenReturn(Optional.empty());

    assertThrows(ScriptNotFoundException.class, () -> service.execute("42", executionContext));
  }
}
