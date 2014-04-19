package org.system;

import java.net.ProxySelector;

import com.btr.proxy.search.ProxySearch;
import com.btr.proxy.search.ProxySearch.Strategy;
import com.btr.proxy.util.PlatformUtil;
import com.btr.proxy.util.PlatformUtil.Platform;

public class Proxy {

	public static ProxySelector getProxy() {
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

		if (proxySearch.getProxySelector()!=null)
			return proxySearch.getProxySelector();
		else
			return ProxySelector.getDefault();

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
