//@flow
import React from "react";
import { translate } from "react-i18next";
import StoreForm from "./StoreForm";
import type { Script } from "../../types";
import { Modal } from "@scm-manager/ui-components";

type Props = {
  onSubmit: Script => void,
  onClose: () => void,

  // context props
  t: string => string
};

class StoreDialog extends React.Component<Props> {
  render() {
    const { t, onSubmit, onClose } = this.props;

    const body = (
      <div className="content">
        <StoreForm onSubmit={onSubmit} onAbort={onClose} />
      </div>
    );

    return (
      <Modal
        title={t("scm-script-plugin.store-dialog.title")}
        closeFunction={() => onClose()}
        body={body}
        active={true}
      />
    );
  }
}

export default translate("plugins")(StoreDialog);
