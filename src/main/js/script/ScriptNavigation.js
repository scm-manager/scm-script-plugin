//@flow
import React from "react";
import { withRouter } from "react-router-dom";
import { translate } from "react-i18next";
import { Navigation, NavLink, Section } from "@scm-manager/ui-components";

type Props = {
  // context props
  match: any,
  t: string => string
};

class ScriptNavigation extends React.Component<Props> {
  render() {
    const { match, t } = this.props;

    return (
      <Navigation>
        <Section label={t("scm-script-plugin.scriptNavigation.navigationLabel")}>
          <NavLink
            to={match.url}
            label={t("scm-script-plugin.scriptNavigation.editNavLink")}
          />
          <NavLink
            to={match.url + "/listeners"}
            label={t("scm-script-plugin.scriptNavigation.listenersNavLink")}
          />
          <NavLink
            to={match.url + "/history"}
            label={t("scm-script-plugin.scriptNavigation.historyNavLink")}
          />
        </Section>
      </Navigation>
    );
  }
}

export default translate("plugins")(withRouter(ScriptNavigation));
