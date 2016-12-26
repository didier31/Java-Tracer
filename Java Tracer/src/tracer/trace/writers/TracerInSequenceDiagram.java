package tracer.trace.writers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

import javax.naming.Reference;
import javax.sql.rowset.spi.SyncResolver;

import org.eclipse.tracecompass.tmf.ui.views.uml2sd.core.ExecutionOccurrence;
import org.eclipse.tracecompass.tmf.ui.views.uml2sd.core.Frame;
import org.eclipse.tracecompass.tmf.ui.views.uml2sd.core.Lifeline;
import org.eclipse.tracecompass.tmf.ui.views.uml2sd.core.SyncMessage;
import org.eclipse.tracecompass.tmf.ui.views.uml2sd.core.SyncMessageReturn;

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
	
	class CallStacks
	{
		Hashtable<ThreadReference, Stack<SyncMessage>> callStacks = new Hashtable<ThreadReference, Stack<SyncMessage>>();
		
		public void push(ThreadReference thread, SyncMessage message)
		{
			Stack<SyncMessage> stack = callStacks.get(thread);
			if (stack == null)
			{
				stack = new Stack<SyncMessage>();
				SyncMessage launchMessage = new SyncMessage();
				Lifeline launcher = frame.getLifeline(JVMLauncher);
				launchMessage.autoSetStartLifeline(launcher);
				launchMessage.autoSetEndLifeline(message.getStartLifeline());
				stack.push(launchMessage);
				callStacks.put(thread, stack);
			}
			stack.push(message);
		}
		
		public SyncMessage pop(ThreadReference thread)
		{
			Stack<SyncMessage> stack = callStacks.get(thread);
			return stack.pop();
		}
	};
	
	CallStacks callStacks = new CallStacks(); 

	private int JVMLauncher = -1;
	
	public TracerInSequenceDiagram(Frame frame) 
	{
		this.frame = frame;
		Lifeline launcher = new Lifeline();
		launcher.setName("<JVM's Launcher>");
		frame.addLifeLine(launcher);
		JVMLauncher = frame.lifeLinesCount() - 1;
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
		} catch (IncompatibleThreadStateException e) 
		{
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
			return JVMLauncher;
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
	
	public void onMethodEntryEvent(MethodEntryEvent event)
	{
		int callerIdx = getCallerLifeline(event);
		Lifeline caller = frame.getLifeline(callerIdx);
		int calleeIdx = getCalleeLifeline(event);
		Lifeline callee = frame.getLifeline(calleeIdx);
		
		SyncMessage call = new SyncMessage();
		
		call.autoSetStartLifeline(caller);
		call.autoSetEndLifeline(callee);
		MethodEntryEvent evt = (MethodEntryEvent) event;
		call.setName(printMethod(evt.method()));
		callStacks.push(event.thread(), call);
		frame.addMessage(call);
	}

	@Override
	public void onMethodExitEvent(MethodExitEvent event) 
	{
		SyncMessage call = callStacks.pop(event.thread());
		SyncMessageReturn returnMessage = new SyncMessageReturn();
		
		returnMessage.autoSetStartLifeline(call.getEndLifeline());
		returnMessage.autoSetEndLifeline(call.getStartLifeline());
		returnMessage.setName(event.method().returnTypeName());
		
		frame.addMessage(returnMessage);
		
        ExecutionOccurrence execution = new ExecutionOccurrence();
        execution.setStartOccurrence(call.getEventOccurrence());
        execution.setEndOccurrence(returnMessage.getEventOccurrence());
        call.getEndLifeline().addExecution(execution);
        execution.setName(event.method().name());
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
