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
package sonia.scm.script.infrastructure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import sonia.scm.script.domain.TypeRepository;

import javax.inject.Inject;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JSR223TypeRepository implements TypeRepository {

  private final Map<String, String> byExtension;

  @Inject
  public JSR223TypeRepository(ScriptEngineManagerProvider scriptEngineManagerProvider) {
    this.byExtension = collectTypes(scriptEngineManagerProvider);
  }

  private Map<String, String> collectTypes(ScriptEngineManagerProvider scriptEngineManagerProvider) {
    Map<String, String> types = Maps.newLinkedHashMap();
    ScriptEngineManager engineManager = scriptEngineManagerProvider.get();
    for (ScriptEngineFactory factory : engineManager.getEngineFactories()) {
      String type = factory.getLanguageName();

      for (String extension : factory.getExtensions()) {
        types.put(extension, type);
      }
    }
    return types;
  }

  @Override
  public Optional<String> findByExtension(String extension) {
    return Optional.ofNullable(byExtension.get(extension));
  }

  @Override
  public List<String> findAll() {
    return ImmutableList.copyOf(byExtension.values()).asList();
  }
}
