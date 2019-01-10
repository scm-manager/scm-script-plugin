//@flow
import React from "react";

import AceEditor from "react-ace";

import "brace/mode/groovy";
import "brace/theme/tomorrow";

type Props = {
  value: string,
  name?: string,
  onChange: string => void
};

class ContentEditor extends React.Component<Props> {
  onChange = value => {
    const { name, onChange } = this.props;
    onChange(value, name);
  };

  render() {
    const { name, value } = this.props;

    return (
      <AceEditor
        mode="groovy"
        theme="tomorrow"
        onChange={this.onChange}
        showGutter={false}
        name={name ? name : "contentEditor"}
        value={value}
        className="box"
        style={{ width: "100%", height: "250px" }}
      />
    );
  }
}

export default ContentEditor;
