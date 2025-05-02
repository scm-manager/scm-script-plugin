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
import { Dialog, Level, Button, Icon } from "@scm-manager/ui-core";
import { Script } from "../types";
import { useDeleteScript } from "../api";
import { useHistory } from "react-router-dom";

type Props = {
  script: Script;
};

const DeleteScript: FC<Props> = ({ script }) => {
  const [t] = useTranslation("plugins");
  const history = useHistory();
  const [showModal, setShowModal] = useState(false);
  const { deleteScript } = useDeleteScript(script, () => history.push("/admin/scripts"));

  let deleteLink = null;
  if (script._links.delete) {
    deleteLink = (
      <Level
        right={
          <>
            <Dialog
              trigger={
                <Button variant="signal">
                  <Icon className="mr-1">times</Icon>
                  {t("scm-script-plugin.delete.button")}
                </Button>
              }
              title={t("scm-script-plugin.delete.confirmAlert.title")}
              footer={[
                <Button onClick={deleteScript}>{t("scm-script-plugin.delete.confirmAlert.submit")}</Button>,
                <Button variant="primary" autoFocus onClick={() => setShowModal(false)}>
                  {t("scm-script-plugin.delete.confirmAlert.cancel")}
                </Button>,
              ]}
              open={showModal}
              onOpenChange={setShowModal}
            >
              {t("scm-script-plugin.delete.confirmAlert.message")}
            </Dialog>
          </>
        }
      />
    );
  }
  return deleteLink;
};

export default DeleteScript;
