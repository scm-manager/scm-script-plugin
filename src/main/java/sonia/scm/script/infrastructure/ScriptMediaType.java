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

import sonia.scm.web.VndMediaType;

final class ScriptMediaType {

  static final String LISTENER_COLLECTION = VndMediaType.PREFIX + "script-listener-collection" + VndMediaType.SUFFIX;
  static final String EVENT_TYPE_COLLECTION = VndMediaType.PREFIX + "script-event-type" + VndMediaType.SUFFIX;

  static final String SCRIPT = VndMediaType.PREFIX + "script" + VndMediaType.SUFFIX;
  static final String SCRIPT_COLLECTION = VndMediaType.PREFIX + "script-collection" + VndMediaType.SUFFIX;
  static final String EXECUTION_RESULT = VndMediaType.PREFIX + "script-execution-result" + VndMediaType.SUFFIX;

  private ScriptMediaType() {}


}
