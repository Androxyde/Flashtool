package gui.tools;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.system.OS;

public class WidgetsTool {

	public static void setSize(Control c) {
		if (c instanceof Composite) {
			Control[] ctl = ((Composite)c).getChildren();
			for (int i=0;i<ctl.length;i++) {
				if (ctl[i] instanceof Composite) setSize((Composite)ctl[i]);
				setNewSize(ctl[i]);
			}
		}
		setNewSize(c);
	}

	private static void setNewSize(Control c) {
		FontData[] fD = c.getFont().getFontData();
		if (OS.getName().equals("mac"))
			fD[0].setHeight(11);
		if (OS.getName().equals("linux"))
			fD[0].setHeight(9);
		c.setFont( new Font(Display.getCurrent(),fD[0]));		
	}
}
