package tracer.trace.writers;

import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ThreadDeathEvent;

public interface IWriter {
	public void onMethodEntryEvent(MethodEntryEvent event);
	public void onMethodExitEvent(MethodExitEvent event);
	public void onThreadDeathEvent(ThreadDeathEvent event);
	public void onExceptionEvent(ExceptionEvent event);
}
