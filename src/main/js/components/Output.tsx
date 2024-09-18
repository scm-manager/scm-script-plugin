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

const Output: FC<Props> = ({ result }) => {
  const createCaption = (r: ScriptExecutionResult) => {
    const duration = Math.abs(new Date(r.ended).getTime() - new Date(r.started).getTime());
    const prefix = r.success ? "success" : "failed";
    return `${prefix} in ${duration}ms`;
  };

  const createCaptionClass = (r: ScriptExecutionResult) => {
    return r.success ? "has-text-success" : "has-text-danger";
  };

  if (!result) {
    return null;
  }
  return (
    <Figure>
      <pre>
        <code>{result.output}</code>
      </pre>
      <Figcaption className={createCaptionClass(result)}>{createCaption(result)}</Figcaption>
    </Figure>
  );
};

export default Output;
