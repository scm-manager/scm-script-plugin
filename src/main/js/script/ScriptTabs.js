//@flow
import React from "react";
import { Link, withRouter } from "react-router-dom";
import { translate } from "react-i18next";

type Props = {
  // context props
  t: string => string
};

export function isUrlSuffixMatching(
  baseURL: string,
  url: string,
  suffix: string
) {
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
    const { location } = this.props;
    const baseURL = "/admin/script";
    if (location && isUrlSuffixMatching(baseURL, location.pathname, suffix)) {
      return "is-active";
    }
    return "";
  }

  render() {
    const { t } = this.props;

    return (
      <div className="tabs">
        <ul>
          <li className={this.navigationClass("edit")}>
            <Link to="/admin/script">
              {t("scm-script-plugin.scriptNavigation.editNavLink")}
            </Link>
          </li>
          <li className={this.navigationClass("listeners")}>
            <Link to="/admin/script/listeners">
              {t("scm-script-plugin.scriptNavigation.listenersNavLink")}
            </Link>
          </li>
          <li className={this.navigationClass("history")}>
            <Link to="/admin/script/history">
              {t("scm-script-plugin.scriptNavigation.historyNavLink")}
            </Link>
          </li>
        </ul>
      </div>
    );
  }
}

export default translate("plugins")(withRouter(ScriptTabs));
