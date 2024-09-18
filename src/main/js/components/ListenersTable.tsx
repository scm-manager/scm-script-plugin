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
import styled from "styled-components";
import { Listener, Listeners } from "../types";
import { Checkbox, Icon, Select } from "@scm-manager/ui-components";

type Props = {
  eventTypes: string[];
  listeners: Listeners;
  eventType?: string;
  asynchronous: boolean;
  onChangeEventType: (value: string) => void;
  onChangeAsynchronous: (value: boolean) => void;
  onSubmit: () => void;
  onDelete: (listener: Listener) => void;
};

const ScrollingTable = styled.div`
  overflow-x: auto;
`;

const VCenteredTd = styled.td`
  display: table-cell;
  vertical-align: middle !important;
`;

const ListenersTable: FC<Props> = ({
  listeners,
  eventTypes,
  eventType,
  asynchronous,
  onChangeAsynchronous,
  onChangeEventType,
  onSubmit,
  onDelete
}) => {
  const [t] = useTranslation("plugins");

  const checkedIcon = (checked: boolean) => {
    if (checked) {
      return <i className="fas fa-check" />;
    }
    return null;
  };

  const renderListenerRow = (listener: Listener, key: number) => {
    return (
      <tr key={key}>
        <td>{listener.eventType}</td>
        <VCenteredTd>{checkedIcon(listener.asynchronous)}</VCenteredTd>
        <VCenteredTd>
          <Icon
            name="trash"
            className="is-clickable"
            color="inherit"
            title={t("scm-script-plugin.listeners.remove")}
            onClick={() => onDelete(listener)}
          />
        </VCenteredTd>
      </tr>
    );
  };

  const renderFormRow = () => {
    const options = eventTypes.map(types => {
      return {
        value: types,
        label: types
      };
    });

    options.unshift({
      value: "",
      label: ""
    });

    return (
      <tr>
        <VCenteredTd className={"has-background-secondary-less"}>
          <Select name="eventType" options={options} value={eventType} onChange={onChangeEventType} />
        </VCenteredTd>
        <VCenteredTd className={"has-background-secondary-less"}>
          <Checkbox name="asynchronous" checked={asynchronous} onChange={onChangeAsynchronous} />
        </VCenteredTd>
        <VCenteredTd className={"has-background-secondary-less"}>
          <Icon
            name="plus"
            color="inherit"
            className="is-clickable"
            title={t("scm-script-plugin.listeners.add")}
            onClick={onSubmit}
          />
        </VCenteredTd>
      </tr>
    );
  };

  return (
    <>
      <ScrollingTable>
        <table className="table is-hoverable is-fullwidth">
          <thead>
            <tr>
              <th>{t("scm-script-plugin.listeners.eventType")}</th>
              <th>{t("scm-script-plugin.listeners.asynchronous")}</th>
              <th>{t("scm-script-plugin.listeners.action")}</th>
            </tr>
          </thead>
          <tbody>
            {listeners.listeners.map(renderListenerRow)}
            {renderFormRow()}
          </tbody>
        </table>
      </ScrollingTable>
      <hr />
    </>
  );
};

export default ListenersTable;
