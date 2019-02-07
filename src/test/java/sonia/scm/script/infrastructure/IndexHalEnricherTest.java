package sonia.scm.script.infrastructure;

import com.google.inject.util.Providers;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.ScmPathInfoStore;

import java.net.URI;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndexHalEnricherTest {

  @Mock
  private HalAppender appender;

  @Mock
  private Subject subject;

  private IndexHalEnricher enricher;

  @BeforeEach
  void setUp() {
    ScmPathInfoStore pathInfoStore = new ScmPathInfoStore();
    pathInfoStore.set(() -> URI.create("/"));
    enricher = new IndexHalEnricher(Providers.of(pathInfoStore));

    ThreadContext.bind(subject);
  }

  @AfterEach
  void tearDown() {
    ThreadContext.unbindSubject();
  }

  @Test
  void shouldAppendLink() {
    when(subject.isPermitted("script:read")).thenReturn(true);

    enricher.enrich(null, appender);

    verify(appender).appendLink("scripts", "/v2/plugins/scripts");
  }

  @Test
  void shouldNotAppendAnyLink() {
    enricher.enrich(null, appender);

    verify(appender, never()).appendLink("scripts", "/v2/plugins/scripts");
  }

}
