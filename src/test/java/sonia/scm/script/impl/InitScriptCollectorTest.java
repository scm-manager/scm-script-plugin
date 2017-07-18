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

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import sonia.scm.script.ScriptManager;
import sonia.scm.script.ScriptType;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

/**
 * Created by sdorra on 18.07.17.
 */
@RunWith(MockitoJUnitRunner.class)
public class InitScriptCollectorTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Mock
  private ScriptManager scriptManager;

  private File scriptFolder;

  private InitScriptCollector collector;

  @Before
  public void setUpObjectUnderTest() throws IOException {
    scriptFolder = temporaryFolder.newFolder();
    when(scriptManager.getSupportedTypes()).thenReturn(createSupportedScriptTypes());
    collector = new InitScriptCollector(scriptManager, scriptFolder);
  }

  private Set<ScriptType> createSupportedScriptTypes() {
    return Sets.newHashSet(createGroovyScriptType());
  }

  private ScriptType createGroovyScriptType() {
    return new ScriptType(
      "groovy",
      "Groovy",
      Lists.newArrayList("application/x-groovy"),
      Lists.newArrayList("groovy")
    );
  }

  @Test
  public void collectWithNonExistingDirectory() {
    assertTrue(scriptFolder.delete());
    List<InitScript> scripts = collector.collect();
    assertEquals(0, scripts.size());
  }

  @Test
  public void collect() throws IOException {
    writeGroovyScript("020-hello-world.groovy");
    writeGroovyScript("040-hello-world.groovy");
    writeGroovyScript("010-hello-world.groovy");
    writeGroovyScript("030-hello-world.groovy");

    List<InitScript> scripts = collector.collect();
    assertEquals(4, scripts.size());
    assertEquals("010-hello-world.groovy", scripts.get(0).getFile().getName());
    assertEquals("020-hello-world.groovy", scripts.get(1).getFile().getName());
    assertEquals("030-hello-world.groovy", scripts.get(2).getFile().getName());
    assertEquals("040-hello-world.groovy", scripts.get(3).getFile().getName());
  }

  private void writeGroovyScript(String filename) throws IOException {
    Files.write("println 'hello world';", new File(scriptFolder, filename), Charsets.UTF_8);
  }

}
