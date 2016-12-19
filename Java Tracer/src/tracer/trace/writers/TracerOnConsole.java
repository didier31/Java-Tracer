package tracer.trace.writers;

import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ThreadDeathEvent;

public class TracerOnConsole implements IWriter {

	@Override
	public void onMethodEntryEvent(MethodEntryEvent event) {
		System.out.println("Entering " + event.method().name());
	}

	@Override
	public void onMethodExitEvent(MethodExitEvent event) {
		System.out.println("Exiting from " + event.method().name());
		
	}

	@Override
	public void onThreadDeathEvent(ThreadDeathEvent event) {
		System.out.println("Death of " + event.thread().name());
	}

	@Override
	public void onExceptionEvent(ExceptionEvent event) {
		System.out.println("Raised " + event.exception().type().name());
	}

}
