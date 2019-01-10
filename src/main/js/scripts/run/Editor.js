//@flow
import React from "react";
import { apiClient, SubmitButton } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import { withRouter } from "react-router-dom";
import Output from "../../components/Output";
import ErrorNotification from "@scm-manager/ui-components/src/ErrorNotification";
import ContentEditor from "../../components/ContentEditor";
import { run, store } from "../../api";
import Button from "@scm-manager/ui-components/src/buttons/Button";
import StoreDialog from "./StoreDialog";
import type { Script, ScriptLinks } from "../../types";

type Props = {
  links: ScriptLinks,
  value?: string,

  // context props
  t: string => string,
  history: any
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
      script: props.value ? props.value : "println 'Hello World'",
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

    const { links } = this.props;
    if (!links.execute) {
      this.setState({
        error: new Error("no permissions to execute scripts")
      });
    }

    run(links.execute, "Groovy", this.state.script)
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
    const { links, history } = this.props;

    if (!links.create) {
      this.setState({
        error: new Error("no permissions to create scripts")
      });
    }
    script.content = this.state.script;

    store(links.create, script)
      .then(resp => resp.headers.get("Location"))
      .then(location => apiClient.get(location))
      .then(resp => resp.json())
      .then(script => script.id)
      .then(id => history.push("/scripts/stored/" + id))
      .catch(error => this.setState({ error }));
  };

  createExecuteButton = () => {
    const { links, t } = this.props;
    if (!links.execute) {
      return;
    }

    const { loading } = this.state;

    return (
      <SubmitButton
        label={t("scm-script-plugin.editor.submit")}
        action={this.execute}
        loading={loading}
      />
    );
  };

  createStoreButton = () => {
    const { links, t } = this.props;
    if (!links.create) {
      return;
    }
    return (
      <Button
        label={t("scm-script-plugin.editor.store")}
        action={this.showStoreDialog}
      />
    );
  };

  render() {
    const { showStoreDialog, script, output, error } = this.state;

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
          {this.createExecuteButton()}
          {this.createStoreButton()}
        </div>
        {body}
        {storeDialog}
      </div>
    );
  }
}

export default translate("plugins")(withRouter(Editor));
