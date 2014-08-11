package org.logger;

import gui.MainSWT;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;

public class LogProgress {

	static ProgressBar _bar = null;
	static long maxstepsconsole = 0;
	static long currentstepconsole = 0;
	

	public static void registerProgressBar(ProgressBar bar) {
		_bar = bar;
	}

	public static ProgressBar getProgressBar() {
		return _bar;
	}
	
	public static void initProgress(final long max) {
		LogProgress.initProgress((int) max);
	}

	public static void initProgress(final int max) {
					if (MainSWT.guimode) {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								_bar.setMinimum(0);
								_bar.setMaximum((int)max);
								_bar.setSelection(0);
							}
						});
					}
					else {
						maxstepsconsole=max;
						currentstepconsole=0;
					}
	}

	public static void updateProgress() {
		if (MainSWT.guimode) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					_bar.setSelection(_bar.getSelection()+1);
				}
			});
		}
		else {
			currentstepconsole++;
			double result = (double)currentstepconsole/(double)maxstepsconsole*100.0;
			LogProgress.printProgBar((int)result);
		}
		MyLogger.lastaction="progress";
	}

	public static void updateProgressValue(int value) {
		if (MainSWT.guimode) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					_bar.setSelection(value);
				}
			});
		}
		else {
			currentstepconsole=value;
			double result = (double)currentstepconsole/(double)maxstepsconsole*100.0;
			LogProgress.printProgBar((int)result);
		}
		MyLogger.lastaction="progress";
	}

	public static void printProgBar(long percent){
		if (percent <=100) {
		    StringBuilder bar = new StringBuilder("[");
	
		    for(int i = 0; i < 50; i++){
		        if( i < (percent/2)){
		            bar.append("=");
		        }else if( i == (percent/2)){
		            bar.append(">");
		        }else{
		            bar.append(" ");
		        }
		    }
	
		    bar.append("]   " + percent + "%     ");
		    System.out.print("\r" + bar.toString());
		}
	}

}
