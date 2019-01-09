//@flow
import React from "react";
import { translate } from "react-i18next";
import StoreForm from "./StoreForm";
import type { Script } from "./types";

type Props = {
  onSubmit: Script => void,
  onClose: () => void,

  // context props
  t: string => string
};

class StoreDialog extends React.Component<Props> {
  render() {
    const { t, onSubmit, onClose } = this.props;
    return (
      <div className="modal is-active">
        <div className="modal-background" />
        <div className="modal-card">
          <header className="modal-card-head">
            <p className="modal-card-title">
              {t("scm-script-plugin.store-dialog.title")}
            </p>
            <button
              className="delete"
              aria-label="close"
              onClick={() => onClose()}
            />
          </header>
          <section className="modal-card-body">
            <div className="content">
              <StoreForm onSubmit={onSubmit} onAbort={onClose} />
            </div>
          </section>
        </div>
      </div>
    );
  }
}

export default translate("plugins")(StoreDialog);
