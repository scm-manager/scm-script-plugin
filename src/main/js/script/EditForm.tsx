/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import React from "react";
import { WithTranslation, withTranslation } from "react-i18next";
import {
  InputField,
  LabelWithHelpIcon,
  ButtonGroup,
  Button,
  SubmitButton,
  Textarea,
  ErrorNotification,
  Notification,
  Level
} from "@scm-manager/ui-components";
import { modify, run } from "../api";
import { Script, ScriptExecutionResult, ScriptLinks } from "../types";
import ContentEditor from "../components/ContentEditor";
import Output from "../components/Output";
import DeleteScript from "./DeleteScript";

type Props = WithTranslation & {
  script: Script;
  onDelete: () => void;
  links: ScriptLinks;
};

type State = Script & {
  loading: boolean;
  error?: Error;
  result?: ScriptExecutionResult;
  saveSuccess: boolean;
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

  renderSuccessNotifications = () => {
    const { t } = this.props;
    const { saveSuccess } = this.state;

    return (
      <>{saveSuccess && <Notification type="success">{t("scm-script-plugin.editForm.saveSuccess")}</Notification>}</>
    );
  };

  isTitleValid = () => {
    return !!this.state.title;
  };

  isScriptValid = () => {
    return this.isTitleValid();
  };

  renderContent = () => {
    const { title, description, content, result } = this.state;
    const { t, script, onDelete } = this.props;

    return (
      <>
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
            <LabelWithHelpIcon label={t("scm-script-plugin.content")} helpText={t("scm-script-plugin.contentHelp")} />
            <ContentEditor name="content" value={content} onChange={this.onValueChange} />
          </div>
          {this.renderControlButtons()}
          <hr />
          <DeleteScript script={script} onDelete={onDelete} />
        </form>
        <Output result={result} />
      </>
    );
  };

  renderControlButtons = () => {
    const { loading } = this.state;
    const { script, links, t } = this.props;

    let run = null;
    if (links.execute) {
      run = <SubmitButton label={t("scm-script-plugin.editForm.run")} action={this.onRun} loading={loading} />;
    }

    let save = null;
    if (script._links.update) {
      const btnColor = run ? "default" : "primary";
      save = (
        <Button
          label={t("scm-script-plugin.editForm.save")}
          color={btnColor}
          action={this.onSave}
          loading={loading}
          disabled={!this.isScriptValid()}
        />
      );
    }

    return (
      <Level
        right={
          <ButtonGroup>
            {run}
            {save}
          </ButtonGroup>
        }
      />
    );
  };

  render() {
    const { error } = this.state;

    if (error) {
      return <ErrorNotification error={error} />;
    } else {
      return (
        <>
          {this.renderSuccessNotifications()}
          {this.renderContent()}
        </>
      );
    }
  }
}

export default withTranslation("plugins")(EditForm);
