// @flow
import React from "react";
import { translate } from "react-i18next";

import { NavLink, SubNavigation } from "@scm-manager/ui-components";

type Props = {
  //context objects
  t: string => string
};

class ScriptNavigation extends React.Component<Props> {
  render() {
    const { t } = this.props;

    return (
      <>
        <SubNavigation
          to="/admin/scripts/run"
          icon="fas fa-puzzle-piece"
          label={t("scm-script-plugin.navLink")}
        >
          <NavLink
            to="/admin/scripts/run"
            label={t("scm-script-plugin.navigation.run")}
          />
          <NavLink
            to="/admin/scripts"
            label={t("scm-script-plugin.navigation.stored")}
          />
          <NavLink
            to="/admin/scripts/samples"
            label={t("scm-script-plugin.navigation.samples")}
          />
        </SubNavigation>
      </>
    );
  }
}

export default translate("plugins")(ScriptNavigation);
