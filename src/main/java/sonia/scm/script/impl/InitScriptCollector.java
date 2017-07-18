/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */
package sonia.scm.script.impl;

import com.google.common.annotations.VisibleForTesting;
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

  @VisibleForTesting
  protected InitScriptCollector(ScriptManager manager, File directory) {
    this.manager = manager;
    this.directory = directory;
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

    Collections.sort(scripts);

    return Collections.unmodifiableList(scripts);
  }

  private File findDirectory() {
    String path = System.getProperty(PROPERTY_DIRECTORY);
    if (Strings.isNullOrEmpty(path)) {
      return new File(SCMContext.getContext().getBaseDirectory(), DEFAULT_DIRECTORY);
    }
    return new File(path);
  }

}
