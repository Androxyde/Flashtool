package org.system;

import java.net.ProxySelector;

import com.btr.proxy.search.ProxySearch;
import com.btr.proxy.search.ProxySearch.Strategy;
import com.btr.proxy.util.PlatformUtil;
import com.btr.proxy.util.PlatformUtil.Platform;

public class Proxy {

	private static ProxySelector dps = null;
	
	public static void setProxy() {
		if (dps==null) dps=ProxySelector.getDefault();
		ProxySearch proxySearch = new ProxySearch();
        
		if (PlatformUtil.getCurrentPlattform() == Platform.WIN) {
		  proxySearch.addStrategy(Strategy.IE);
		  proxySearch.addStrategy(Strategy.FIREFOX);
		  proxySearch.addStrategy(Strategy.JAVA);
		} else 
		if (PlatformUtil.getCurrentPlattform() == Platform.LINUX) {
		  proxySearch.addStrategy(Strategy.GNOME);
		  proxySearch.addStrategy(Strategy.KDE);
		  proxySearch.addStrategy(Strategy.FIREFOX);
		} else {
		  proxySearch.addStrategy(Strategy.OS_DEFAULT);
		}
		ProxySelector ps = proxySearch.getProxySelector();
		if (ps!=null)
			ProxySelector.setDefault(ps);
	}
	
	public static boolean canResolve(String uri) {
		DNSResolver tr = new DNSResolver("github.com");
		try {
			tr.start();
			tr.join(2000);
			tr.interrupt();
			return tr.get()!=null;
		} catch (InterruptedException e) {
			return tr.get()!=null;
		}
	}
}
