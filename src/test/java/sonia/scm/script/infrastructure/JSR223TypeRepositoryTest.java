package sonia.scm.script.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JSR223TypeRepositoryTest {

  private JSR223TypeRepository repository;

  @BeforeEach
  void beforeEach() {
    repository = new JSR223TypeRepository(ScriptEngineManagerProvider.context());
  }

  @Test
  void shouldReturnAtLeastGroovy() {
    List<String> types = repository.findAll();
    long count = types.stream().filter(t -> "Groovy".equals(t)).count();
    assertThat(count).isOne();
  }

  @Test
  void shouldReturnTypeByExtension() {
    Optional<String> groovy = repository.findByExtension("groovy");
    assertThat(groovy).isPresent();
  }
}
