package tracer.creation.wizard.pages.datamodels.containers;

import java.util.Vector;

public abstract class StaticTree {
	private Object[] tree; 
	
	public class Item implements Comparable<Item>
	{
		private Object object;
		private int idx;
		
		public Item(int idx, Object object)
		{
			this.idx = idx;
			this.object = object;
		}
		
		public int getIdx()
		{
			return idx;
		}
		
		public Object get()
		{
			return object;
		}

		@Override
		public int compareTo(Item i) {
			return ((Comparable) get()).compareTo((Comparable) i.get());
		}			
	}
	
	public Item[] getItems(int idx)
	{
		if (idx == tree.length)
		{
			return new Item[0];
		}
		Object elt = tree[idx]; 
		if (elt instanceof Integer)
		{
			Integer count = (Integer) elt; // in fact, descendants and count's cells too
			Vector<Item> children = new Vector<Item>(count);
			int idxEnd = idx + count; 
			idx++;
			while (idx < idxEnd)
			{
				elt = tree[idx];
				if (elt instanceof Integer)
				{
					idx += (Integer) elt;
				}
				else
				{
					children.add(new Item(idx, tree[idx]));
					idx++;
				}
			}
			return (Item[]) children.toArray(new Item[0]);
		}
		else
			return null;
	}
	
	public Item[] getChildren(Item item)
	{
		int idx = item.getIdx();
		if (idx < tree.length - 1)
		{
			idx++;
			Item[] children = getItems(idx);
			if (children == null)
			{
				children = new Item[0];
			}
			return children; 
		}
		else
		{
			return new Item[0];
		}
	}	
	
	public Item getParent(Item item)
	{
		boolean found = false;
		int idx = item.getIdx() - 1;
		Item parent = null;
		while (idx > 0 && !found)
		{
			if (tree[idx] instanceof Integer)
			{
				Integer childrenCount = (Integer) tree[idx];
				found = item.getIdx() < idx + childrenCount;
				if (found && idx > 0)
				{
					idx--;
					parent = new Item(idx, tree[idx]);
				}
			}
			idx--;				
		}		
		return parent;
	}
	
	public boolean hasChildren(Item item)
	{
		return item.getIdx() + 1 < tree.length && tree[item.getIdx() + 1] instanceof Integer;
	}
	
	protected void setTree(Object[] tree)
	{
		this.tree = tree; 
	}
}
