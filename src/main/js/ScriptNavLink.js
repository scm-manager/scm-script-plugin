// @flow
import React from "react";
import { translate } from "react-i18next";

import { NavLink } from "@scm-manager/ui-components";

type Props = {
  //context objects
  t: string => string
};

class ScriptNavLink extends React.Component<Props> {
  render() {
    const { t } = this.props;

    return (
      <NavLink
        to="/admin/scripts"
        icon="fas fa-puzzle-piece"
        label={t("scm-script-plugin.navLink")}
      />
    );
  }
}

export default translate("plugins")(ScriptNavLink);
