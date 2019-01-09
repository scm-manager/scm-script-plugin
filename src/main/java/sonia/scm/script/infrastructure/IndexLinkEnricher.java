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
    LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStore.get().get(), ScriptResource.class);

    LinkAppender.LinkArrayBuilder builder = appender.arrayBuilder("scripts");

    // TODO check permissions

    builder.append("list", createListLink(linkBuilder));
    builder.append("run", createRunLink(linkBuilder));
    builder.build();
  }

  private String createListLink(LinkBuilder builder) {
    return builder.method("findAll").parameters().href();
  }

  private String createRunLink(LinkBuilder builder) {
    return builder.method("run").parameters().href();
  }
}
