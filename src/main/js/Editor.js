//@flow
import React from "react";
import { SubmitButton } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import Output from "./Output";
import ErrorNotification from "@scm-manager/ui-components/src/ErrorNotification";
import ContentEditor from "./ContentEditor";
import { run, store } from "./api";
import Button from "@scm-manager/ui-components/src/buttons/Button";
import StoreDialog from "./StoreDialog";
import type { Script } from "./types";

type Props = {
  // context props
  t: string => string
};

type State = {
  script: string,
  loading: boolean,
  output: string,
  error?: Error,
  showStoreDialog: boolean
};

class Editor extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      script: "println 'Hello World'",
      loading: false,
      output: "",
      showStoreDialog: false
    };
  }

  onScriptChange = (script: string) => {
    this.setState({
      script
    });
  };

  execute = () => {
    this.setState({
      loading: true,
      output: "",
      error: null
    });
    run("Groovy", this.state.script)
      .then(output =>
        this.setState({
          output,
          loading: false
        })
      )
      .catch(error => {
        this.setState({
          loading: false,
          error
        });
      });
  };

  showStoreDialog = () => {
    this.setState({
      showStoreDialog: true
    });
  };

  closeStoreDialog = () => {
    this.setState({
      showStoreDialog: false
    });
  };

  store = (script: Script) => {
    script.content = this.state.script;

    store(script).then(this.closeStoreDialog);
  };

  render() {
    const { t } = this.props;
    const { showStoreDialog, script, output, error, loading } = this.state;

    const storeDialog = showStoreDialog ? (
      <StoreDialog onSubmit={this.store} onClose={this.closeStoreDialog} />
    ) : null;

    const body = error ? (
      <ErrorNotification error={error} />
    ) : (
      <Output output={output} />
    );

    return (
      <div>
        <ContentEditor onChange={this.onScriptChange} value={script} />
        <div>
          <SubmitButton
            label={t("scm-script-plugin.editor.submit")}
            action={this.execute}
            loading={loading}
          />
          <Button
            label={t("scm-script-plugin.editor.store")}
            action={this.showStoreDialog}
          />
        </div>
        {body}
        {storeDialog}
      </div>
    );
  }
}

export default translate("plugins")(Editor);
