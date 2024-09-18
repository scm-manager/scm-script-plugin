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
import { CodeEditor } from "@scm-manager/scm-code-editor-plugin";
import styled from "styled-components";

type Props = {
  value?: string;
  readOnly?: boolean;
  onChange: (value: string) => void;
};

const StyledCodeEditor = styled(CodeEditor)`
  width: 100%;
  height: 250px;
`;

const ContentEditor: FC<Props> = ({ value, readOnly, onChange }) => (
  <StyledCodeEditor
    className="box"
    language="groovy"
    onChange={onChange}
    disabled={readOnly || false}
    content={value || ""}
    initialFocus={true}
  />
);

export default ContentEditor;
