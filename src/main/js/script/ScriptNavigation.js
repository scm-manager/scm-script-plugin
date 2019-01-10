//@flow
import React from "react";
import { withRouter } from "react-router-dom";
import { translate } from "react-i18next";
import { Navigation, NavLink, Section } from "@scm-manager/ui-components";

type Props = {
  match: any,
  t: string => string
};

class ScriptNavigation extends React.Component<Props> {
  render() {
    const { match, t } = this.props;
    return (
      <Navigation>
        <Section label="Navigation">
          <NavLink
            to={match.url}
            label={t("scm-script-plugin.script-navigation.edit")}
          />
        </Section>
        <Section label="Actions">
          <NavLink
            to="/scripts/samples"
            label={t("scm-script-plugin.script-navigation.delete")}
          />
          <NavLink
            to="/scripts"
            label={t("scm-script-plugin.script-navigation.back")}
          />
        </Section>
      </Navigation>
    );
  }
}

export default translate("plugins")(withRouter(ScriptNavigation));
