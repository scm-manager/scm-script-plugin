package sonia.scm.script.infrastructure;

import com.github.sdorra.ssp.Constants;
import org.apache.shiro.SecurityUtils;

/**
 * The {@link ScriptPermissions} should be replaced with a generated one from shiro-static-permission,
 * but at the moment it does not work in conjunction with lombok.
 */
class ScriptPermissions {

  private static final String TYPE = "script";

  private static final String ACTION_READ = "read";
  private static final String ACTION_MODIFY = "modify";
  private static final String ACTION_EXECUTE = "execute";

  private ScriptPermissions() {
  }

  static boolean isPermittedToRead() {
    return isPermitted(ACTION_READ);
  }

  static boolean isPermittedToModify() {
    return isPermitted(ACTION_MODIFY);
  }

  static boolean isPermittedToExecute() {
    return isPermitted(ACTION_EXECUTE);
  }

  private static boolean isPermitted(String action) {
    String permission = create(action);
    return SecurityUtils.getSubject().isPermitted(permission);
  }

  static void checkRead() {
    check(ACTION_READ);
  }

  static void checkModify() {
    check(ACTION_MODIFY);
  }

  static void checkExecute() {
    check(ACTION_EXECUTE);
  }

  private static void check(String action) {
    String permission = create(action);
    SecurityUtils.getSubject().checkPermission(permission);
  }

  private static String create(String action) {
    return TYPE.concat(Constants.SEPARATOR).concat(action);
  }
}
