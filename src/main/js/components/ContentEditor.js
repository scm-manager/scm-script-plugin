//@flow
import React from "react";

import AceEditor from "react-ace";

import "ace-builds/src-noconflict/mode-groovy";
import "ace-builds/src-noconflict/theme-tomorrow";

type Props = {
  value?: string,
  name?: string,
  readOnly?: boolean,
  onChange: (value: string, name: string) => void
};

class ContentEditor extends React.Component<Props> {
  name = () => {
    const { name } = this.props;
    return name ? name : "contentEditor";
  };

  onChange = (value: string) => {
    const { onChange } = this.props;
    onChange(value, this.name());
  };

  render() {
    const { readOnly, value } = this.props;

    return (
      <AceEditor
        mode="groovy"
        theme="tomorrow"
        onChange={this.onChange}
        showGutter={true}
        readOnly={readOnly}
        name={this.name()}
        value={value ? value : ""}
        className="box"
        style={{ width: "100%", height: "250px" }}
      />
    );
  }
}

export default ContentEditor;
