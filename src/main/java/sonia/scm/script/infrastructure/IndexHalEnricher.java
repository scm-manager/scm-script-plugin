package sonia.scm.script.infrastructure;

import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.Index;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.HalEnricher;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.plugin.Extension;

import javax.inject.Inject;
import javax.inject.Provider;

@Extension
@Enrich(Index.class)
public class IndexHalEnricher implements HalEnricher {

  private final Provider<ScmPathInfoStore> scmPathInfoStore;

  @Inject
  public IndexHalEnricher(Provider<ScmPathInfoStore> scmPathInfoStore) {
    this.scmPathInfoStore = scmPathInfoStore;
  }

  @Override
  public void enrich(HalEnricherContext context, HalAppender appender) {
    if (ScriptPermissions.isPermittedToRead()) {
      appender.appendLink("scripts", createListLink());
    }
  }

  private String createListLink() {
    LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStore.get().get(), ScriptResource.class);
    return linkBuilder.method("findAll").parameters().href();
  }
}
