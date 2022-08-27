package org.flashtool.gui.models;

import java.util.Vector;

import org.flashtool.gui.TARestore;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TableLine {

	private Vector<String> line = new Vector<String>();

	public TableLine() {
	}

	  public String getValueOf(int index) {
	    return line.get(index);
	  }
	  
	  public void setValueOf(int index, String value) {
		  line.set(index, value);
	  }
	  
	  public void add(String value) {
		  line.add(value);
	  }

	  public String toString() {
		  String result="";
		  for (int i=0;i<line.size();i++)
			  result+=line.get(i)+" ";
		  return result.trim();
	  }
}
