package sonia.scm.script.infrastructure;

import de.otto.edison.hal.HalRepresentation;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ListenerDto extends HalRepresentation {

  @NotNull
  private Class<?> eventType;
  private boolean asynchronous;

}
