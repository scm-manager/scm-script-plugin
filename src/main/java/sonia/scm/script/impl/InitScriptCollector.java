package sonia.scm.script.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.SCMContext;
import sonia.scm.script.ScriptManager;
import sonia.scm.script.ScriptType;
import sonia.scm.script.ScriptUtil;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * InitScriptCollector is able to read {@link InitScript} objects from a directory. The directory is determined by the
 * {@link #PROPERTY_DIRECTORY} system property. if the system property is not set, the collector will use the
 * {@link #DEFAULT_DIRECTORY} in the scm home directory.
 */
public class InitScriptCollector {

  private static final Logger LOG = LoggerFactory.getLogger(InitScriptCollector.class);

  private static final String PROPERTY_DIRECTORY = "sonia.scm.init.script.d";
  private static final String DEFAULT_DIRECTORY = "init.script.d";

  private final ScriptManager manager;
  private final File directory;

  public InitScriptCollector(ScriptManager manager) {
    this.manager = manager;
    this.directory = findDirectory();
  }

  /**
   * Collects a list of scripts from the init script directory. It no script could be found or the directory does not
   * exists an empty list is returned.
   *
   * @return a list of collected scripts
   */
  public List<InitScript> collect() {
    if (directory.exists() && directory.isDirectory()) {
      return collectFromDirectory(directory);
    }

    LOG.info("init script directory {}, does not exists", directory);
    return Collections.emptyList();
  }

  private List<InitScript> collectFromDirectory(File directory) {
    List<InitScript> scripts = Lists.newArrayList();

    for (File file : directory.listFiles()) {
      ScriptType type = ScriptUtil.getTypeFromFile(manager, file);
      if (type != null) {
        scripts.add(new InitScript(type, file));
      } else {
        LOG.warn("could not find engine for script {}", file);
      }
    }

    return scripts;
  }

  private File findDirectory() {
    String path = System.getProperty(PROPERTY_DIRECTORY);
    if (Strings.isNullOrEmpty(path)) {
      return new File(SCMContext.getContext().getBaseDirectory(), DEFAULT_DIRECTORY);
    }
    return new File(path);
  }

}
