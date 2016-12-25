package tracer.trace.writers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

import javax.naming.Reference;

import org.eclipse.tracecompass.tmf.ui.views.uml2sd.core.Frame;
import org.eclipse.tracecompass.tmf.ui.views.uml2sd.core.Lifeline;
import org.eclipse.tracecompass.tmf.ui.views.uml2sd.core.SyncMessage;

import com.google.inject.spi.Message;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ThreadDeathEvent;

public class TracerInSequenceDiagram implements IWriter {

	private Frame frame;
	
	Hashtable<ReferenceType, Integer> lifelinesByType = new Hashtable<ReferenceType, Integer>();
	
	Hashtable<ThreadReference, Stack<SyncMessage>> callStacks = new Hashtable<ThreadReference, Stack<SyncMessage>>(); 

	private int JVMLauncher = -1;
	
	public TracerInSequenceDiagram(Frame frame) {
		this.frame = frame;
	}	
	
	protected ReferenceType getCaller(LocatableEvent event)
	{
		ReferenceType referenceType = null;
		try {
			if (event.thread().frames().size() > 1)
			{
				StackFrame callerFrame = event.thread().frame(1);
				referenceType = callerFrame.location().declaringType();
			}
			else
			{
				return null;
			}
		} catch (IncompatibleThreadStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			referenceType = null;
		}
		return referenceType;
	}
	
	protected int getCallerLifeline(LocatableEvent event)
	{
		ReferenceType callerReferenceType = getCaller(event);
		Integer idx = null;
		if (callerReferenceType != null)
		{
			idx = lifelinesByType.get(callerReferenceType);
			if (idx == null)
			{
				return createLifeline(callerReferenceType);
			}
			else
			{
				return idx;
			}
		}
		else
		{
			if (JVMLauncher  == -1)
			{
				Lifeline callee = new Lifeline();
				callee.setName("<JVM's Launcher>");
				frame.addLifeLine(callee);
				return frame.lifeLinesCount() - 1;
			}
			else
			{
				return JVMLauncher;
			}
		}
	}
	
	protected int getCalleeLifeline(LocatableEvent event)
	{
		ReferenceType calleeReferenceType = getCallee(event); 
		Integer idx = lifelinesByType.get(calleeReferenceType);
		if (idx == null)
		{
			return createLifeline(calleeReferenceType);
		}
		else
		{
			return idx;
		}
	}	
	
	protected ReferenceType getCallee(LocatableEvent event)
	{
		return event.location().declaringType();
	}
	
	protected int createLifeline(ReferenceType referenceType)
	{
		Lifeline callee = new Lifeline();
		String name = referenceType.name();
		int index = name.lastIndexOf('.');
		name = name.substring(index+1);
		callee.setName(name);
		frame.addLifeLine(callee);
		lifelinesByType.put(referenceType, frame.lifeLinesCount() - 1);
		return frame.lifeLinesCount() - 1;
	}
	
	protected void addMethodEventMessage(LocatableEvent event)
	{
		int callerIdx = getCallerLifeline(event);
		Lifeline caller = frame.getLifeline(callerIdx);
		int calleeIdx = getCalleeLifeline(event);
		Lifeline callee = frame.getLifeline(calleeIdx);
		
		SyncMessage call = new SyncMessage();
		
		int stackDeepness = -1;
		try {
			stackDeepness = event.thread().frames().size();
		} catch (IncompatibleThreadStateException e) {
		}
		
		if (event instanceof MethodEntryEvent)
		{
			call.autoSetStartLifeline(caller);
			call.autoSetEndLifeline(callee);
			MethodEntryEvent evt = (MethodEntryEvent) event;
			call.setName(stackDeepness + ": " + printMethod(evt.method()));
		}
		else
		{
			call.autoSetStartLifeline(callee);
			call.autoSetEndLifeline(caller);
			MethodExitEvent evt = (MethodExitEvent) event;
			call.setName(stackDeepness + ": return " + evt.method().returnTypeName());
		}
		frame.addMessage(call);
	}
	
	@Override
	public void onMethodEntryEvent(MethodEntryEvent event)
	{
		addMethodEventMessage(event);
	}

	@Override
	public void onMethodExitEvent(MethodExitEvent event) 
	{
		addMethodEventMessage(event);
	}

	@Override
	public void onThreadDeathEvent(ThreadDeathEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onExceptionEvent(ExceptionEvent event) {
		// TODO Auto-generated method stub

	}

	String printMethod(Method method)
	{
		List<LocalVariable> listParams = new ArrayList<LocalVariable>(0);
		try {
			listParams = method.arguments();
		} catch (AbsentInformationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		String call;
		call = method.declaringType().name() + "." + method.name() +"(";
		for (int i = 0; i < listParams.size(); i++)
		{
			LocalVariable param = listParams.get(i);
			String typeName = param.typeName();
			int j = typeName.lastIndexOf('.');
			typeName = typeName.substring(j+1);
			call += typeName + " " + param.name();
			if (i < listParams.size() - 1)
			{
				call += ", ";
			}
		}
		call += ");";
		return call;
	}
}
