package sonia.scm.script.infrastructure;

import com.google.common.collect.Sets;
import com.google.inject.util.Providers;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.api.v2.resources.LinkAppender;
import sonia.scm.api.v2.resources.ScmPathInfoStore;

import java.net.URI;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndexLinkEnricherTest {

  @Mock
  private LinkAppender appender;

  @Mock
  private LinkAppender.LinkArrayBuilder arrayBuilder;

  @Mock
  private Subject subject;

  private IndexLinkEnricher enricher;

  @BeforeEach
  void setUp() {
    ScmPathInfoStore pathInfoStore = new ScmPathInfoStore();
    pathInfoStore.set(() -> URI.create("/"));
    enricher = new IndexLinkEnricher(Providers.of(pathInfoStore));

    ThreadContext.bind(subject);
  }

  @AfterEach
  void tearDown() {
    ThreadContext.unbindSubject();
  }

  @Test
  void shouldAppendLink() {
    assignPermissions("script:read", "script:execute");

    when(appender.arrayBuilder("scripts")).thenReturn(arrayBuilder);
    enricher.enrich(null, appender);

    verify(arrayBuilder).append("list", "/v2/plugins/scripts");
    verify(arrayBuilder).append("run", "/v2/plugins/scripts/run");

    verify(arrayBuilder).build();
  }

  @Test
  void shouldNotAppendListLink() {
    assignPermissions("script:execute");

    when(appender.arrayBuilder("scripts")).thenReturn(arrayBuilder);
    enricher.enrich(null, appender);

    verify(arrayBuilder).append("run", "/v2/plugins/scripts/run");
    verify(arrayBuilder).build();
  }

  @Test
  void shouldNotAppendRunLink() {
    assignPermissions("script:read");

    when(appender.arrayBuilder("scripts")).thenReturn(arrayBuilder);
    enricher.enrich(null, appender);

    verify(arrayBuilder).append("list", "/v2/plugins/scripts");
    verify(arrayBuilder).build();
  }

  @Test
  void shouldNotAppendAnyLink() {
    when(appender.arrayBuilder("scripts")).thenReturn(arrayBuilder);
    enricher.enrich(null, appender);
    verify(arrayBuilder).build();
  }

  private void assignPermissions(String ...permissions) {
    Set<String> assigned = Sets.newHashSet(permissions);
    when(subject.isPermitted(anyString())).thenAnswer(ic -> assigned.contains(ic.getArgument(0)));
  }

}
