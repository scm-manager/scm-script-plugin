// @flow
import React from "react";
import { withRouter } from "react-router-dom";
import { translate } from "react-i18next";
import { NavLink, SubNavigation } from "@scm-manager/ui-components";

type Props = {
  //context objects
  match: any,
  t: string => string
};

class ScriptNavigation extends React.Component<Props> {
  matchesScript = (route: any) => {
    const regex = new RegExp("/admin/script/.+");
    return route.location.pathname.match(regex);
  };

  render() {
    const { match, t } = this.props;

    return (
      <>
        <SubNavigation
          to={match.url + "/scripts/run"}
          icon="fas fa-puzzle-piece"
          label={t("scm-script-plugin.navLink")}
          activeWhenMatch={this.matchesScript}
          activeOnlyWhenExact={false}
        >
          <NavLink
            to={match.url + "/scripts/run"}
            label={t("scm-script-plugin.navigation.run")}
          />
          <NavLink
            to={match.url + "/scripts"}
            label={t("scm-script-plugin.navigation.stored")}
            activeWhenMatch={this.matchesScript}
            activeOnlyWhenExact={true}
          />
          <NavLink
            to={match.url + "/scripts/samples"}
            label={t("scm-script-plugin.navigation.samples")}
          />
        </SubNavigation>
      </>
    );
  }
}

export default translate("plugins")(withRouter(ScriptNavigation));
