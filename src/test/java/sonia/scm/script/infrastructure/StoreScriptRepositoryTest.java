package sonia.scm.script.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sonia.scm.script.domain.Script;
import sonia.scm.script.domain.ScriptRepository;
import sonia.scm.store.InMemoryDataStoreFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class StoreScriptRepositoryTest {

  private ScriptRepository scriptRepository;

  @BeforeEach
  void setUpObjectUnderTest() {
    scriptRepository = new StoreScriptRepository(new InMemoryDataStoreFactory());
  }

  private Script createSample() {
    return new Script("groovy", "print 'Hello'");
  }

  @Test
  void shouldCreateAnIdForTheScript() {
    Script script = createSample();

    Script storedScript = scriptRepository.store(script);
    assertThat(storedScript.getId()).isPresent();
  }

  @Test
  void shouldModifyStoredScript() {
    Script script = scriptRepository.store(createSample());
    script.setDescription("My Sample");
    scriptRepository.store(script);

    Optional<Script> byId = scriptRepository.findById(script.getId().get());

    assertThat(byId.get().getDescription()).contains("My Sample");
  }

  @Test
  void shouldReturnScriptFromStore() {
    Script script = scriptRepository.store(createSample());
    Optional<Script> byId = scriptRepository.findById(script.getId().get());
    assertThat(byId).isPresent();
  }

  @Test
  void shouldReturnEmptyOptionalForUnknownIds() {
    Optional<Script> byId = scriptRepository.findById("123");
    assertThat(byId).isNotPresent();
  }

  @Test
  void shouldRemoveScriptFromStore() {
    Script script = scriptRepository.store(createSample());

    String id = script.getId().get();
    scriptRepository.remove(id);

    Optional<Script> byId = scriptRepository.findById(id);
    assertThat(byId).isNotPresent();
  }

  @Test
  void shouldReturnAllStoredScripts() {
    String one = scriptRepository.store(createSample()).getId().get();
    String two = scriptRepository.store(createSample()).getId().get();
    String three = scriptRepository.store(createSample()).getId().get();

    List<Script> all = scriptRepository.findAll();
    assertThat(all).hasSize(3);

    List<String> ids = all.stream().map(s -> s.getId().get()).collect(Collectors.toList());
    assertThat(ids).containsOnly(one, two, three);
  }

  @Test
  void shouldReturnEmptyListIfNoScriptWasStored() {
    List<Script> all = scriptRepository.findAll();
    assertThat(all).isEmpty();
  }

}
