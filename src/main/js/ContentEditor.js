//@flow
import React from "react";

import AceEditor from "react-ace";

import "brace/mode/groovy";
import "brace/theme/tomorrow";

type Props = {
  value: string,
  onChange: string => void
};

class ContentEditor extends React.Component<Props> {
  render() {
    const { value, onChange } = this.props;

    return (
      <AceEditor
        mode="groovy"
        theme="tomorrow"
        onChange={onChange}
        showGutter={false}
        name="contentEditor"
        value={value}
        className="box"
        style={{ width: "100%", height: "250px" }}
      />
    );
  }
}

export default ContentEditor;
