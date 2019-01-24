package sonia.scm.script.infrastructure;

import de.otto.edison.hal.HalRepresentation;
import de.otto.edison.hal.Links;
import lombok.Getter;
import lombok.Setter;
import sonia.scm.script.domain.ExecutionHistoryEntry;

import java.util.List;

@Getter
@Setter
public class ExecutionHistoryDto extends HalRepresentation {

  private List<ExecutionHistoryEntry> entries;

  public ExecutionHistoryDto() {
  }

  public ExecutionHistoryDto(Links links, List<ExecutionHistoryEntry> entries) {
    super(links);
    this.entries = entries;
  }
}
