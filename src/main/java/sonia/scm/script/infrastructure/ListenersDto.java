package sonia.scm.script.infrastructure;

import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("squid:S2160") // we need no equals or hashCode
public class ListenersDto extends HalRepresentation {

  private List<ListenerDto> listeners;
  private boolean storeListenerExecutionResults;

  @Override
  @SuppressWarnings("squid:S1185") // We want to have this method available in this package
  protected HalRepresentation add(Links links) {
    return super.add(links);
  }

}