package tracer.trace.writers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

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
	
	public TracerInSequenceDiagram(Frame frame) {
		this.frame = frame; 
	}	
	
	protected ObjectReference getCaller(LocatableEvent event)
	{
		try {
			StackFrame callerFrame = event.thread().frame(1);
			return callerFrame.thisObject();
		} catch (IncompatibleThreadStateException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected int getCallerLifeline(LocatableEvent event)
	{
		ReferenceType callerReferenceType = getCaller(event).referenceType(); 
		Integer idx = lifelinesByType.get(callerReferenceType);
		if (idx == null)
		{
			return createLifeline(callerReferenceType);
		}
		else
		{
			return idx;
		}
	}
	
/*	protected int getThreadLifeline(ThreadReference thread)
	{
		ReferenceType callerReferenceType = getCaller(event).referenceType(); 
		Integer idx = lifelinesByType.get(callerReferenceType);
		if (idx == null)
		{
			return createLifeline(callerReferenceType);
		}
		else
		{
			return idx;
		}
	}*/

	protected int getCalleeLifeline(LocatableEvent event)
	{
		ReferenceType calleeReferenceType = getCallee(event).referenceType(); 
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
	
	protected ObjectReference getCallee(LocatableEvent event)
	{
		try {
			return event.thread().frame(0).thisObject();
		} catch (IncompatibleThreadStateException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected int createLifeline(ReferenceType referenceType)
	{
		Lifeline callee = new Lifeline();
		String name = referenceType.name();
		int index = name.lastIndexOf('.');
		name = name.substring(index);
		callee.setName(name);
		frame.addLifeLine(callee);
		lifelinesByType.put(referenceType, frame.lifeLinesCount() - 1);
		return frame.lifeLinesCount() - 1;
	}
	
	@Override
	public void onMethodEntryEvent(MethodEntryEvent event)
	{
	ObjectReference callerReference = getCaller(event);
	if (callerReference == null)
		return;
	int callerIdx = getCallerLifeline(event);
	Lifeline caller = frame.getLifeline(callerIdx);
	int calleeIdx = getCalleeLifeline(event);
	Lifeline callee = frame.getLifeline(calleeIdx);
	
	SyncMessage call = new SyncMessage();
	call.autoSetStartLifeline(caller);
	call.autoSetEndLifeline(callee);
	call.setName(printMethod(event.method()));
	frame.addMessage(call);
	}

	@Override
	public void onMethodExitEvent(MethodExitEvent event) {
		// TODO Auto-generated method stub

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
