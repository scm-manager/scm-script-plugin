package sonia.scm.script.infrastructure;

import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@SuppressWarnings("squid:S2160") // we need no equals or hashCode
class ScriptDto extends HalRepresentation {

  private String id;

  @NotNull
  @Size(min = 1)
  private String type;

  @NotNull
  @Size(min = 1)
  private String title;

  private String description;
  private String content;

  @Override
  @SuppressWarnings("squid:S1185") // We want to have this method available in this package
  protected HalRepresentation add(Links links) {
    return super.add(links);
  }
}
