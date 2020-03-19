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
import React from "react";
import { Link, withRouter, RouteComponentProps } from "react-router-dom";
import { WithTranslation, withTranslation } from "react-i18next";

type Props = WithTranslation & RouteComponentProps & {
  path: string;
};

export function isUrlSuffixMatching(baseURL: string, url: string, suffix: string) {
  let strippedUrl = url.substring(baseURL.length);
  if (strippedUrl.startsWith("/")) {
    strippedUrl = strippedUrl.substring(1);
  }
  const slash = strippedUrl.indexOf("/");
  if (slash >= 0) {
    strippedUrl = strippedUrl.substring(0, slash);
  }
  return strippedUrl === suffix;
}

class ScriptTabs extends React.Component<Props> {
  navigationClass(suffix: string) {
    const { location, path } = this.props;
    if (location && isUrlSuffixMatching(path, location.pathname, suffix)) {
      return "is-active";
    }
    return "";
  }

  render() {
    const { path, t } = this.props;

    return (
      <div className="tabs">
        <ul>
          <li className={this.navigationClass("")}>
            <Link to={path}>{t("scm-script-plugin.scriptTab.edit")}</Link>
          </li>
          <li className={this.navigationClass("listeners")}>
            <Link to={path + "/listeners"}>{t("scm-script-plugin.scriptTab.listeners")}</Link>
          </li>
          <li className={this.navigationClass("history")}>
            <Link to={path + "/history"}>{t("scm-script-plugin.scriptTab.history")}</Link>
          </li>
        </ul>
      </div>
    );
  }
}

export default withTranslation("plugins")(withRouter(ScriptTabs));
