//@flow
import React from "react";
import ContentEditor from "./ContentEditor";

type Props = {
  value: string
};

class ContentViewer extends React.Component<Props> {
  render() {
    const { value } = this.props;
    return <ContentEditor value={value} readOnly={true} />;
  }
}

export default ContentViewer;
