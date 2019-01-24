package sonia.scm.script.infrastructure;

import de.otto.edison.hal.HalRepresentation;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@SuppressWarnings("squid:S2160") // we need no equals or hashCode
public class ListenerDto extends HalRepresentation {

  @NotNull
  private Class<?> eventType;
  private boolean asynchronous;

}
