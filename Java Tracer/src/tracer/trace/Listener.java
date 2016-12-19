package tracer.trace;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.debug.core.model.IThread;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventQueue;
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
import tracer.trace.writers.IWriter;

@SuppressWarnings("restriction")
public class Listener implements Runnable {

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
			addClassFilterPattern = (selectionType == SelectionType.Excludes) ? 
					r.getClass().getMethod("addClassExclusionFilter", String.class):
			        r.getClass().getMethod("addClassFilter", String.class);
					
			addClassFilter = r.getClass().getMethod("addClassFilter", ReferenceType.class);
		} 
		catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}		
	
		for (Object o : selectedClasses)
		{
			if (o instanceof String)
			{
				String qualifiedName = (String) o;
				try {
					addClassFilterPattern.invoke(r, qualifiedName + ".*");
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			else if (o instanceof ReferenceType)
			{
				if (selectionType == SelectionType.Excludes)
				{
					try {
						addClassFilterPattern.invoke(r, ((ReferenceType) o).name());
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}						
				}
				else
				{
					try {
						addClassFilter.invoke(r, (ReferenceType) o);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
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
	
	void setEventRequests() {
        EventRequestManager mgr = vm.eventRequestManager();

        // want all exceptions
        ExceptionRequest excReq = mgr.createExceptionRequest(null, true, true);
        // suspend so we can step
        excReq.setSuspendPolicy(EventRequest.SUSPEND_ALL);
        excReq.enable();

        MethodEntryRequest methodEntryRequest = mgr.createMethodEntryRequest();
        prepareClassesFiltering(methodEntryRequest);
        methodEntryRequest.setSuspendPolicy(EventRequest.SUSPEND_NONE);
        methodEntryRequest.enable();

        MethodExitRequest methodExitRequest = mgr.createMethodExitRequest();
        prepareClassesFiltering(methodExitRequest);
        methodExitRequest.setSuspendPolicy(EventRequest.SUSPEND_NONE);
        methodExitRequest.enable();

        ThreadDeathRequest tdr = mgr.createThreadDeathRequest();
		for (IThread thread : threads)
		{
			JDIThread javaThread = (JDIThread) thread;
			ThreadReference threadReference = javaThread.getUnderlyingThread();
			tdr.addThreadFilter(threadReference);
		}
        tdr.setSuspendPolicy(EventRequest.SUSPEND_ALL);
        tdr.enable();        
        }
	
	@Override
	public void run() {
        EventQueue queue = vm.eventQueue();
        while (connected) {
            try {
                EventSet eventSet = queue.remove();
                EventIterator it = eventSet.eventIterator();
                while (it.hasNext()) {
                    handleEvent(it.nextEvent());
                }
                eventSet.resume();
            } catch (InterruptedException exc) {
                // Ignore
            } catch (VMDisconnectedException discExc) {
                handleDisconnectedException();
                break;
            }
        }
	}

    /**
     * Dispatch incoming events
     */
    private void handleEvent(Event event) {
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

    /***
     * A VMDisconnectedException has happened while dealing with
     * another event. We need to flush the event queue, dealing only
     * with exit events (VMDeath, VMDisconnect) so that we terminate
     * correctly.
     */
    synchronized void handleDisconnectedException() {
        EventQueue queue = vm.eventQueue();
        while (connected) {
            try {
                EventSet eventSet = queue.remove();
                EventIterator iter = eventSet.eventIterator();
                while (iter.hasNext()) {
                    Event event = iter.nextEvent();
                    if (event instanceof VMDeathEvent) {
                        vmDeathEvent((VMDeathEvent)event);
                    } else if (event instanceof VMDisconnectEvent) {
                        vmDisconnectEvent((VMDisconnectEvent)event);
                    }
                }
                eventSet.resume(); // Resume the VM
            } catch (InterruptedException exc) {
                // ignore
            }
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


    private void exceptionEvent(ExceptionEvent event) {
//        if (trace != null) {  // only want threads we care about
//            trace.exceptionEvent(event);      // Forward event
//        }
    }

    public void vmDeathEvent(VMDeathEvent event) {
        vmDied = true;
       // writer.println("-- The application exited --");
    }

    public void vmDisconnectEvent(VMDisconnectEvent event) {
        connected = false;
        if (!vmDied) {
          //  writer.println("-- The application has been disconnected --");
        }
    }
	
	private VirtualMachine vm;	
	private SelectionType selectionType;
	private Object[] selectedClasses;
	private IThread[] threads;
	
	private boolean connected;
	private boolean vmDied;
}
