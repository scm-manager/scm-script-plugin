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
import React, { FC, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import StoreDialog from "./StoreDialog";
import { Button, ButtonGroup, ErrorNotification, Level, SubmitButton } from "@scm-manager/ui-components";
import Output from "../../components/Output";
import ContentEditor from "../../components/ContentEditor";
import { useRunScript, useStoreScript } from "../../api";
import { Script, ScriptExecutionResult, ScriptLinks } from "../../types";

type Props = {
  links: ScriptLinks;
};

const Editor: FC<Props> = ({ links }) => {
  const [t] = useTranslation("plugins");
  const history = useHistory();
  const [script, setScript] = useState<Script>({ content: "println 'Hello World'", type: "Groovy", _links: {} });
  const [showStoreModal, setShowStoreModal] = useState(false);
  const [result, setResult] = useState<ScriptExecutionResult | undefined>();
  const { run, isLoading: runLoading, error: runError } = useRunScript(links.execute!, r => setResult(r));
  const { store, isLoading: storeLoading, error: storeError } = useStoreScript(links.create!, (id: string) =>
    history.push(`/admin/script/${id}`)
  );

  const execute = () => {
    setResult(undefined);
    run(script);
  };

  const storeDialog = showStoreModal ? (
    <StoreDialog onSubmit={(s: Script) => store(s)} onClose={() => setShowStoreModal(false)} />
  ) : null;

  const error = runError || storeError;

  const body = error ? <ErrorNotification error={error} /> : <Output result={result} />;

  return (
    <>
      <div className="content">
        <h3>{t("scm-script-plugin.navigation.run")}</h3>
      </div>
      <ContentEditor onChange={content => setScript({ ...script, content })} value={script.content} />
      <Level
        right={
          <ButtonGroup>
            {links.execute ? (
              <SubmitButton
                label={t("scm-script-plugin.editor.submit")}
                action={execute}
                loading={runLoading}
                disabled={!links.execute}
              />
            ) : null}
            {links.create ? (
              <Button
                label={t("scm-script-plugin.editor.store")}
                action={() => setShowStoreModal(true)}
                loading={storeLoading}
                disabled={!links.create}
              />
            ) : null}
          </ButtonGroup>
        }
      />
      {body}
      {storeDialog}
    </>
  );
};

export default Editor;
