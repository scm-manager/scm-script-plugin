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
