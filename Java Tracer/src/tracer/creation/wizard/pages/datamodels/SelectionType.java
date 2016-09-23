package tracer.creation.wizard.pages.datamodels;

public enum SelectionType {
	Excludes("Excludes"),
	Includes("Includes");
	
	  private final String display;
	  
	  private SelectionType(String s) {
	    display = s;
	  }
	  @Override
	  public String toString() {
	    return display;
	  }
	  
	  
}
