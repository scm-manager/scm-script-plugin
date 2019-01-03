package sonia.scm.script.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sonia.scm.script.domain.Content;
import sonia.scm.script.domain.Description;
import sonia.scm.script.domain.Id;
import sonia.scm.script.domain.Script;
import sonia.scm.script.domain.ScriptRepository;
import sonia.scm.script.domain.Type;
import sonia.scm.store.InMemoryDataStoreFactory;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class StoreScriptRepositoryTest {

  private ScriptRepository scriptRepository;

  @BeforeEach
  void setUpObjectUnderTest() {
    scriptRepository = new StoreScriptRepository(new InMemoryDataStoreFactory());
  }

  private Script createSample() {
    return new Script(Type.valueOf("groovy"), Description.valueOf("sample"), Content.valueOf("print 'Hello'"));
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
    script.changeDescription(Description.valueOf("My Sample"));
    scriptRepository.store(script);

    Optional<Script> byId = scriptRepository.findById(script.getId().get());

    assertThat(byId.get().getDescription()).isEqualTo(Description.valueOf("My Sample"));
  }

  @Test
  void shouldReturnScriptFromStore() {
    Script script = scriptRepository.store(createSample());
    Optional<Script> byId = scriptRepository.findById(script.getId().get());
    assertThat(byId).isPresent();
  }

  @Test
  void shouldReturnEmptyOptionalForUnknownIds() {
    Optional<Script> byId = scriptRepository.findById(Id.valueOf("123"));
    assertThat(byId).isNotPresent();
  }

  @Test
  void shouldRemoveScriptFromStore() {
    Script script = scriptRepository.store(createSample());

    Id id = script.getId().get();
    scriptRepository.remove(id);

    Optional<Script> byId = scriptRepository.findById(id);
    assertThat(byId).isNotPresent();
  }

  @Test
  void shouldReturnAllStoredScripts() {
    scriptRepository.store(createSample());
    scriptRepository.store(createSample());
    scriptRepository.store(createSample());

    List<Script> all = scriptRepository.findAll();
    assertThat(all).hasSize(3);
  }

  @Test
  void shouldReturnEmptyListIfNoScriptWasStored() {
    List<Script> all = scriptRepository.findAll();
    assertThat(all).isEmpty();
  }

}
