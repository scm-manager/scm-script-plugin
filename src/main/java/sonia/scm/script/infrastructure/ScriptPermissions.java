/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
