// @flow
import { binder } from "@scm-manager/ui-extensions";
import { ProtectedRoute, PrimaryNavigationLink } from '@scm-manager/ui-components';
import { translate } from "react-i18next";

import Overview from './Overview';

const ScriptRoute = ({authenticated}) => {
  return (
    <ProtectedRoute
      path="/scripts"
      component={Overview}
      authenticated={authenticated}
    />
  );
};

binder.bind("main.route", ScriptRoute);

const ScriptNavigation = ({links, t}) => {
  return (
    <PrimaryNavigationLink
      to="/scripts"
      match="/(script|scripts)"
      label={t("scm-script-plugin.primary-navigation")}
      key="scripts"
    />
  );
};

const ScriptNavigationWithI18n = translate("plugins")(ScriptNavigation);
binder.bind("primary-navigation", ScriptNavigationWithI18n);
