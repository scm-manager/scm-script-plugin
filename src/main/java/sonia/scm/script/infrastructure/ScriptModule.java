package sonia.scm.script.infrastructure;

import com.google.inject.AbstractModule;
import sonia.scm.plugin.Extension;
import sonia.scm.script.domain.Executor;
import sonia.scm.script.domain.StorableScriptRepository;
import sonia.scm.script.domain.TypeRepository;

@Extension
public class ScriptModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(ScriptMapper.class).to(ScriptMapperImpl.class);
    bind(Executor.class).to(JSR223Executor.class);
    bind(TypeRepository.class).to(JSR223TypeRepository.class);
    bind(StorableScriptRepository.class).to(StoreStorableScriptRepository.class);
  }
}
