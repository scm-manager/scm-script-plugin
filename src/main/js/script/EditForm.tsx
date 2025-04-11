/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
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
import { useDocumentTitle } from "@scm-manager/ui-core";

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
  const { error: runError, isLoading: runLoading, run } = useRunScript(links.execute, r => setResult(r));
  useDocumentTitle(
    t("scm-script-plugin.scriptTab.editTitle", { name: scriptState.title }),
    t("scm-script-plugin.navigation.stored"),
    t("scm-script-plugin.rootPage.title")
  );

  const onSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setSaveSuccess(false);
    run(scriptState);
  };

  const onSave = () => {
    setSaveSuccess(false);
    setScriptState({
      ...script,
      title: scriptState.title,
      description: scriptState.description,
      content: scriptState.content
    });
    update(scriptState);
    setSaveSuccess(true);
  };

  const renderControlButtons = () => {
    let execute = null;
    if (links.execute) {
      execute = (
        <SubmitButton
          label={t("scm-script-plugin.editForm.run")}
          loading={runLoading}
          scrollToTop={false}
        />
      );
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
        <form onSubmit={onSubmit}>
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
          {saveSuccess && <Notification type="success">{t("scm-script-plugin.editForm.saveSuccess")}</Notification>}
          <hr />
          <DeleteScript script={script} />
        </form>
        <Output result={result} />
      </>
    );
  }
};

export default EditForm;
