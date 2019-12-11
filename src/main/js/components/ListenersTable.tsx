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

class ListenersTable extends React.Component<Props> {
  checkedIcon = (checked: boolean) => {
    if (checked) {
      return <i className="fas fa-check" />;
    }
    return null;
  };

  renderListenerRow = (listener: Listener, key: number) => {
    const { onDelete } = this.props;

    return (
      <tr key={key}>
        <td>{listener.eventType}</td>
        <VCenteredTd>{this.checkedIcon(listener.asynchronous)}</VCenteredTd>
        <VCenteredTd>
          <a className="level-item" onClick={onDelete}>
            <span className="icon is-small">
              <i className="fas fa-trash" />
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

    return (
      <tr>
        <td className="is-darker">
          <Select name="eventType" options={options} value={eventType} onChange={onChange} />
        </td>
        <VCenteredTd className="is-darker">
          <Checkbox name="asynchronous" checked={asynchronous} onChange={onChange} />
        </VCenteredTd>
        <VCenteredTd className="is-darker">
          <a className="level-item" onClick={onSubmit}>
            <span className="icon is-small">
              <Icon name="plus" color="inherit" title={t("scm-script-plugin.listeners.submit")} />
            </span>
          </a>
        </VCenteredTd>
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
                <th />
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
