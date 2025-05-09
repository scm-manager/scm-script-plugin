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
import { useHistory } from "react-router-dom";
import StoreDialog from "./StoreDialog";
import { Button, ButtonGroup, ErrorNotification, Level, SubmitButton } from "@scm-manager/ui-components";
import Output from "../../components/Output";
import ContentEditor from "../../components/ContentEditor";
import { useRunScript, useStoreScript } from "../../api";
import { Script, ScriptExecutionResult, ScriptLinks } from "../../types";
import { useDocumentTitle } from "@scm-manager/ui-core";

type Props = {
  links: ScriptLinks;
};

const Editor: FC<Props> = ({ links }) => {
  const [t] = useTranslation("plugins");
  const history = useHistory();
  const [script, setScript] = useState<Script>({ content: "println 'Hello World'", type: "Groovy", _links: {} });
  const [showStoreModal, setShowStoreModal] = useState(false);
  const [result, setResult] = useState<ScriptExecutionResult | undefined>();
  const { run, isLoading: runLoading, error: runError } = useRunScript(links.execute, r => setResult(r));
  const { store, isLoading: storeLoading, error: storeError } = useStoreScript(links.create, (id: string) =>
    history.push(`/admin/script/${id}`)
  );
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
