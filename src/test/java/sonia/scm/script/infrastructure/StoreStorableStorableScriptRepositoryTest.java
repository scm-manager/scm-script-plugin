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
