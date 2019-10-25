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
