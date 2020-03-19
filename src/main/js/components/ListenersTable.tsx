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
import React from "react";
import { WithTranslation, withTranslation } from "react-i18next";
import styled from "styled-components";
import { Listeners, Listener } from "../types";
import { Select, Checkbox, Icon } from "@scm-manager/ui-components";

type Props = WithTranslation & {
  eventTypes: string[];
  listeners: Listeners;
  eventType?: string;
  asynchronous: boolean;

  onChange: (value: string, name: string) => void;
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

const DarkerVCenteredTd = styled(VCenteredTd)`
  background-color: whitesmoke;
`;

class ListenersTable extends React.Component<Props> {
  checkedIcon = (checked: boolean) => {
    if (checked) {
      return <i className="fas fa-check" />;
    }
    return null;
  };

  renderListenerRow = (listener: Listener, key: number) => {
    const { onDelete, t } = this.props;

    return (
      <tr key={key}>
        <td>{listener.eventType}</td>
        <VCenteredTd>{this.checkedIcon(listener.asynchronous)}</VCenteredTd>
        <VCenteredTd>
          <a className="level-item" onClick={() => onDelete(listener)}>
            <span className="icon is-small">
              <Icon name="trash" color="inherit" title={t("scm-script-plugin.listeners.remove")} />
            </span>
          </a>
        </VCenteredTd>
      </tr>
    );
  };

  renderFormRow = () => {
    const { eventTypes, eventType, asynchronous, onChange, onSubmit, t } = this.props;
    const options = eventTypes.map(eventType => {
      return {
        value: eventType,
        label: eventType
      };
    });

    options.unshift({
      value: "",
      label: ""
    });

    return (
      <tr>
        <DarkerVCenteredTd>
          <Select name="eventType" options={options} value={eventType} onChange={onChange} />
        </DarkerVCenteredTd>
        <DarkerVCenteredTd>
          <Checkbox name="asynchronous" checked={asynchronous} onChange={onChange} />
        </DarkerVCenteredTd>
        <DarkerVCenteredTd>
          <a className="level-item" onClick={onSubmit}>
            <span className="icon is-small">
              <Icon name="plus" color="inherit" title={t("scm-script-plugin.listeners.add")} />
            </span>
          </a>
        </DarkerVCenteredTd>
      </tr>
    );
  };

  render() {
    const { listeners, t } = this.props;

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
              {listeners.listeners.map(this.renderListenerRow)}
              {this.renderFormRow()}
            </tbody>
          </table>
        </ScrollingTable>
        <hr />
      </>
    );
  }
}

export default withTranslation("plugins")(ListenersTable);
