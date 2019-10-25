import React from "react";
import { WithTranslation, withTranslation } from "react-i18next";
import StoreForm from "./StoreForm";
import { Script } from "../../types";
import { Modal } from "@scm-manager/ui-components";

type Props = WithTranslation & {
  onSubmit: (p: Script) => void;
  onClose: () => void;
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
        title={t("scm-script-plugin.storeDialog.title")}
        closeFunction={() => onClose()}
        body={body}
        active={true}
      />
    );
  }
}

export default withTranslation("plugins")(StoreDialog);
