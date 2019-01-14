// @flow
import React from "react";
import { binder } from "@scm-manager/ui-extensions";
import { ProtectedRoute } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import ScriptNavigation from "./ScriptNavigation";
import RootPage from "./RootPage";
import type { Links } from "@scm-manager/ui-types";

type RouteProps = {
  authenticated: boolean,
  links: Links
};

const ScriptRoute = ({ authenticated, links }: RouteProps) => {
  return (
    <>
      <ProtectedRoute
        path="/scripts"
        component={() => <RootPage link={links.scripts.href} />}
        authenticated={authenticated}
      />

      <ProtectedRoute
        path="/script"
        component={() => <RootPage link={links.scripts.href} />}
        authenticated={authenticated}
      />
    </>
  );
};

binder.bind("main.route", ScriptRoute);

type PredicateProps = {
  links: Links
};

// @VisibleForTesting
export const predicate = ({ links }: PredicateProps) => {
  return !!(links && links.scripts);
};

binder.bind("primary-navigation", ScriptNavigation, predicate);
