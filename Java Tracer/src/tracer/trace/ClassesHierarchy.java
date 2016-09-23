package tracer.trace;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import com.sun.jdi.ReferenceType;

public class ClassesHierarchy extends tracer.creation.wizard.pages.datamodels.ClassesHierarchy {

	public ClassesHierarchy(Collection<ReferenceType> list) {
		super(list);
	}
	
	public boolean contains(ReferenceType refType)
	{
		String[] qualifiedName = refType.name().split("\\.");
		Item[] itemsOfALevel = getItems(0);
		boolean isFound = itemsOfALevel.length > 0;
		for (int i = 0;  isFound && itemsOfALevel.length > 0 && i < qualifiedName.length; i++)
		{
			int foundIdx = Arrays.binarySearch(itemsOfALevel, qualifiedName[i], new ItemComparator());
			if (0 <= foundIdx && foundIdx < itemsOfALevel.length)
			{
				if (ItemComparator.itemComparator.compare(qualifiedName[i], itemsOfALevel[foundIdx]) == 0)
				{
					itemsOfALevel = getChildren(itemsOfALevel[foundIdx]);	
				}
				else
				{
					isFound = false;
				}
			}
		}
	return isFound;
	}

	static class ItemComparator implements Comparator<Object>
	{

		public static final ItemComparator itemComparator = new ItemComparator();
		
		@Override
		public int compare(Object o1, Object o2) {
			String st[] = new String[2];
			Object o[] = new Object[]{ o1, o2};
			
			for (int i = 0; i < st.length; i++)
			{
				if (o[i] instanceof Item)
				{
					Object oo = ((Item) o[i]).get();
					if (oo instanceof String)
					{
						st[i] = (String) oo;
					}
					else if (oo instanceof ReferenceType)
					{						
						st[i] = ((ReferenceType) oo).name();
						st[i] = st[i].substring(st[i].lastIndexOf("\\."), st[i].length());
						
					}
				}
				else if (o[i] instanceof String)
				{
					st[i] = (String) o[i];
				}
			}
		return st[0].compareTo(st[1]);	
		}
		
	}
}
