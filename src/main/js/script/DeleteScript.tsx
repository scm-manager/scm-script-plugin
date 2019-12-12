import React from "react";
import { WithTranslation, withTranslation } from "react-i18next";
import { DeleteButton, confirmAlert, Level } from "@scm-manager/ui-components";
import { Script } from "../types";

type Props = WithTranslation & {
  script: Script;
  onDelete: (p: Script) => void;
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
        <Level right={<DeleteButton label={t("scm-script-plugin.delete.button")} action={this.confirmDelete} />} />
      );
    }
    return deleteLink;
  }
}

export default withTranslation("plugins")(DeleteScript);
