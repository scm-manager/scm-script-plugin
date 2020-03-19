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
import styled from "styled-components";
import { ScriptExecutionResult } from "../types";

const Figure = styled.figure`
  margin-top: 1em;
  position: relative;
`;

const Figcaption = styled.figcaption`
  outline: 0;
  padding-bottom: 0;
  padding-top: 0;
  position: absolute;
  right: 0.25rem;
  top: 0.25rem;
  font-size: 0.75rem;
`;

type Props = {
  result?: ScriptExecutionResult;
};

class Output extends React.Component<Props> {
  createCaption(result: ScriptExecutionResult) {
    const duration = Math.abs(new Date(result.ended) - new Date(result.started));
    const prefix = result.success ? "success" : "failed";
    return `${prefix} in ${duration}ms`;
  }

  createCaptionClass(result: ScriptExecutionResult) {
    return result.success ? "has-text-success" : "has-text-danger";
  }

  render() {
    const { result } = this.props;
    if (!result) {
      return null;
    }
    return (
      <Figure>
        <pre>
          <code>{result.output}</code>
        </pre>
        <Figcaption className={this.createCaptionClass(result)}>{this.createCaption(result)}</Figcaption>
      </Figure>
    );
  }
}

export default Output;
