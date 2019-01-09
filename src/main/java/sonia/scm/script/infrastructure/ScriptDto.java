package sonia.scm.script.infrastructure;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@XmlRootElement(name = "script")
public class ScriptDto {

  private String id;
  private String type;
  private String title;
  private String description;
  private String content;

}
