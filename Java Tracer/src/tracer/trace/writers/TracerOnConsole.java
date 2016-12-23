package tracer.trace.writers;

import java.util.ArrayList;
import java.util.List;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Method;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ThreadDeathEvent;

public class TracerOnConsole implements IWriter {

	@Override
	public void onMethodEntryEvent(MethodEntryEvent event) {
		Method method = event.method();
		System.out.print("---> ");
		printMethod(method);
	}

	void printMethod(Method method)
	{
		List<LocalVariable> listParams = new ArrayList<LocalVariable>(0);
		/*try {
			listParams = method.arguments();
		} catch (AbsentInformationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		System.out.print(method.declaringType().name() + "." + method.name() +"(");
		for (int i = 0; i < listParams.size(); i++)
		{
			System.out.print(listParams.get(i));
			if (i < listParams.size() - 1)
			{
				System.out.print(", ");
			}
		}
		System.out.println(");");
	}
	
	@Override
	public void onMethodExitEvent(MethodExitEvent event) {
		Method method = event.method();
		System.out.print("<--- ");
		printMethod(method);		
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
