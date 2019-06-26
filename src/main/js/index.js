// @flow
import React from "react";
import { Route } from "react-router-dom";
import { binder } from "@scm-manager/ui-extensions";
import type { Links } from "@scm-manager/ui-types";
import ScriptNavigation from "./ScriptNavigation";
import RootPage from "./RootPage";

type PredicateProps = {
  links: Links
};

// @VisibleForTesting
export const predicate = ({ links }: PredicateProps) => {
  return !!(links && links.scripts);
};

const ScriptRoute = ({ links }) => {
  return (
    <>
      <Route
        path="/admin/scripts"
        component={() => (
          <RootPage link={links.scripts.href} />
        )}
      />
      <Route
        path="/admin/script"
        component={() => <RootPage link={links.scripts.href} />}
      />
    </>
  );
};

binder.bind("admin.route", ScriptRoute, predicate);

binder.bind("admin.navigation", ScriptNavigation, predicate);
