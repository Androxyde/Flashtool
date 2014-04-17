package gui.models;

import java.util.Vector;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

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