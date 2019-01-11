//@flow
import React from "react";
import type { Script, ScriptLinks } from "../types";
import {
  InputField,
  LabelWithHelpIcon,
  Button,
  SubmitButton,
  Textarea,
  ErrorNotification,
  Notification
} from "@scm-manager/ui-components";
import ContentEditor from "../components/ContentEditor";
import { translate } from "react-i18next";
import Output from "../components/Output";
import { modify, run } from "../api";

type Props = {
  script: Script,
  links: ScriptLinks,

  // context props
  t: string => string
};

type State = Script & {
  loading: boolean,
  error?: Error,
  output?: string,
  saveSuccess: boolean
};

class EditForm extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      ...props.script,
      loading: false,
      saveSuccess: false
    };
  }

  onRun = (event: Event) => {
    this.onAction(event);

    const { script, links } = this.props;
    const { content } = this.state;
    run(links.execute, script.type, content)
      .then(output =>
        this.setState({
          loading: false,
          error: null,
          output
        })
      )
      .catch(error =>
        this.setState({
          loading: false,
          error,
          output: null
        })
      );
  };

  onSave = (event: Event) => {
    this.onAction(event);

    const script = {
      ...this.props.script,
      title: this.state.title,
      description: this.state.description,
      content: this.state.content
    };

    modify(script._links.update.href, script)
      .then(() =>
        this.setState({
          loading: false,
          error: null,
          saveSuccess: true
        })
      )
      .catch(error =>
        this.setState({
          loading: false,
          error
        })
      );
  };

  onAction = (event: Event) => {
    event.preventDefault();
    this.setState({
      loading: true,
      error: null,
      saveSuccess: false
    });
  };

  onValueChange = (value, name) => {
    this.setState({
      [name]: value
    });
  };

  renderNotifications = () => {
    const { t } = this.props;
    const { saveSuccess, error } = this.state;
    let successNotification = null;
    if (saveSuccess) {
      successNotification = (
        <Notification type="success">
          {t("scm-script-plugin.edit-form.save-success")}
        </Notification>
      );
    }

    return (
      <>
        <ErrorNotification error={error} />
        {successNotification}
      </>
    );
  };

  renderControlButtons = () => {
    const { loading } = this.state;
    const { script, links, t } = this.props;

    let run = null;
    if (links.execute) {
      run = (
        <SubmitButton
          label={t("scm-script-plugin.edit-form.run")}
          action={this.onRun}
          loading={loading}
        />
      );
    }

    let save = null;
    if (script._links.update) {
      const btnColor = run ? "default" : "primary";
      save = (
        <Button
          label={t("scm-script-plugin.edit-form.save")}
          color={btnColor}
          action={this.onSave}
          loading={loading}
        />
      );
    }

    return (
      <div>
        {run}
        {save}
      </div>
    );
  };

  render() {
    const { t } = this.props;
    const { title, description, content, output } = this.state;

    return (
      <>
        {this.renderNotifications()}
        <form onSubmit={this.onRun}>
          <InputField
            name="title"
            label={t("scm-script-plugin.title")}
            helpText={t("scm-script-plugin.titleHelp")}
            value={title}
            onChange={this.onValueChange}
          />
          <Textarea
            name="description"
            label={t("scm-script-plugin.description")}
            helpText={t("scm-script-plugin.descriptionHelp")}
            value={description}
            onChange={this.onValueChange}
          />
          <div className="field">
            <LabelWithHelpIcon
              label={t("scm-script-plugin.content")}
              helpText={t("scm-script-plugin.contentHelp")}
            />
            <ContentEditor
              name="content"
              value={content}
              onChange={this.onValueChange}
            />
          </div>
          {this.renderControlButtons()}
        </form>
        <Output output={output} />
      </>
    );
  }
}

export default translate("plugins")(EditForm);
