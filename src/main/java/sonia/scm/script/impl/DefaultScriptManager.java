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

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.script.EnvironmentBuilder;
import sonia.scm.script.ScriptContent;
import sonia.scm.script.ScriptManager;
import sonia.scm.script.ScriptMetadata;
import sonia.scm.script.ScriptType;
import sonia.scm.script.ScriptWrapperException;
import sonia.scm.security.KeyGenerator;
import sonia.scm.security.Role;
import sonia.scm.store.Blob;
import sonia.scm.store.BlobStore;
import sonia.scm.store.BlobStoreFactory;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;

//~--- JDK imports ------------------------------------------------------------

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

/**
 *
 * @author Sebastian Sdorra
 */
@Singleton
public class DefaultScriptManager implements ScriptManager
{

  /** Field description */
  private static final String SAMPLEVERSION_STORE = "__sampleversion";

  /** Field description */
  private static final String STORE_NAME = "script";

  /**
   * the logger for DefaultScriptManager
   */
  private static final Logger logger =
    LoggerFactory.getLogger(DefaultScriptManager.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param environmentBuilder
   * @param keyGenerator
   * @param dataStoreFactory
   * @param blobStoreFactory
   */
  @Inject
  public DefaultScriptManager(EnvironmentBuilder environmentBuilder,
    KeyGenerator keyGenerator, DataStoreFactory dataStoreFactory,
    BlobStoreFactory blobStoreFactory)
  {
    this.environmentBuilder = environmentBuilder;
    this.keyGenerator = keyGenerator;
    metadataStore = dataStoreFactory.getStore(ScriptMetadata.class, STORE_NAME);
    contentStore = blobStoreFactory.getBlobStore(STORE_NAME);
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param metadata
   * @param content
   *
   * @return
   *
   * @throws IOException
   */
  @Override
  public String add(ScriptMetadata metadata, InputStream content)
    throws IOException
  {
    assertIsAdmin();
    Preconditions.checkNotNull(metadata, "parameter metadata is required");
    Preconditions.checkNotNull(content, "parameter content is required");

    String key = keyGenerator.createKey();

    metadata.setId(key);
    metadataStore.put(key, metadata);

    Blob blob = contentStore.create(key);
    OutputStream out = null;

    try
    {
      out = blob.getOutputStream();
      ByteStreams.copy(content, out);
      blob.commit();
    }
    finally
    {
      Closeables.closeQuietly(content);
      Closeables.closeQuietly(out);
    }

    return key;
  }

  /**
   * Method description
   *
   *
   * @param mimeType
   * @param reader
   * @param writer
   *
   * @throws IOException
   * @throws ScriptWrapperException
   */
  @Override
  public void execute(String mimeType, Reader reader, Writer writer)
    throws IOException, ScriptWrapperException
  {
    assertIsAdmin();
    executeScript(mimeType, reader, writer);
  }

  /**
   * Method description
   *
   *
   * @param id
   * @param writer
   *
   * @throws IOException
   * @throws ScriptException
   */
  @Override
  public void execute(String id, Writer writer)
    throws IOException, ScriptException
  {
    ScriptMetadata metadata = get(id);
    Blob blob = contentStore.get(id);
    InputStreamReader reader = null;

    try
    {
      reader = new InputStreamReader(blob.getInputStream());
      executeScript(metadata.getType(), reader, writer);
    }
    finally
    {
      Closeables.closeQuietly(reader);
    }
  }

  /**
   * Method description
   *
   *
   * @param id
   */
  @Override
  public void remove(String id)
  {
    assertIsAdmin();
    metadataStore.remove(id);
    contentStore.remove(id);
  }

  /**
   * Method description
   *
   *
   * @param script
   * @param content
   *
   * @throws IOException
   */
  @Override
  public void update(ScriptMetadata script, InputStream content)
    throws IOException
  {
    assertIsAdmin();

    String key = script.getId();

    metadataStore.put(key, script);

    Blob blob = contentStore.get(key);
    OutputStream out = null;

    try
    {
      out = blob.getOutputStream();
      ByteStreams.copy(content, out);
    }
    finally
    {
      Closeables.closeQuietly(content);
      Closeables.closeQuietly(out);
    }
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param id
   *
   * @return
   */
  @Override
  public ScriptMetadata get(String id)
  {
    assertIsAdmin();

    return metadataStore.get(id);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public Collection<ScriptMetadata> getAll()
  {
    assertIsAdmin();

    return metadataStore.getAll().values();
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public int getSampleVersion()
  {
    assertIsAdmin();

    int version = -1;

    Blob blob = contentStore.get(SAMPLEVERSION_STORE);

    if (blob != null)
    {
      DataInputStream versionContent = null;

      try
      {
        versionContent = new DataInputStream(blob.getInputStream());
        version = versionContent.readInt();
      }
      catch (Exception ex)
      {
        logger.error("could not read sample version", ex);
      }
      finally
      {
        Closeables.closeQuietly(versionContent);
      }
    }

    return version;
  }

  /**
   * Method description
   *
   *
   * @param id
   *
   * @return
   *
   * @throws IOException
   */
  @Override
  public ScriptContent getScriptContent(String id) throws IOException
  {
    assertIsAdmin();

    ScriptMetadata metadata = metadataStore.get(id);

    return new ScriptContent(metadata.getType(),
      contentStore.get(id).getInputStream());
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public Set<ScriptType> getSupportedTypes()
  {
    if (supportedTypes == null)
    {
      Builder<ScriptType> builder = ImmutableSet.builder();

      ScriptEngineManager manager = new ScriptEngineManager();
      List<ScriptEngineFactory> factories = manager.getEngineFactories();

      for (ScriptEngineFactory factory : factories)
      {
        builder.add(new ScriptType(
          factory.getLanguageName(),
          factory.getEngineName(), 
          factory.getMimeTypes(), 
          factory.getExtensions()
        ));
      }

      supportedTypes = builder.build();
    }

    return supportedTypes;
  }

  //~--- set methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param version
   */
  @Override
  public void setSampleVersion(int version)
  {
    assertIsAdmin();

    Blob blob = contentStore.get(SAMPLEVERSION_STORE);

    if (blob == null)
    {
      blob = contentStore.create(SAMPLEVERSION_STORE);
    }

    DataOutputStream versionContent = null;

    try
    {
      versionContent = new DataOutputStream(blob.getOutputStream());
      versionContent.writeInt(version);
    }
    catch (Exception ex)
    {
      logger.error("could not read sample version", ex);
    }
    finally
    {
      Closeables.closeQuietly(versionContent);
    }
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   */
  private void assertIsAdmin()
  {
    Subject subject = SecurityUtils.getSubject();

    subject.checkRole(Role.ADMIN);
  }

  /**
   * Method description
   *
   *
   * @param mimeType
   * @param reader
   * @param writer
   *
   * @throws IOException
   * @throws ScriptWrapperException
   */
  private void executeScript(String mimeType, Reader reader, Writer writer)
    throws IOException, ScriptWrapperException
  {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByMimeType(mimeType);

    if (engine == null)
    {
      throw new IOException(
        "no script engine available, for mimetype ".concat(mimeType));
    }

    SimpleScriptContext context = new SimpleScriptContext();

    // context.setReader(reader);
    context.setWriter(writer);
    context.setErrorWriter(writer);

    Map<String, Object> env = environmentBuilder.createEnvironment();

    for (Entry<String, Object> e : env.entrySet())
    {
      context.setAttribute(e.getKey(), e.getValue(),
        ScriptContext.ENGINE_SCOPE);
    }

    try
    {
      engine.eval(reader, context);
    }
    catch (ScriptException ex)
    {
      throw new ScriptWrapperException(ex);
    }
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private BlobStore contentStore;

  /** Field description */
  private EnvironmentBuilder environmentBuilder;

  /** Field description */
  private KeyGenerator keyGenerator;

  /** Field description */
  private DataStore<ScriptMetadata> metadataStore;

  /** Field description */
  private Set<ScriptType> supportedTypes;
}
