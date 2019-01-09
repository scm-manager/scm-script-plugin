// @flow

import {PrimaryNavigationLink} from '@scm-manager/ui-components';
import {translate} from "react-i18next";
import type {Link} from '@scm-manager/ui-types';

// @VisibleForTesting
export const hasLinkItem = (links: Link[], name: string) => {
  for (let link of links) {
    if (link.name === name) {
      return true;
    }
  }
  return false;
};

// @VisibleForTesting
export const findNavigationLink = (links: Link[]) => {
  if (hasLinkItem(links, "run")) {
    return "/scripts/run";
  }
  return "/scripts/stored";
};

const ScriptNavigation = ({ links, t }) => {
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
