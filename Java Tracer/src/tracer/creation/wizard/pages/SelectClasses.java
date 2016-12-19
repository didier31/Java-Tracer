package tracer.creation.wizard.pages;

import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.IBeanValueProperty;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;

import com.sun.jdi.ReferenceType;

import tracer.creation.wizard.pages.datamodels.ClassesHierarchy;
import tracer.creation.wizard.pages.datamodels.SelectionType;
import tracer.creation.wizard.pages.datamodels.providers.ClassesHierarchyContentProvider;
import tracer.creation.wizard.pages.datamodels.providers.ClassesHierarchyLabelProvider;

import org.eclipse.jface.databinding.viewers.IViewerObservableValue;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;

public class SelectClasses extends WizardPage {
	// Input data
	final private ClassesHierarchy loadedClasses;
	
	// Output data
	private SelectionType selectionType = SelectionType.Excludes;
	private ContainerCheckedTreeViewer checkboxTreeViewer;
	
	public SelectionType getSelectionType()
	{
		return selectionType;
	}
	
	public void setSelectionType(SelectionType selectionType)
	{
		this.selectionType = selectionType;
	}
	
	public Object[] getCheckedClasses()
	{
		return checkboxTreeViewer.getCheckedElements();
	}
	
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);

		setControl(container);
		container.setLayout(new GridLayout(1, false));
		
		ComboViewer selectionTypeCombo = new ComboViewer(container, SWT.NONE | SWT.READ_ONLY);
		Combo combo = selectionTypeCombo.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		checkboxTreeViewer = new ContainerCheckedTreeViewer(container, SWT.BORDER | SWT.CHECK);
		checkboxTreeViewer.setContentProvider(new ClassesHierarchyContentProvider());
		checkboxTreeViewer.setLabelProvider(new ClassesHierarchyLabelProvider());
		checkboxTreeViewer.setInput(getLoadedClasses());
		Tree tree = checkboxTreeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		// Data binding
		selectionTypeCombo.setContentProvider(ArrayContentProvider.getInstance());
		selectionTypeCombo.setInput(SelectionType.values());
		DataBindingContext bindingContext = new DataBindingContext();

		// The second key to binding a combo to an Enum is to use a
		// selection observable from the ComboViewer:
		IViewerObservableValue widgetObservable = ViewersObservables.observeSingleSelection(selectionTypeCombo);		
		IBeanValueProperty selectionTypeProperty = PojoProperties.value(getClass(), "selectionType"); 
		
		bindingContext.bindValue(widgetObservable,
				                 selectionTypeProperty.observe(this));
	}
	
	/**
	 * Create the wizard.
	 * @param threads 
	 * @param loadedClasses 
	 */
	public SelectClasses(List<ReferenceType> loadedClasses) {
		super("SelectClasses");
		setTitle("Select classes");
		setMessage("Select classes to include or exclude.");
		setDescription("Select classes to include or exclude.");
		
		this.loadedClasses = new ClassesHierarchy(loadedClasses);
	}

	public ClassesHierarchy getLoadedClasses() {
		return loadedClasses;
	}
}
