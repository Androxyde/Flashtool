package com.iagucool.xperifirm;

public class FirmwaresList {

	Firmware _f = null;
	
	public void add(Firmware f) {
		if (_f==null) _f=f;
		else {
			if (!_f.getRelease().equals(f.getRelease())) {
				if (f.getId()>_f.getId()) _f=f;
			}
			else {
				if (f.getRevision().compareTo(_f.getRevision())>0) _f=f;
			}
		}
	}
	
	public Firmware getLatest() {
		return _f;
	}
}
