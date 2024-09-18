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
import { Button, ButtonGroup, InputField, Level, SubmitButton, Textarea } from "@scm-manager/ui-components";
import { Script } from "../../types";

type Props = {
  onSubmit: (p: Script) => void;
  onAbort: () => void;
  storeLoading: boolean;
};

const StoreForm: FC<Props> = ({ onSubmit, onAbort, storeLoading }) => {
  const [t] = useTranslation("plugins");
  const [script, setScript] = useState<Script>({
    title: "",
    description: "",
    type: "Groovy",
    _links: {}
  });
  const [titleDirty, setTitleDirty] = useState(false);

  const onChange = (value: string, name?: string) => {
    if (name) {
      if (name === "title") {
        setTitleDirty(true);
      }
      setScript({
        ...script,
        [name]: value
      });
    }
  };

  const submit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    onSubmit(script);
  };

  const isTitleInvalid = () => {
    return titleDirty && !script.title;
  };

  const isScriptValid = () => {
    return !isTitleInvalid();
  };

  return (
    <form onSubmit={submit}>
      <InputField
        autofocus={true}
        name="title"
        label={t("scm-script-plugin.title")}
        helpText={t("scm-script-plugin.titleHelp")}
        onChange={onChange}
        value={script.title}
        validationError={isTitleInvalid()}
        errorMessage={t("scm-script-plugin.titleValidationError")}
      />
      <Textarea
        name="description"
        label={t("scm-script-plugin.description")}
        helpText={t("scm-script-plugin.descriptionHelp")}
        onChange={onChange}
        value={script.description}
      />
      <Level
        right={
          <ButtonGroup>
            <SubmitButton
              label={t("scm-script-plugin.storeForm.submit")}
              loading={storeLoading}
              disabled={!isScriptValid() || !script.title}
            />
            <Button label={t("scm-script-plugin.storeForm.abort")} action={onAbort} disabled={storeLoading} />
          </ButtonGroup>
        }
      />
    </form>
  );
};

export default StoreForm;
