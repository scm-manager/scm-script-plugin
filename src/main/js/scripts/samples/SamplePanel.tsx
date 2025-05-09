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
import { Sample } from "../../types";
import ContentViewer from "../../components/ContentViewer";

type Props = {
  sample: Sample;
};

const SamplePanel: FC<Props> = ({ sample }) => (
  <div className="content">
    <h4>{sample.title}</h4>
    <p>{sample.description}</p>
    <ContentViewer value={sample.content} />
  </div>
);

export default SamplePanel;
