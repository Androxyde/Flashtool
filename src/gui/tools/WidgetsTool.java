package gui.tools;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.system.OS;

public class WidgetsTool {

	public static void setSize(Composite c) {
		Control[] ctl = c.getChildren();
		for (int i=0;i<ctl.length;i++) {
			if (ctl[i] instanceof Composite) setSize((Composite)ctl[i]);
			FontData[] fD = ctl[i].getFont().getFontData();
			if (OS.getName().equals("mac"))
				fD[0].setHeight(11);
			if (OS.getName().equals("linux"))
				fD[0].setHeight(9);
			ctl[i].setFont( new Font(Display.getCurrent(),fD[0]));
		}
	}

}
