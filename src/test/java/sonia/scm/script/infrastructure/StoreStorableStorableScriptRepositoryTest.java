/*
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.script.domain.StorableScript;
import sonia.scm.script.domain.StorableScriptRepository;
import sonia.scm.store.InMemoryDataStoreFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class StoreStorableStorableScriptRepositoryTest {

  @Mock
  private Subject subject;

  private StorableScriptRepository scriptRepository;

  @BeforeEach
  void setUpObjectUnderTest() {
    scriptRepository = new StoreStorableScriptRepository(new InMemoryDataStoreFactory());
    ThreadContext.bind(subject);
  }

  @AfterEach
  void tearDown() {
    ThreadContext.unbindSubject();
  }

  private StorableScript createSample() {
    return new StorableScript("groovy", "print 'Hello'");
  }

  @Test
  void shouldCreateAnIdForTheScript() {
    StorableScript script = createSample();

    StorableScript storedScript = scriptRepository.store(script);
    assertThat(storedScript.getId()).isPresent();
  }

  @Test
  void shouldModifyStoredScript() {
    StorableScript script = scriptRepository.store(createSample());
    script.setDescription("My Sample");
    scriptRepository.store(script);

    Optional<StorableScript> byId = scriptRepository.findById(script.getId().get());

    assertThat(byId.get().getDescription()).contains("My Sample");
  }

  @Test
  void shouldReturnScriptFromStore() {
    StorableScript script = scriptRepository.store(createSample());
    Optional<StorableScript> byId = scriptRepository.findById(script.getId().get());
    assertThat(byId).isPresent();
  }

  @Test
  void shouldReturnEmptyOptionalForUnknownIds() {
    Optional<StorableScript> byId = scriptRepository.findById("123");
    assertThat(byId).isNotPresent();
  }

  @Test
  void shouldRemoveScriptFromStore() {
    StorableScript script = scriptRepository.store(createSample());

    String id = script.getId().get();
    scriptRepository.remove(id);

    Optional<StorableScript> byId = scriptRepository.findById(id);
    assertThat(byId).isNotPresent();
  }

  @Test
  void shouldReturnAllStoredScripts() {
    String one = scriptRepository.store(createSample()).getId().get();
    String two = scriptRepository.store(createSample()).getId().get();
    String three = scriptRepository.store(createSample()).getId().get();

    List<StorableScript> all = scriptRepository.findAll();
    assertThat(all).hasSize(3);

    List<String> ids = all.stream().map(s -> s.getId().get()).collect(Collectors.toList());
    assertThat(ids).containsOnly(one, two, three);
  }

  @Test
  void shouldThrowAuthorizationExceptionsWithoutModifyPermission() {
    doThrow(AuthorizationException.class).when(subject).checkPermission("script:modify");

    StorableScript sample = createSample();
    assertThrows(AuthorizationException.class, () -> scriptRepository.store(sample));
    assertThrows(AuthorizationException.class, () -> scriptRepository.remove("42"));
  }

  @Test
  void shouldThrowAuthorizationExceptionsWithoutReadPermission() {
    doThrow(AuthorizationException.class).when(subject).checkPermission("script:read");

    assertThrows(AuthorizationException.class, () -> scriptRepository.findById("42"));
    assertThrows(AuthorizationException.class, () -> scriptRepository.findAll());
  }

  @Test
  void shouldReturnEmptyListIfNoScriptWasStored() {
    List<StorableScript> all = scriptRepository.findAll();
    assertThat(all).isEmpty();
  }

}
