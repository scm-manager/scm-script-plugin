/**
 * Copyright (c) 2010, Sebastian Sdorra All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of SCM-Manager;
 * nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.script;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.plugin.ext.Extension;
import sonia.scm.web.security.AdministrationContext;
import sonia.scm.web.security.PrivilegedAction;

//~--- JDK imports ------------------------------------------------------------

import java.io.InputStream;

import java.net.URL;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 *
 * @author Sebastian Sdorra
 */
@Extension
public class SampleInstaller implements ServletContextListener
{

  /** Field description */
  //J-
  private static ImmutableSet<Sample> SAMPLES_1 = ImmutableSet.of(
    new Sample(
      "/samples/metadata/clear-caches.xml",
      "/samples/content/clear-caches.groovy"
    ), 
    new Sample(
      "/samples/metadata/latest-changesets.xml",
      "/samples/content/latest-changesets.js"
    )
  );
  //J+

  /** Field description */
  private static final int SAMPLEVERSION = 1;

  /**
   * the logger for SampleInstaller
   */
  private static final Logger logger =
    LoggerFactory.getLogger(SampleInstaller.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param adminContext
   * @param scriptManager
   */
  @Inject
  public SampleInstaller(AdministrationContext adminContext,
    ScriptManager scriptManager)
  {
    this.adminContext = adminContext;
    this.scriptManager = scriptManager;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param sce
   */
  @Override
  public void contextDestroyed(ServletContextEvent sce)
  {

    // do nothing
  }

  /**
   * Method description
   *
   *
   * @param sce
   */
  @Override
  public void contextInitialized(ServletContextEvent sce)
  {
    adminContext.runAsAdmin(new SampleInstallerAction());
  }

  //~--- inner classes --------------------------------------------------------

  /**
   * Class description
   *
   *
   * @version        Enter version here..., 13/03/07
   * @author         Enter your name here...
   */
  private static class Sample
  {

    /**
     * Constructs ...
     *
     *
     * @param metadata
     * @param content
     */
    public Sample(String metadata, String content)
    {
      this.metadata = metadata;
      this.content = content;
    }

    //~--- fields -------------------------------------------------------------

    /** Field description */
    private String content;

    /** Field description */
    private String metadata;
  }


  /**
   * Class description
   *
   *
   * @version        Enter version here..., 13/03/07
   * @author         Enter your name here...
   */
  private class SampleInstallerAction implements PrivilegedAction
  {

    /**
     * Method description
     *
     */
    @Override
    public void run()
    {
      int version = scriptManager.getSampleVersion();

      if (version < SAMPLEVERSION)
      {
        try
        {
          JAXBContext context = JAXBContext.newInstance(ScriptMetadata.class);

          for (Sample sample : SAMPLES_1)
          {
            InputStream content = null;

            try
            {
              ScriptMetadata metadata = getMetadata(context, sample);

              content = getContent(sample);
              scriptManager.add(metadata, content);
            }
            catch (Exception ex)
            {
              logger.error("could not install sample ".concat(sample.metadata),
                ex);
            }
            finally
            {
              Closeables.closeQuietly(content);
            }
          }

        }
        catch (JAXBException ex)
        {
          logger.error("could not install samples", ex);
        }
        finally
        {
          scriptManager.setSampleVersion(SAMPLEVERSION);
        }
      }
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param sample
     *
     * @return
     */
    private InputStream getContent(Sample sample)
    {
      return SampleInstaller.class.getResourceAsStream(sample.content);
    }

    /**
     * Method description
     *
     *
     * @param context
     * @param sample
     *
     * @return
     *
     * @throws JAXBException
     */
    private ScriptMetadata getMetadata(JAXBContext context, Sample sample)
      throws JAXBException
    {
      URL url = SampleInstaller.class.getResource(sample.metadata);

      return (ScriptMetadata) context.createUnmarshaller().unmarshal(url);
    }
  }


  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private AdministrationContext adminContext;

  /** Field description */
  private ScriptManager scriptManager;
}
