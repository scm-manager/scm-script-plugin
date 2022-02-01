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
