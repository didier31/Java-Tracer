package tracer.handlers;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;

import tracer.creation.TracingWizard;
import tracer.creation.wizard.pages.datamodels.SelectionType;
import tracer.trace.Listener;
import tracer.trace.writers.TracerOnConsole;

import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.tracecompass.tmf.ui.views.uml2sd.SDView;
import org.eclipse.tracecompass.tmf.ui.views.uml2sd.load.IUml2SDLoader;
import org.eclipse.tracecompass.tmf.ui.views.uml2sd.load.LoadersManager;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class Start extends AbstractHandler {

	private static final String TRACER_SEQUENCE_DIAGRAM_VIEW_ID = "tracer.sequenceDiagramView";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		TreeSelection selections = (TreeSelection) HandlerUtil.getActiveWorkbenchWindow(event)
                .getActivePage().getSelection();	
		
		Object selection = selections.getFirstElement();
		
		VirtualMachine vm = null;
		IThread[] threads = null;
		
		if (selection instanceof JDIStackFrame)
		{
			selection = ((JDIStackFrame) selection).getThread();
		}
		if (selection instanceof JDIThread)
		{
			threads = new JDIThread[1];
			threads[0] = ((JDIThread) selection);
			selection = threads[0].getDebugTarget();
		}
		if (selection instanceof JDIDebugTarget)
		{
			JDIDebugTarget dbgTarget = ((JDIDebugTarget) selection);
			vm = dbgTarget.getVM();
			if (threads == null)
			{
				threads = dbgTarget.getThreads();
			}
		}
		// local, selection may be instance of Launch too but we can't how to deal with.
		// In this case, this plugin does not go further.
		
		if (vm != null)
		{		
			List<ReferenceType> loadedClasses = vm.allClasses().stream()
					.filter(c -> c instanceof com.sun.jdi.ClassType).collect(Collectors.toList());
			tracingWizard = new TracingWizard(loadedClasses);
			WizardDialog dialog = new WizardDialog(window.getShell(), tracingWizard);
			dialog.open();
			
			SDView sdView = (SDView) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(Start.TRACER_SEQUENCE_DIAGRAM_VIEW_ID);
		
			if (sdView == null)
				try {
					sdView = (SDView) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().showView(Start.TRACER_SEQUENCE_DIAGRAM_VIEW_ID);
					} catch (PartInitException e) {
						e.printStackTrace();
						}
			if (sdView != null)
			{
				SelectionType selectionType = tracingWizard.getSelectionType();
				Object[] selectedClasses = tracingWizard.getCheckedClasses();
				IUml2SDLoader uml2SDLoader = LoadersManager.getInstance().getCurrentLoader(Start.TRACER_SEQUENCE_DIAGRAM_VIEW_ID);
				Thread jdiEventRecorder = new Thread(new Listener(vm, selectionType, selectedClasses, threads, new TracerOnConsole()));
				}
			}							
		return null;
		}
	
	private TracingWizard tracingWizard;
}
