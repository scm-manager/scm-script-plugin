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

import React, { FC } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { Script } from "../../types";

type Props = {
  scripts: Script[];
};

const ScriptTable: FC<Props> = ({ scripts }) => {
  const [t] = useTranslation("plugins");

  return (
    <>
      <div className="content">
        <h3>{t("scm-script-plugin.navigation.stored")}</h3>
      </div>
      <table className="card-table table is-hoverable is-fullwidth">
        <thead>
          <tr>
            <th>{t("scm-script-plugin.title")}</th>
            <th>{t("scm-script-plugin.description")}</th>
          </tr>
        </thead>
        <tbody>
          {scripts.map(script => {
            if (!script.id) {
              return null;
            }
            return (
              <tr key={script.id}>
                <td>
                  <Link to={`/admin/script/${script.id}`}>{script.title}</Link>
                </td>
                <td>
                  <Link to={`/admin/script/${script.id}`}>{script.description}</Link>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </>
  );
};

export default ScriptTable;
