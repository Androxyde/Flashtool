package org.flashtool.gui.models;

import java.util.Vector;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.flashtool.gui.TARestore;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VectorContentProvider implements IStructuredContentProvider {

    public Object[] getElements(Object inputElement) {
        Vector v = (Vector)inputElement;
        return v.toArray();
      }
      
      public void dispose() {
      }
 
      public void inputChanged(
        Viewer viewer,
        Object oldInput,
        Object newInput) {
      }

}