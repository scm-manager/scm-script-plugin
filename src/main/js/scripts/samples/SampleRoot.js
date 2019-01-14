//@flow
import React from "react";
import samples from "./samples";
import SamplePanel from "./SamplePanel";

type Props = {};

class SampleRoot extends React.Component<Props> {
  render() {
    return (
      <div>
        {samples.map(sample => (
          <SamplePanel sample={sample} />
        ))}
      </div>
    );
  }
}

export default SampleRoot;
