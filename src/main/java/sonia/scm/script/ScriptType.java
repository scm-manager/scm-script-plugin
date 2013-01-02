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

import com.google.common.base.Objects;

//~--- JDK imports ------------------------------------------------------------

import java.io.Serializable;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Sebastian Sdorra
 */
@XmlRootElement(name = "script-type")
@XmlAccessorType(XmlAccessType.FIELD)
public final class ScriptType implements Serializable
{

  /** Field description */
  private static final long serialVersionUID = 2014232471565306812L;

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   */
  public ScriptType() {}

  /**
   * Constructs ...
   *
   *
   * @param name
   * @param displayName
   * @param mimeType
   * @param mimeTypes
   */
  public ScriptType(String name, String displayName, List<String> mimeTypes)
  {
    this.name = name;
    this.displayName = displayName;
    this.mimeTypes = mimeTypes;
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

    final ScriptType other = (ScriptType) obj;

    return Objects.equal(name, other.displayName)
      && Objects.equal(displayName, other.displayName)
      && Objects.equal(mimeTypes, other.mimeTypes);
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
    return Objects.hashCode(name, displayName, mimeTypes);
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
                  .add("name", name)
                  .add("displayName", displayName)
                  .add("mimeTypes", mimeTypes)
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
  public String getDisplayName()
  {
    return displayName;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public List<String> getMimeTypes()
  {
    return mimeTypes;
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

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  @XmlElement(name = "display-name")
  private String displayName;

  /** Field description */
  @XmlElement(name = "mime-types")
  @XmlElementWrapper(name = "mime-type")
  private List<String> mimeTypes;

  /** Field description */
  private String name;
}
