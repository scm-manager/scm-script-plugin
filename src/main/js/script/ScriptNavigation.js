//@flow
import React from "react";
import { withRouter } from "react-router-dom";
import { translate } from "react-i18next";
import { Navigation, NavLink, Section } from "@scm-manager/ui-components";
import type { Script } from "../types";
import DeleteNavAction from "./DeleteNavAction";

type Props = {
  script: Script,
  onDelete: () => void,

  // context props
  match: any,
  t: string => string
};

class ScriptNavigation extends React.Component<Props> {
  render() {
    const { script, onDelete, match, t } = this.props;

    return (
      <Navigation>
        <Section label="Navigation">
          <NavLink
            to={match.url}
            label={t("scm-script-plugin.script-navigation.edit")}
          />
          <NavLink
            to={match.url + "/listeners"}
            label={t("scm-script-plugin.script-navigation.listeners")}
          />
          <NavLink
            to={match.url + "/history"}
            label={t("scm-script-plugin.script-navigation.history")}
          />
        </Section>
        <Section label="Actions">
          <DeleteNavAction script={script} onDelete={onDelete} />
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
