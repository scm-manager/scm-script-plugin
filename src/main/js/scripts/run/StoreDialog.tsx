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

import React, { FC } from "react";
import { useTranslation } from "react-i18next";
import StoreForm from "./StoreForm";
import { Script } from "../../types";
import { Modal } from "@scm-manager/ui-components";

type Props = {
  onSubmit: (p: Script) => void;
  onClose: () => void;
  storeLoading: boolean;
};

const StoreDialog: FC<Props> = ({ onSubmit, onClose, storeLoading }) => {
  const [t] = useTranslation("plugins");
  const body = (
    <div className="content">
      <StoreForm onSubmit={onSubmit} onAbort={onClose} storeLoading={storeLoading} />
    </div>
  );

  return (
    <Modal title={t("scm-script-plugin.storeDialog.title")} closeFunction={onClose} body={body} active={true} />
  );
};

export default StoreDialog;
