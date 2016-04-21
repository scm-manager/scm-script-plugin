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
package sonia.scm.script;

//~--- non-JDK imports --------------------------------------------------------
import com.google.common.io.Files;
import java.io.File;
import java.io.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//~--- JDK imports ------------------------------------------------------------
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.script.ScriptException;
import sonia.scm.util.IOUtil;

/**
 *
 * @author Sebastian Sdorra
 */
public class ScriptUtil
{

  /**
   * the logger for Scripts
   */
  private static final Logger logger =
    LoggerFactory.getLogger(ScriptUtil.class);

  //~--- methods --------------------------------------------------------------
  /**
   * Method description
   *
   *
   * @param executor
   * @param script
   *
   * @return
   *
   * @throws IOException
   * @throws ScriptException
   */
  public static String execute(ScriptManager executor, ScriptContent script)
    throws IOException, ScriptException
  {
    logger.debug("execute script of type {}", script.getMimetype());

    return execute(executor, script.getMimetype(), script.getContent());
  }

  /**
   * Method description
   *
   *
   * @param executor
   * @param mimeType
   * @param script
   * @param content
   *
   * @return
   *
   * @throws IOException
   * @throws ScriptException
   */
  public static String execute(ScriptManager executor, String mimeType,
    InputStream content)
    throws IOException, ScriptException
  {
    InputStreamReader reader = new InputStreamReader(content);
    StringWriter writer = new StringWriter();
    PrintWriter printWriter = new PrintWriter(writer);

    executor.execute(mimeType, reader, printWriter);

    printWriter.close();

    return writer.toString();
  }

  public static String execute(ScriptManager executor, String mimeType, File script)
          throws IOException, ScriptException
  {
    StringWriter writer = new StringWriter();
    PrintWriter printWriter = new PrintWriter(writer);
    FileReader reader = null;
    try
    {
      reader = new FileReader(script);
      executor.execute(mimeType, reader, printWriter);
    } 
    finally
    {
      IOUtil.close(reader);
    }

    printWriter.close();

    return writer.toString();
  }

  public static ScriptType getTypeFromFile(ScriptManager executor, File file)
  {
    ScriptType type = null;
    String extension = Files.getFileExtension(file.getName());
    for (ScriptType t : executor.getSupportedTypes())
    {
      if (t.getExtensions().contains(extension))
      {
        type = t;
        break;
      }
    }
    return type;
  }
}