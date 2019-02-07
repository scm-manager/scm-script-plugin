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
      title: t("scm-script-plugin.delete-nav-action.confirm-alert.title"),
      message: t("scm-script-plugin.delete-nav-action.confirm-alert.message"),
      buttons: [
        {
          label: t("scm-script-plugin.delete-nav-action.confirm-alert.submit"),
          onClick: onDelete
        },
        {
          label: t("scm-script-plugin.delete-nav-action.confirm-alert.cancel"),
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
              label={t("scm-script-plugin.deleteScript.button")}
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
