package org.system;

import java.util.EventListener;

public interface StatusListener extends EventListener {
    public void statusChanged(StatusEvent e);
}