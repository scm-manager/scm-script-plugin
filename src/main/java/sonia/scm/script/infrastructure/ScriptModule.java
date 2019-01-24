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
