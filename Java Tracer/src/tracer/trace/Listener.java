package tracer.trace;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.debug.core.model.IThread;
import org.eclipse.jdt.internal.debug.core.IJDIEventListener;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.ExceptionRequest;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.jdi.request.ThreadDeathRequest;

import tracer.creation.wizard.pages.datamodels.SelectionType;
import tracer.creation.wizard.pages.datamodels.containers.StaticTree.Item;
import tracer.trace.writers.IWriter;

@SuppressWarnings("restriction")
public class Listener implements IJDIEventListener {

	private IWriter[] writers;
	public Listener(VirtualMachine vm, SelectionType selectionType, Object[] selectedClasses, IThread[] threads, IWriter... writers) {
		super();
		this.vm = vm;
		this.selectionType = selectionType;
		this.selectedClasses = selectedClasses;
		this.threads = threads;
		this.writers = writers;
	}
	
	void prepareClassesFiltering(EventRequest r)
	{
		Method addClassFilterPattern = null;
		Method addClassFilter = null;
		try {			
			addClassFilterPattern = r.getClass().getMethod("addClassExclusionFilter", String.class);
			addClassFilter = r.getClass().getMethod("addClassFilter", ReferenceType.class);
		} 
		catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}		
	
		for (Object o : selectedClasses)
		{
			Item item = (Item) o;
			if (item.get() instanceof ReferenceType)
			{
				ReferenceType refType = (ReferenceType) (item.get());
				try 
				{
				if (selectionType == SelectionType.Excludes)
				{
					addClassFilterPattern.invoke(r, refType.name());
				}
				else
				{
					addClassFilter.invoke(r, refType);
				}
				}
				catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}					
		}
		Method addThreadFilter = null;
		try {					
			addThreadFilter = r.getClass().getMethod("addThreadFilter", ThreadReference.class);
		} 
		catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		for (IThread thread : threads)
		{
			JDIThread javaThread = (JDIThread) thread;
			ThreadReference threadReference = javaThread.getUnderlyingThread();
			try {
				addThreadFilter.invoke(r, threadReference);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setEventRequests() {
        EventRequestManager mgr = vm.eventRequestManager();

        MethodEntryRequest methodEntryRequest = mgr.createMethodEntryRequest();
        prepareClassesFiltering(methodEntryRequest);
        methodEntryRequest.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
        methodEntryRequest.enable();

        MethodExitRequest methodExitRequest = mgr.createMethodExitRequest();
        prepareClassesFiltering(methodExitRequest);
        methodExitRequest.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
        methodExitRequest.enable();
        
        // want all exceptions
        ExceptionRequest excReq = mgr.createExceptionRequest(null, true, true);
        // suspend so we can step
        excReq.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
        excReq.enable();

        ThreadDeathRequest tdr = mgr.createThreadDeathRequest();
		for (IThread thread : threads)
		{
			JDIThread javaThread = (JDIThread) thread;
			ThreadReference threadReference = javaThread.getUnderlyingThread();
			tdr.addThreadFilter(threadReference);
			javaThread.addJDIEventListener(this, methodEntryRequest);
			javaThread.addJDIEventListener(this, methodExitRequest);
			javaThread.addJDIEventListener(this, excReq);
			javaThread.addJDIEventListener(this, tdr);
		}
        tdr.setSuspendPolicy(EventRequest.SUSPEND_NONE);
        tdr.enable();
        }
	
    /**
     * Dispatch incoming events
     */
    synchronized private void handleEvent(Event event) {
        if (event instanceof ExceptionEvent) {
            exceptionEvent((ExceptionEvent)event);
        } else if (event instanceof MethodEntryEvent) {
            methodEntryEvent((MethodEntryEvent)event);
        } else if (event instanceof MethodExitEvent) {
            methodExitEvent((MethodExitEvent)event);
        } else if (event instanceof ThreadDeathEvent) {
            threadDeathEvent((ThreadDeathEvent)event);
        } else if (event instanceof VMStartEvent) {
            vmStartEvent((VMStartEvent)event);
        } else if (event instanceof VMDeathEvent) {
            vmDeathEvent((VMDeathEvent)event);
        } else if (event instanceof VMDisconnectEvent) {
            vmDisconnectEvent((VMDisconnectEvent)event);
        } else {
            throw new Error("Unexpected event type");
        }
    }

    private void vmStartEvent(VMStartEvent event)  {
//         writer.println("-- VM Started --");
    }

    // Forward event for thread specific processing
    private void methodEntryEvent(MethodEntryEvent event)  {
    	for (IWriter writer : writers)
    	{
    		writer.onMethodEntryEvent(event);
    	}
    }

    // Forward event for thread specific processing
    private void methodExitEvent(MethodExitEvent event)  {
    	for (IWriter writer : writers)
    	{
    		writer.onMethodExitEvent(event);
    	}
    }

    void threadDeathEvent(ThreadDeathEvent event)  {
//        if (trace != null) {  // only want threads we care about
//            trace.threadDeathEvent(event);   // Forward event
//        }
    }


    private void exceptionEvent(ExceptionEvent event) 
    {
    	for (IWriter writer : writers)
    	{
    		writer.onExceptionEvent(event);
    	}
    }

    public void vmDeathEvent(VMDeathEvent event) {
    }

    public void vmDisconnectEvent(VMDisconnectEvent event) {
    }
	
	private VirtualMachine vm;	
	private SelectionType selectionType;
	private Object[] selectedClasses;
	private IThread[] threads;
	
	@Override
	public boolean handleEvent(Event event, JDIDebugTarget target, boolean suspendVote, EventSet eventSet) {
		handleEvent(event);
		return true;
	}

	@Override
	public void eventSetComplete(Event event, JDIDebugTarget target, boolean suspend, EventSet eventSet) {
		// TODO Auto-generated method stub
		
	}
}
