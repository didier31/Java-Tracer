package tracer.creation.wizard.pages.datamodels.providers;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import tracer.creation.wizard.pages.datamodels.containers.StaticTree.Item;

import org.eclipse.jdt.ui.ISharedImages;

public class ClassesHierarchyLabelProvider implements ILabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return true;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	@Override
	public Image getImage(Object element) {
		Item item = (Item) element;
		return (item.get() instanceof String) ?
				org.eclipse.jdt.ui.JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_PACKAGE)
		       :org.eclipse.jdt.ui.JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_CLASS);
	}

	@Override
	public String getText(Object element) {
		Item item = (Item) element;
		String name = item.get().toString();
		String[] qualifiedName = name.split("\\.");
		return qualifiedName[qualifiedName.length - 1]; 		
	}

}
