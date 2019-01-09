package sonia.scm.script.infrastructure;

import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class ScriptDto extends HalRepresentation {

  private String id;
  private String type;
  private String title;
  private String description;
  private String content;

  @Override
  @SuppressWarnings("squid:S1185") // We want to have this method available in this package
  protected HalRepresentation add(Links links) {
    return super.add(links);
  }
}
