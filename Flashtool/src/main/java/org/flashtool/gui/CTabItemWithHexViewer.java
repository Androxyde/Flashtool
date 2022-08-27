package org.flashtool.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.flashtool.flashsystem.Bundle;
import org.flashtool.flashsystem.S1Command;
import org.flashtool.windowbuilder.swt.SWTResourceManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CTabItemWithHexViewer {

	
	private CTabFolder parent;
	private StyledText counter;
	private StyledText hexContent;
	private StyledText binContent;
	private String name;
	private int style;
	
	public CTabItemWithHexViewer(CTabFolder parent, String name, int style) {
		this.parent = parent;
		this.name = name;
		this.style = style;
		createEditor();
	}
	
	public void loadContent(byte[] buffer) {
		hexContent.setText("");
		
		
		int ctr = 0;
		StringBuffer hexContentStringBuffer = new StringBuffer();
		StringBuffer counterStringBuffer = new StringBuffer("000000");
		StringBuffer binContentStringBuffer = new StringBuffer();
		for(byte c: buffer){
			ctr++;
			String hex = String.format("%02X", c);
            
			char ch = (char)c;
            if (ch < 32 || ch > 126) {
            	ch = '.';
            }
            	
			if(ctr % 16 == 0){
				hexContentStringBuffer.append(hex).append("\n");
				binContentStringBuffer.append(ch).append("\n");
				counterStringBuffer.append("\n").append(String.format("%06d", ctr));
			} else if (ctr % 8 == 0){
				hexContentStringBuffer.append(hex).append("  ");
				binContentStringBuffer.append(ch);
			} else {
				hexContentStringBuffer.append(hex).append(" ");
				binContentStringBuffer.append(ch);
			}
		}

		hexContent.setText(hexContentStringBuffer.toString());
		binContent.setText(binContentStringBuffer.toString());
		counter.setText(counterStringBuffer.toString());

	}
	
	private void createEditor(){		
		CTabItem tabItem = new CTabItem(parent, style);
		tabItem.setText(name);
		
		Composite composite = new Composite(parent, SWT.NONE);
		tabItem.setControl(composite);
		FormLayout fl_composite = new FormLayout();
		fl_composite.marginWidth = 0;
		fl_composite.marginHeight = 0;
		composite.setLayout(fl_composite);
		
		counter = new StyledText(composite, SWT.BORDER | SWT.READ_ONLY);
		//counter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		FormData fd_counter = new FormData();
		fd_counter.top=new FormAttachment(0, 0);
		fd_counter.bottom=new FormAttachment(100, 0);
		fd_counter.left=new FormAttachment(0, 0);
		counter.setLayoutData(fd_counter);
		counter.setFont(SWTResourceManager.getFont("Courier New", 10, SWT.NORMAL));
		addListeners(counter);

		hexContent = new StyledText(composite, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL);
		FormData fd_hexContent = new FormData();
		fd_hexContent.top=new FormAttachment(0, 0);
		fd_hexContent.bottom=new FormAttachment(100, 0);
		fd_hexContent.left=new FormAttachment(counter, 0);
		fd_hexContent.width=400;
		hexContent.setLayoutData(fd_hexContent);
		hexContent.setFont(SWTResourceManager.getFont("Courier New", 10, SWT.NORMAL));
		addListeners(hexContent);

		binContent = new StyledText(composite, SWT.BORDER | SWT.READ_ONLY );
		FormData fd_binContent = new FormData();
		fd_binContent.top=new FormAttachment(0, 0);
		fd_binContent.bottom=new FormAttachment(100, 0);
		fd_binContent.left=new FormAttachment(hexContent, 0);
		fd_binContent.right=new FormAttachment(100, 0);
		binContent.setLayoutData(fd_binContent);
		//binContent.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true, 1, 1));
		binContent.setFont(SWTResourceManager.getFont("Courier New", 10, SWT.NORMAL));
		addListeners(binContent);

		parent.setSelection(tabItem);

	}
	
	private void addListeners(final StyledText txt){
		txt.addMouseWheelListener(new MouseWheelListener() {
			public void mouseScrolled(MouseEvent arg0) {
				binContent.setTopIndex(txt.getTopIndex());
				hexContent.setTopIndex(txt.getTopIndex());
				counter.setTopIndex(txt.getTopIndex());
			}
		});
		txt.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
				binContent.setTopIndex(txt.getTopIndex());
				hexContent.setTopIndex(txt.getTopIndex());
				counter.setTopIndex(txt.getTopIndex());
			}
			public void widgetSelected(SelectionEvent arg0) {
				binContent.setTopIndex(txt.getTopIndex());
				hexContent.setTopIndex(txt.getTopIndex());
				counter.setTopIndex(txt.getTopIndex());
			}			
		});
		txt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				binContent.setTopIndex(txt.getTopIndex());
				hexContent.setTopIndex(txt.getTopIndex());
				counter.setTopIndex(txt.getTopIndex());
			}
		});
		txt.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				binContent.setTopIndex(txt.getTopIndex());
				hexContent.setTopIndex(txt.getTopIndex());
				counter.setTopIndex(txt.getTopIndex());
			}
		});
		
		txt.addCaretListener(new CaretListener() {
			public void caretMoved(CaretEvent arg0) {
				binContent.setTopIndex(txt.getTopIndex());
				hexContent.setTopIndex(txt.getTopIndex());
				counter.setTopIndex(txt.getTopIndex());
			}
		});
		
		txt.addDragDetectListener(new DragDetectListener() {
			public void dragDetected(DragDetectEvent arg0) {
				binContent.setTopIndex(txt.getTopIndex());
				hexContent.setTopIndex(txt.getTopIndex());
				counter.setTopIndex(txt.getTopIndex());
			}
		});
		
		ScrollBar vbar = txt.getVerticalBar();
		if(vbar != null){
			vbar.addListener(SWT.Selection, new Listener(){
				public void handleEvent(Event arg0) {
					binContent.setTopIndex(txt.getTopIndex());
					hexContent.setTopIndex(txt.getTopIndex());
					counter.setTopIndex(txt.getTopIndex());
				}
				
			});
		}
		
	}
}