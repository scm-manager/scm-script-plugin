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

import React, { FC, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { useIndexLink } from "@scm-manager/ui-api";
import { Button, ErrorNotification, useDocumentTitle } from "@scm-manager/ui-core";
import Output from "../../components/Output";
import ContentEditor from "../../components/ContentEditor";
import { useRunScript, useStoreScript, useScriptLinks } from "../../api";
import { Script, ScriptExecutionResult } from "../../types";
import StoreDialog from "./StoreDialog";

const Editor: FC = () => {
  const navigate = useNavigate();
  const [script, setScript] = useState<Script>({ content: "println 'Hello World'", type: "Groovy", _links: {} });
  const [showStoreModal, setShowStoreModal] = useState(false);
  const [result, setResult] = useState<ScriptExecutionResult | undefined>();
  const indexLink = useIndexLink("scripts");
  const { data: links } = useScriptLinks(indexLink);
  const executeLink = links?.execute;
  const createLink = links?.create;
  const { run, isLoading: runLoading, error: runError } = useRunScript(executeLink, (r) => setResult(r));

  const {
    store,
    isLoading: storeLoading,
    error: storeError,
  } = useStoreScript(createLink, (id: string) => navigate(`/admin/script/${id}`));
  const [t] = useTranslation("plugins");
  useDocumentTitle(t("scm-script-plugin.navigation.run"), t("scm-script-plugin.rootPage.title"));

  const execute = () => {
    setResult(undefined);
    run(script);
  };

  const storeDialog = showStoreModal ? (
    <StoreDialog
      onSubmit={(s: Script) => store({ ...script, ...s })}
      onClose={() => setShowStoreModal(false)}
      storeLoading={storeLoading}
    />
  ) : null;

  const error = runError || storeError;

  const body = error ? <ErrorNotification error={error} /> : <Output result={result} />;

  return (
    <>
      <div className="content">
        <h3>{t("scm-script-plugin.navigation.run")}</h3>
      </div>
      <ContentEditor onChange={(content) => setScript({ ...script, content })} value={script.content} />
      <div className="buttons is-flex is-justify-content-flex-end">
        {executeLink ? (
          <Button variant="primary" onClick={execute} isLoading={runLoading} disabled={!executeLink}>
            {t("scm-script-plugin.editor.submit")}
          </Button>
        ) : null}
        {createLink ? (
          <Button onClick={() => setShowStoreModal(true)} isLoading={storeLoading} disabled={!createLink}>
            {t("scm-script-plugin.editor.store")}
          </Button>
        ) : null}
      </div>
      {body}
      {storeDialog}
    </>
  );
};

export default Editor;
