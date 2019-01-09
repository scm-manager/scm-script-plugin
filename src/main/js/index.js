// @flow
import { binder } from "@scm-manager/ui-extensions";
import {
  ProtectedRoute
} from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import Main from "./Main";
import ScriptNavigation from './ScriptNavigation';

const ScriptRoute = ({ authenticated }) => {
  return (
    <ProtectedRoute
      path="/scripts"
      component={Main}
      authenticated={authenticated}
    />
  );
};

binder.bind("main.route", ScriptRoute);

// @VisibleForTesting
export const predicate = ({links}) => {
  return !!(links && links.scripts && links.scripts.length > 0);
};

binder.bind("primary-navigation", ScriptNavigation, predicate);
