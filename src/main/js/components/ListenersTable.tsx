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
