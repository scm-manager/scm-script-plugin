//@flow
import React from "react";
import { Link } from "react-router-dom";
import { translate } from "react-i18next";
import type { Script } from "../../types";

type Props = {
  scripts: Script[],

  // context props
  t: string => string
};

class ScriptTable extends React.Component<Props> {
  render() {
    const { scripts, t } = this.props;
    return (
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
                  <Link to={`/admin/script/${script.id}`}>
                    {script.description}
                  </Link>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    );
  }
}

export default translate("plugins")(ScriptTable);
