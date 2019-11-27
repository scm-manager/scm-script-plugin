import React from "react";
import { WithTranslation, withTranslation } from "react-i18next";
import { withRouter, RouteComponentProps } from "react-router-dom";
import StoreDialog from "./StoreDialog";
import { apiClient, ErrorNotification, ButtonGroup, Button, SubmitButton, Level } from "@scm-manager/ui-components";
import Output from "../../components/Output";
import ContentEditor from "../../components/ContentEditor";
import { run, store } from "../../api";
import { Script, ScriptExecutionResult, ScriptLinks } from "../../types";

type Props = WithTranslation &
  RouteComponentProps & {
    links: ScriptLinks;
    value?: string;
  };

type State = {
  script: string;
  loading: boolean;
  result?: ScriptExecutionResult;
  error?: Error;
  showStoreDialog: boolean;
};

class Editor extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      script: props.value ? props.value : "println 'Hello World'",
      loading: false,
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
      result: undefined,
      error: undefined
    });

    const { links } = this.props;
    if (!links.execute) {
      this.setState({
        error: new Error("no permissions to execute scripts")
      });
      return;
    }

    run(links.execute, "Groovy", this.state.script)
      .then(result =>
        this.setState({
          result,
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
      return;
    }
    script.content = this.state.script;

    store(links.create, script)
      .then(resp => resp.headers.get("Location"))
      .then(location => apiClient.get(location))
      .then(resp => resp.json())
      .then(script => script.id)
      .then(id => history.push("/admin/script/" + id))
      .catch(error =>
        this.setState({
          error
        })
      );
  };

  createExecuteButton = () => {
    const { links, t } = this.props;
    if (!links.execute) {
      return;
    }

    const { loading } = this.state;

    return <SubmitButton label={t("scm-script-plugin.editor.submit")} action={this.execute} loading={loading} />;
  };

  createStoreButton = () => {
    const { links, t } = this.props;
    if (!links.create) {
      return;
    }
    return <Button label={t("scm-script-plugin.editor.store")} action={this.showStoreDialog} />;
  };

  render() {
    const { t } = this.props;
    const { showStoreDialog, script, result, error } = this.state;

    const storeDialog = showStoreDialog ? <StoreDialog onSubmit={this.store} onClose={this.closeStoreDialog} /> : null;

    const body = error ? <ErrorNotification error={error} /> : <Output result={result} />;

    return (
      <>
        <div className="content">
          <h3>{t("scm-script-plugin.navigation.run")}</h3>
        </div>
        <ContentEditor onChange={this.onScriptChange} value={script} />
        <Level
          right={
            <ButtonGroup>
              {this.createExecuteButton()}
              {this.createStoreButton()}
            </ButtonGroup>
          }
        />
        {body}
        {storeDialog}
      </>
    );
  }
}

export default withTranslation("plugins")(withRouter(Editor));
