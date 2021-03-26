package transformed;

public class PublicMethodRename {

  private String value = "original";

  /** rename this to getValue() */
  public String getValueString() {
    return value;
  }
}
