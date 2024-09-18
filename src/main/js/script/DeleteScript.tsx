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
import { ConfirmAlert, DeleteButton, Level } from "@scm-manager/ui-components";
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
            <DeleteButton label={t("scm-script-plugin.delete.button")} action={() => setShowModal(true)} />{" "}
            {showModal ? (
              <ConfirmAlert
                title={t("scm-script-plugin.delete.confirmAlert.title")}
                message={t("scm-script-plugin.delete.confirmAlert.message")}
                close={() => setShowModal(false)}
                buttons={[
                  {
                    label: t("scm-script-plugin.delete.confirmAlert.submit"),
                    onClick: deleteScript
                  },
                  {
                    className: "is-info",
                    label: t("scm-script-plugin.delete.confirmAlert.cancel"),
                    onClick: () => setShowModal(false)
                  }
                ]}
              />
            ) : null}
          </>
        }
      />
    );
  }
  return deleteLink;
};

export default DeleteScript;
