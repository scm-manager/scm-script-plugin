package sonia.scm.script.infrastructure;

import sonia.scm.web.VndMediaType;

final class ScriptMediaType {

  static final String ONE = VndMediaType.PREFIX + "script" + VndMediaType.SUFFIX;
  static final String COLLECTION = VndMediaType.PREFIX + "script-collection" + VndMediaType.SUFFIX;

  private ScriptMediaType() {}


}
