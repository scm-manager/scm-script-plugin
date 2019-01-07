//@flow
import React from "react";
import { SubmitButton } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import Output from "./Output";
import ErrorNotification from "@scm-manager/ui-components/src/ErrorNotification";
import ContentEditor from "./ContentEditor";
import { run } from "./api";

type Props = {
  // context props
  t: string => string
};

type State = {
  script: string,
  loading: boolean,
  output: string,
  error?: Error
};

class Editor extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      script: "println 'Hello World'",
      loading: false,
      output: ""
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

  render() {
    const { t } = this.props;
    const { script, output, error, loading } = this.state;
    const body = error ? (
      <ErrorNotification error={error} />
    ) : (
      <Output output={output} />
    );

    return (
      <div>
        <ContentEditor onChange={this.onScriptChange} value={script} />
        <SubmitButton
          label={t("scm-script-plugin.editor.submit")}
          action={this.execute}
          loading={loading}
        />
        {body}
      </div>
    );
  }
}

export default translate("plugins")(Editor);
