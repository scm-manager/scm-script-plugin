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
import { Navigate } from "react-router-dom";
import { binder } from "@scm-manager/ui-extensions";
import RootPage from "./RootPage";
import {
  ADMIN_BASE_ROUTE,
  SCRIPT_BASE_ROUTE,
  SCRIPT_SUB_ROUTES,
  SINGLE_SCRIPT_BASE_ROUTE,
  SINGLE_SCRIPT_SUB_ROUTES,
} from "./routes";
import Editor from "./scripts/run/Editor";
import Stored from "./scripts/list/Stored";
import SampleRoot from "./scripts/samples/SampleRoot";
import ScriptRoot from "./script/ScriptRoot";
import EditForm from "./script/EditForm";
import HistoryPage from "./script/HistoryPage";
import { ListenersRoot } from "./script/ListenersPage";
import { Links } from "@scm-manager/ui-types";
import ScriptNavigation from "./ScriptNavigation";

const scriptRoute = () => [
  {
    path: SCRIPT_BASE_ROUTE,
    element: <RootPage />,
    children: [
      {
        index: true,
        element: <Stored />,
      },
      {
        path: SCRIPT_SUB_ROUTES.RUN,
        element: <Editor />,
      },
      {
        path: SCRIPT_SUB_ROUTES.SAMPLES,
        element: <SampleRoot />,
      },
    ],
  },
  {
    path: SINGLE_SCRIPT_BASE_ROUTE,
    element: <Navigate to={`/${ADMIN_BASE_ROUTE}/${SCRIPT_BASE_ROUTE}`} replace />,
  },
  {
    path: `${SINGLE_SCRIPT_BASE_ROUTE}/:id`,
    element: <ScriptRoot />,
    children: [
      {
        index: true,
        element: <EditForm />,
      },
      {
        path: SINGLE_SCRIPT_SUB_ROUTES.LISTENERS,
        element: <ListenersRoot />,
      },
      {
        path: SINGLE_SCRIPT_SUB_ROUTES.HISTORY,
        element: <HistoryPage />,
      },
    ],
  },
];

binder.bind("admin.route", scriptRoute);

type PredicateProps = {
  links: Links;
};

export const predicate = ({ links }: PredicateProps) => {
  return !!(links && links.scripts);
};

binder.bind("admin.navigation", ScriptNavigation, predicate);
