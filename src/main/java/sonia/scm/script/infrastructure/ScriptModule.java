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

import com.google.inject.AbstractModule;
import org.mapstruct.factory.Mappers;
import sonia.scm.plugin.Extension;
import sonia.scm.script.domain.EventTypeRepository;
import sonia.scm.script.domain.Executor;
import sonia.scm.script.domain.StorableScriptRepository;
import sonia.scm.script.domain.TypeRepository;

@Extension
public class ScriptModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(ScriptMapper.class).to(Mappers.getMapper(ScriptMapper.class).getClass());
    bind(ListenerMapper.class).to(Mappers.getMapper(ListenerMapper.class).getClass());

    bind(Executor.class).to(JSR223Executor.class);
    bind(TypeRepository.class).to(JSR223TypeRepository.class);
    bind(EventTypeRepository.class).to(DefaultEventTypeRepository.class);
    bind(StorableScriptRepository.class).to(StoreStorableScriptRepository.class);
  }
}
