package tracer.creation.wizard.pages.datamodels.providers;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import tracer.creation.wizard.pages.datamodels.ClassesHierarchy;
import tracer.creation.wizard.pages.datamodels.containers.StaticTree.Item;

public class ClassesHierarchyContentProvider implements ITreeContentProvider {

	ClassesHierarchy packageHierarchy;
	
	@Override
	 public void inputChanged(Viewer arg0, Object oldValue, Object newValue) {
		  packageHierarchy = (ClassesHierarchy) newValue;
		  }
	
	@Override
	public Object[] getElements(Object inputElement) {
		packageHierarchy = (ClassesHierarchy) inputElement;
		return packageHierarchy.getItems(0);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return packageHierarchy.getChildren((Item) parentElement) ;
	}

	@Override
	public Object getParent(Object element) {
		return packageHierarchy.getParent((Item) element);
	}

	@Override
	public boolean hasChildren(Object element) {
		return packageHierarchy.hasChildren((Item) element);
	}	
}
