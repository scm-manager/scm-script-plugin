/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package sonia.scm.script.infrastructure;

import com.google.common.collect.ImmutableList;
import org.codehaus.groovy.reflection.ClassInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.plugin.Extension;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
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
