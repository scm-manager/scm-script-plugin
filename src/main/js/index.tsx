/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

import React from "react";
import { Route } from "react-router-dom";
import { binder } from "@scm-manager/ui-extensions";
import { Link, Links } from "@scm-manager/ui-types";
import ScriptNavigation from "./ScriptNavigation";
import RootPage from "./RootPage";
import { predicate } from "./predicate";

const ScriptRoute = ({ links }: { links: Links }) => {
  return (
    <>
      <Route path="/admin/scripts">
        <RootPage link={(links.scripts as Link).href} />
      </Route>
      <Route path="/admin/script">
        <RootPage link={(links.scripts as Link).href} />
      </Route>
    </>
  );
};

binder.bind("admin.route", ScriptRoute, predicate);

binder.bind("admin.navigation", ScriptNavigation, predicate);
