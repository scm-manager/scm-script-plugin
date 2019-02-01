//@flow
import React from "react";
import type { Script, ScriptExecutionResult, ScriptLinks } from "../types";
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
import DeleteScript from "./DeleteScript";

type Props = {
  script: Script,
  onDelete: () => void,
  links: ScriptLinks,

  // context props
  t: string => string
};

type State = Script & {
  loading: boolean,
  error?: Error,
  result?: ScriptExecutionResult,
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

    if (!links.execute) {
      return;
    }

    run(links.execute, script.type, content ? content : "")
      .then(result =>
        this.setState({
          loading: false,
          error: undefined,
          result
        })
      )
      .catch(error =>
        this.setState({
          loading: false,
          error,
          result: undefined
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
          error: undefined,
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
      error: undefined,
      saveSuccess: false
    });
  };

  onValueChange = (value: string, name: string) => {
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

  isTitleValid = () => {
    return !!this.state.title;
  };

  isScriptValid = () => {
    return this.isTitleValid();
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
          disabled={!this.isScriptValid()}
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
    const { title, description, content, result } = this.state;
    const { t, script, onDelete } = this.props;

    console.log(this.props);
    return (
      <>
        {this.renderNotifications()}
        <form onSubmit={this.onRun}>
          <InputField
            name="title"
            label={t("scm-script-plugin.title")}
            helpText={t("scm-script-plugin.titleHelp")}
            value={title}
            validationError={!this.isTitleValid()}
            errorMessage={t("scm-script-plugin.titleValidationError")}
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
          <hr />
          <DeleteScript script={script} onDelete={onDelete} />
        </form>
        <Output result={result} />
      </>
    );
  }
}

export default translate("plugins")(EditForm);
