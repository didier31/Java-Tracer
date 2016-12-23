package tracer.creation;

import java.util.List;

import org.eclipse.jface.wizard.Wizard;
import com.sun.jdi.ReferenceType;

import tracer.creation.wizard.pages.SelectClasses;
import tracer.creation.wizard.pages.datamodels.SelectionType;

public class TracingWizard extends Wizard {

	SelectClasses selectClassesPage; 
	SelectionType selectionType = null;
	Object[] checkedClasses = null;
	
	public TracingWizard(List<ReferenceType> loadedClasses) {
		setWindowTitle("New Trace directive");
		selectClassesPage = new SelectClasses(loadedClasses);
		addPage(selectClassesPage);
	}
	
	public SelectionType getSelectionType()
	{
		if (selectionType == null)
		   return selectClassesPage.getSelectionType();
		else
			return selectionType;
	}
	
	public Object[] getCheckedClasses()
	{
		if (checkedClasses == null)
		{
		   return selectClassesPage.getCheckedClasses();
		   /* Object[] checkedClasses = selectClassesPage.getCheckedClasses();
		   return checkedClasses != null ? checkedClasses : new Object[0]; */
		}
		else
			return checkedClasses;
	}
	
	@Override
	public boolean performFinish() {
		selectionType = getSelectionType();
		checkedClasses = getCheckedClasses();
		return true;
	}

}
