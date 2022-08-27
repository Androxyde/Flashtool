package org.flashtool.gui.models;

import org.eclipse.jface.viewers.LabelProvider;
import org.flashtool.gui.TARestore;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyTreeLabelProvider extends LabelProvider {

	   @Override
	   public String getText(Object element) {
	     if (element instanceof TreeDevice) {
	       return ((TreeDevice) element).getDeviceName();
	     } else
	     if (element instanceof TreeDeviceVariant) {
	       return ((TreeDeviceVariant) element).getVariant();
	     } else
	     if (element instanceof TreeDeviceCustomization) {
	       return ((TreeDeviceCustomization) element).getCustomization();
	     }
	     if (element instanceof TreeDeviceCustomizationRelease) {
		       return ((TreeDeviceCustomizationRelease) element).getRelease();
		     }
	     return null;
	   }
}