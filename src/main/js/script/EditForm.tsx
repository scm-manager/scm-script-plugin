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
import React, { FC, FormEvent, useState } from "react";
import { useTranslation } from "react-i18next";
import {
  Button,
  ButtonGroup,
  ErrorNotification,
  InputField,
  LabelWithHelpIcon,
  Level,
  Notification,
  SubmitButton,
  Textarea
} from "@scm-manager/ui-components";
import { useRunScript, useUpdateScript } from "../api";
import { Script, ScriptExecutionResult, ScriptLinks } from "../types";
import ContentEditor from "../components/ContentEditor";
import Output from "../components/Output";
import DeleteScript from "./DeleteScript";

type Props = {
  script: Script;
  links: ScriptLinks;
};

const EditForm: FC<Props> = ({ script, links }) => {
  const [t] = useTranslation("plugins");
  const [scriptState, setScriptState] = useState<Script>(script);
  const [result, setResult] = useState<ScriptExecutionResult | undefined>();
  const [saveSuccess, setSaveSuccess] = useState(false);
  const { error: updateError, isLoading: updateLoading, update } = useUpdateScript(script);
  const { error: runError, isLoading: runLoading, run } = useRunScript(script, r => setResult(r));

  const onRun = async (event: FormEvent<HTMLFormElement>) => {
    onAction(event);

    if (!links.execute) {
      return;
    }

    run(scriptState);
  };

  const onSave = (event: FormEvent<HTMLFormElement>) => {
    onAction(event);
    setScriptState({
      ...script,
      title: scriptState.title,
      description: scriptState.description,
      content: scriptState.content
    });
    update(scriptState);
    setSaveSuccess(true);
  };

  const onAction = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setSaveSuccess(false);
  };

  const renderSuccessNotifications = () => {
    return (
      <>{saveSuccess && <Notification type="success">{t("scm-script-plugin.editForm.saveSuccess")}</Notification>}</>
    );
  };

  const renderContent = () => {
    return (
      <>
        <form onSubmit={onRun}>
          <InputField
            name="title"
            label={t("scm-script-plugin.title")}
            helpText={t("scm-script-plugin.titleHelp")}
            value={scriptState.title}
            validationError={!scriptState.title}
            errorMessage={t("scm-script-plugin.titleValidationError")}
            onChange={title => setScriptState({ ...scriptState, title })}
          />
          <Textarea
            name="description"
            label={t("scm-script-plugin.description")}
            helpText={t("scm-script-plugin.descriptionHelp")}
            value={scriptState.description}
            onChange={description => setScriptState({ ...scriptState, description })}
          />
          <div className="field">
            <LabelWithHelpIcon label={t("scm-script-plugin.content")} helpText={t("scm-script-plugin.contentHelp")} />
            <ContentEditor
              value={scriptState.content}
              onChange={content => setScriptState({ ...scriptState, content })}
            />
          </div>
          {renderControlButtons()}
          <hr />
          <DeleteScript script={script} />
        </form>
        <Output result={result} />
      </>
    );
  };

  const renderControlButtons = () => {
    let execute = null;
    if (links.execute) {
      execute = <SubmitButton label={t("scm-script-plugin.editForm.run")} action={onRun} loading={runLoading} />;
    }

    let save = null;
    if (script._links.update) {
      const btnColor = execute ? "default" : "primary";
      save = (
        <Button
          label={t("scm-script-plugin.editForm.save")}
          color={btnColor}
          action={onSave}
          loading={updateLoading}
          disabled={!script.title}
        />
      );
    }

    return (
      <Level
        right={
          <ButtonGroup>
            {execute}
            {save}
          </ButtonGroup>
        }
      />
    );
  };

  if (updateError || runError) {
    return <ErrorNotification error={updateError || runError} />;
  } else {
    return (
      <>
        {renderSuccessNotifications()}
        {renderContent()}
      </>
    );
  }
};

export default EditForm;
