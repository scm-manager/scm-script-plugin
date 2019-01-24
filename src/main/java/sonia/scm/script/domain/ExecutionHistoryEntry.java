package sonia.scm.script.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionHistoryEntry {
  private Listener listener;
  private ExecutionResult result;
}
