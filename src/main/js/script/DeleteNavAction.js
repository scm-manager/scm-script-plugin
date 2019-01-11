//@flow
import React from "react";
import { translate } from "react-i18next";
import { confirmAlert, NavAction } from "@scm-manager/ui-components";
import type { Script } from "../types";

type Props = {
  script: Script,
  onDelete: Script => void,

  // context props
  t: string => string
};

class DeleteNavAction extends React.Component<Props> {
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
        <NavAction
          action={this.confirmDelete}
          label={t("scm-script-plugin.delete-nav-action.delete")}
        />
      );
    }
    return deleteLink;
  }
}

export default translate("plugins")(DeleteNavAction);
