package sonia.scm.script.infrastructure;

import com.google.common.collect.ImmutableList;
import org.codehaus.groovy.reflection.ClassInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.plugin.Extension;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;

@Extension
public class ClearCacheListener implements ServletContextListener {

  private static final Logger LOG = LoggerFactory.getLogger(ClearCacheListener.class);

  @Override
  public void contextInitialized(ServletContextEvent sce) {

  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    LOG.info("try to clear groovy ClassInfo cache, to avoid ClassLoader leaks");

    List<ClassInfo> classes = ImmutableList.copyOf(ClassInfo.getAllClassInfo());
    for (ClassInfo classInfo : classes) {
      Class<?> theClass = classInfo.getTheClass();
      LOG.info("remove {} from groovy class cache", theClass);
      classInfo.finalizeReference();
      ClassInfo.remove(theClass);
    }
  }

}
