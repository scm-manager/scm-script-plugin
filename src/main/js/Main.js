//@flow
import React from "react";
import { Page } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import MainNavigation from "./MainNavigation";
import MainRouting from "./MainRouting";

type Props = {
  // context props
  t: string => string
};

class Overview extends React.Component<Props> {
  render() {
    const { t } = this.props;
    return (
      <Page
        title={t("scm-script-plugin.main.title")}
        subtitle={t("scm-script-plugin.main.subtitle")}
      >
        <div className="columns">
          <div className="column is-three-quarters">
            <MainRouting />
          </div>
          <div className="column">
            <MainNavigation />
          </div>
        </div>
      </Page>
    );
  }
}

export default translate("plugins")(Overview);
