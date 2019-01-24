package sonia.scm.script.infrastructure;

import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EventTypesDto extends HalRepresentation {

  private List<Class<?>> eventTypes;

  public EventTypesDto(Links links, List<Class<?>> eventTypes) {
    super(links);
    this.eventTypes = eventTypes;
  }
}
