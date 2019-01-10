// @flow

import { PrimaryNavigationLink } from "@scm-manager/ui-components";
import { translate } from "react-i18next";

const ScriptNavigation = ({ t }) => {
  return (
    <PrimaryNavigationLink
      to="/scripts/run"
      match="/(script|scripts)"
      label={t("scm-script-plugin.primary-navigation")}
      key="scripts"
    />
  );
};

export default translate("plugins")(ScriptNavigation);
