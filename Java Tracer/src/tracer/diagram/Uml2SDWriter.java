package tracer.diagram;

import java.util.Vector;

import org.eclipse.tracecompass.tmf.ui.views.uml2sd.SDView;
import org.eclipse.tracecompass.tmf.ui.views.uml2sd.load.IUml2SDLoader;

public class Uml2SDWriter implements IUml2SDLoader {
	
	public static Vector<Uml2SDWriter> instances = new Vector<Uml2SDWriter>(10);
	
	public Uml2SDWriter()
	{
		instances.add(this);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTitleString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setViewer(SDView arg0) {
		// TODO Auto-generated method stub

	}

}
