//@flow
import React from "react";
import { translate } from "react-i18next";
import { DeleteButton, confirmAlert } from "@scm-manager/ui-components";
import type { Script } from "../types";

type Props = {
  script: Script,
  onDelete: Script => void,

  // context props
  t: string => string
};

class DeleteScript extends React.Component<Props> {
  confirmDelete = () => {
    const { onDelete, t } = this.props;
    confirmAlert({
      title: t("scm-script-plugin.delete.confirmAlert.title"),
      message: t("scm-script-plugin.delete.confirmAlert.message"),
      buttons: [
        {
          label: t("scm-script-plugin.delete.confirmAlert.submit"),
          onClick: onDelete
        },
        {
          label: t("scm-script-plugin.delete.confirmAlert.cancel"),
          onClick: () => null
        }
      ]
    });
  };

  render() {
    const { script, t } = this.props;
    let deleteLink = null;
    if (script._links.delete) {
      deleteLink = (
        <div className="columns">
          <div className="column">
            <DeleteButton
              label={t("scm-script-plugin.delete.button")}
              action={this.confirmDelete}
            />
          </div>
        </div>
      );
    }
    return deleteLink;
  }
}

export default translate("plugins")(DeleteScript);
