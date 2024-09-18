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

import { Links } from "@scm-manager/ui-types";
import { predicate } from "./predicate";

describe("test predicate", () => {
  const exec = (links: Links) => {
    return predicate({
      links
    });
  };

  it("should return true", () => {
    const result = exec({
      scripts: {
        href: "http://..."
      }
    });
    expect(result).toBe(true);
  });

  it("should return false, without script links", () => {
    const result = exec({});
    expect(result).toBe(false);
  });
});
