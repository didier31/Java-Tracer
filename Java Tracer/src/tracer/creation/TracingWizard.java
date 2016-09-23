package tracer.creation;

import java.util.List;

import org.eclipse.jface.wizard.Wizard;
import com.sun.jdi.ReferenceType;

import tracer.creation.wizard.pages.SelectClasses;
import tracer.creation.wizard.pages.datamodels.SelectionType;

public class TracingWizard extends Wizard {

	SelectClasses selectClassesPage; 
	
	public TracingWizard(List<ReferenceType> loadedClasses) {
		setWindowTitle("New Trace directive");
		selectClassesPage = new SelectClasses(loadedClasses);
		addPage(selectClassesPage);
	}
	
	public SelectionType getSelectionType()
	{
		return selectClassesPage.getSelectionType();
	}
	
	public Object[] getCheckedClasses()
	{
		return selectClassesPage.getCheckedClasses();
	}
	
	@Override
	public boolean performFinish() {
		return true;
	}

}
