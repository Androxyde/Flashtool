package gui.models;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class MyTreeContentProvider implements ITreeContentProvider {

    private static final Object[] EMPTY_ARRAY = new Object[0];

    //Called just for the first-level objects.
    //Here we provide a list of objects
    @Override
    public Object[] getElements(Object inputElement) {
      if (inputElement instanceof TreeDevices)
        return ((TreeDevices) inputElement).getContent().toArray();
      else
        return EMPTY_ARRAY;
    }

    //Queried to know if the current node has children
    @Override
    public boolean hasChildren(Object element) {
      if (element instanceof TreeDevices || element instanceof TreeDevice || element instanceof TreeDeviceVariant || element instanceof TreeDeviceCustomization) {
        return true;
      }
      return false;
    }

    //Queried to load the children of a given node
    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof TreeDevices) {
      	  TreeDevices devices = (TreeDevices) parentElement;
      	  return devices.getContent().toArray();
        }
    	if (parentElement instanceof TreeDevice) {
    	  TreeDevice device = (TreeDevice) parentElement;
    	  return device.getVariants().toArray();
    	}
    	if (parentElement instanceof TreeDeviceVariant) {
      	  TreeDeviceVariant variant = (TreeDeviceVariant) parentElement;
      	  return variant.getCustomizations().toArray();
      	}
    	if (parentElement instanceof TreeDeviceCustomization) {
        	  TreeDeviceCustomization cust = (TreeDeviceCustomization) parentElement;
        	  return cust.getReleases().toArray();
        }
    	return EMPTY_ARRAY;
    }

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    @Override
    public Object getParent(Object element) {
      return null;
    }

  }