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

import com.google.common.base.Objects;

//~--- JDK imports ------------------------------------------------------------

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Sebastian Sdorra
 */
@XmlRootElement(name = "script")
@XmlAccessorType(XmlAccessType.FIELD)
public final class Script implements Serializable
{

  /** Field description */
  private static final long serialVersionUID = -322431605781198934L;

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   */
  public Script() {}

  /**
   * Constructs ...
   *
   *
   *
   * @param id
   * @param name
   * @param description
   * @param type
   * @param content
   */
  public Script(String id, String name, String description, ScriptType type,
    String content)
  {
    this(id, name, description, type.getMimeTypes().get(0), content);
  }

  /**
   * Constructs ...
   *
   *
   *
   * @param id
   * @param name
   * @param description
   * @param type
   * @param content
   */
  public Script(String id, String name, String description, String type,
    String content)
  {
    this.id = id;
    this.name = name;
    this.description = description;
    this.type = type;
    this.content = content;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param obj
   *
   * @return
   */
  @Override
  public boolean equals(Object obj)
  {
    if (obj == null)
    {
      return false;
    }

    if (getClass() != obj.getClass())
    {
      return false;
    }

    final Script other = (Script) obj;

    //J-
    return Objects.equal(id, other.id) 
      && Objects.equal(name, other.name) 
      && Objects.equal(description, other.description)
      && Objects.equal(type, other.type)
      && Objects.equal(content, other.content);
    //J+
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public int hashCode()
  {
    return Objects.hashCode(id, name, description, type, content);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public String toString()
  {
    //J-
    return Objects.toStringHelper(this)
                  .add("id", id)
                  .add("name", name)
                  .add("description", description)
                  .add("type", type)
                  .add("content", content)
                  .toString();
    //J+
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public String getContent()
  {
    return content;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getId()
  {
    return id;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getName()
  {
    return name;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getType()
  {
    return type;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private String content;

  /** Field description */
  private String description;

  /** Field description */
  private String id;

  /** Field description */
  private String name;

  /** Field description */
  private String type;
}
