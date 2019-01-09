package sonia.scm.script.infrastructure;

import com.google.inject.util.Providers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.api.v2.resources.LinkAppender;
import sonia.scm.api.v2.resources.ScmPathInfoStore;

import java.net.URI;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndexLinkEnricherTest {

  @Mock
  private LinkAppender appender;

  @Mock
  private LinkAppender.LinkArrayBuilder arrayBuilder;

  private IndexLinkEnricher enricher;

  @BeforeEach
  void setUp() {
    ScmPathInfoStore pathInfoStore = new ScmPathInfoStore();
    pathInfoStore.set(() -> URI.create("/"));
    enricher = new IndexLinkEnricher(Providers.of(pathInfoStore));
  }

  @Test
  void shouldAppendLink() {
    when(appender.arrayBuilder("scripts")).thenReturn(arrayBuilder);

    enricher.enrich(null, appender);

    verify(arrayBuilder).append("list", "/v2/plugins/scripts");
    verify(arrayBuilder).append("run", "/v2/plugins/scripts/run");

    verify(arrayBuilder).build();
  }

}
