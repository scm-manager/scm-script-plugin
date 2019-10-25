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
