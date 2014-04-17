package gui.tools;

import gui.tools.WidgetTask.Result;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class MsgBox {

	static Shell current = null;
	
	public static void error(String message) {
		Shell curshell = null;
		try {
			curshell = Display.getCurrent().getActiveShell();
			if (curshell == null) curshell = current;
		} catch (Exception e) {
			curshell = current;
		}
		MessageBox mb = new MessageBox(curshell,SWT.ICON_ERROR|SWT.OK);
		mb.setText("Error");
		mb.setMessage(message);
		mb.open();
	}
	
	public static int question(final String question) {
		Shell curshell = null;
		try {
			curshell = Display.getCurrent().getActiveShell();
			if (curshell == null) curshell = current;
		} catch (Exception e) {
			curshell = current;
		}
		final Result res = new Result();
		Display.getDefault().syncExec(
				new Runnable() {
					public void run() {
						MessageBox mb = new MessageBox(current,SWT.ICON_QUESTION|SWT.YES|SWT.NO);
						mb.setText("Question");
						mb.setMessage(question);
						res.setResult(mb.open());		
					}
				}
		);
		return (Integer)res.getResult();
	}
	
	public static void setCurrentShell(Shell s) {
		current = s;
	}

}
