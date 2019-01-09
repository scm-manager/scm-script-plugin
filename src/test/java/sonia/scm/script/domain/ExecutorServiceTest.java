package sonia.scm.script.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
  private ScriptRepository repository;

  @Mock
  private ExecutionContext executionContext;

  @InjectMocks
  private ExecutorService service;

  @Test
  void shouldExecuteTheScript() {
    Id id = Id.valueOf("42");
    Script script = new Script(id, Type.valueOf("groovy"), Title.valueOf("Heart Of Gold"), Description.valueOf("Heart Of Gold"), Content.valueOf(""), new ArrayList<>());

    when(repository.findById(id)).thenReturn(Optional.of(script));

    service.execute(id, executionContext);

    verify(executor).execute(script, executionContext);
  }

  @Test
  void shouldThrowScriptNotFoundException() {
    Id id = Id.valueOf("42");

    when(repository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ScriptNotFoundException.class, () -> service.execute(id, executionContext));
  }
}
