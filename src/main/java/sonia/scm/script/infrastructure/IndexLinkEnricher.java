package sonia.scm.script.infrastructure;

import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.Index;
import sonia.scm.api.v2.resources.LinkAppender;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.LinkEnricher;
import sonia.scm.api.v2.resources.LinkEnricherContext;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.plugin.Extension;

import javax.inject.Inject;
import javax.inject.Provider;

@Extension
@Enrich(Index.class)
public class IndexLinkEnricher implements LinkEnricher {

  private final Provider<ScmPathInfoStore> scmPathInfoStore;

  @Inject
  public IndexLinkEnricher(Provider<ScmPathInfoStore> scmPathInfoStore) {
    this.scmPathInfoStore = scmPathInfoStore;
  }

  @Override
  public void enrich(LinkEnricherContext context, LinkAppender appender) {
    if (ScriptPermissions.isPermittedToRead()) {
      appender.appendOne("scripts", createListLink());
    }
  }

  private String createListLink() {
    LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStore.get().get(), ScriptResource.class);
    return linkBuilder.method("findAll").parameters().href();
  }
}
