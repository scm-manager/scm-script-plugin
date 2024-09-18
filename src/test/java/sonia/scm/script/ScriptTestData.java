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

package sonia.scm.script;

import sonia.scm.script.domain.StorableScript;

import java.util.ArrayList;

public final class ScriptTestData {

  private ScriptTestData() {

  }

  public static StorableScript createHelloWorld() {
    StorableScript script = new StorableScript(
      "Groovy",
      "println 'Hello World'"
    );
    script.setId("42");
    script.setTitle("Hello World");
    script.setDescription("Awesome Hello World");
    return script;
  }
}
