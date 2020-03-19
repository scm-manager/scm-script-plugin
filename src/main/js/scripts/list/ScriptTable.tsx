/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import React from "react";
import { Link } from "react-router-dom";
import { WithTranslation, withTranslation } from "react-i18next";
import { Script } from "../../types";

type Props = WithTranslation & {
  scripts: Script[];
};

class ScriptTable extends React.Component<Props> {
  render() {
    const { scripts, t } = this.props;
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
  }
}

export default withTranslation("plugins")(ScriptTable);
