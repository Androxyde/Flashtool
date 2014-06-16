package com.btr.proxy.search;

import java.awt.GraphicsEnvironment;
import java.net.ProxySelector;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.logger.MyLogger;

import com.btr.proxy.search.browser.firefox.FirefoxProxySearchStrategy;
import com.btr.proxy.search.browser.ie.IEProxySearchStrategy;
import com.btr.proxy.search.desktop.DesktopProxySearchStrategy;
import com.btr.proxy.search.desktop.gnome.GnomeProxySearchStrategy;
import com.btr.proxy.search.desktop.kde.KdeProxySearchStrategy;
import com.btr.proxy.search.desktop.win.WinProxySearchStrategy;
import com.btr.proxy.search.env.EnvProxySearchStrategy;
import com.btr.proxy.search.java.JavaProxySearchStrategy;
import com.btr.proxy.selector.misc.BufferedProxySelector;
import com.btr.proxy.selector.misc.ProxyListFallbackSelector;
import com.btr.proxy.selector.pac.PacProxySelector;
import com.btr.proxy.util.PlatformUtil;
import com.btr.proxy.util.ProxyException;

/*****************************************************************************
 * Main class to setup and initialize the proxy detection system.<br/>
 * This class can be used to select a proxy discovery strategy.<br/>
 * Implements the "Builder" pattern.<br/> 
 * Use <code>addStrategy</code> to add one or more search strategies.<br/> 
 * If you are done call the <code>getProxySelector</code> method. <br/>
 * Then the strategies are asked one after the other for a ProxySelector until
 * an valid selector is found. <br/>
 * <p> 
 * Invoke the static <code>getDefaultProxySearch</code> method to use a default search strategy.
 * </p>
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

public class ProxySearch implements ProxySearchStrategy {
	
	private static final int DEFAULT_PAC_CACHE_SIZE = 20;

	private static final long DEFAULT_PAC_CACHE_TTL = 1000*60*10; // 10 Minutes
	
	private List<ProxySearchStrategy> strategies;
	private int pacCacheSize;
	private long pacCacheTTL;
	private static Logger logger = Logger.getLogger(ProxySearch.class);
	
	/*****************************************************************************
	 * Types of proxy detection supported by the builder.
	 ****************************************************************************/
	
	public enum Strategy {
		/// Use the platform settings.
		OS_DEFAULT,  
		/// Use the settings of the platforms default browser.
		BROWSER, 
		/// Use Firefox settings
		FIREFOX, 
		/// Use InternetExplorer settings
		IE, 
		/// Use environment variables for proxy settings.
		ENV_VAR, 
		/// Use windows default proxy settings.
		WIN, 
		/// Use KDE desktop default proxy settings.
		KDE,
		/// Use KDE desktop default proxy settings.
		GNOME, 
		/// Use Java Networking system properties
		JAVA
	}
	
	/*************************************************************************
	 * Constructor
	 ************************************************************************/
	
	public ProxySearch() {
		super();
		this.strategies = new ArrayList<ProxySearchStrategy>();
		this.pacCacheSize = DEFAULT_PAC_CACHE_SIZE;
		this.pacCacheTTL = DEFAULT_PAC_CACHE_TTL;
	}

	/*************************************************************************
	 * Sets up a ProxySearch that uses a default search strategy suitable for 
	 * every platform.
	 * @return a ProxySearch initialized with default settings.
	 ************************************************************************/
	
	public static ProxySearch getDefaultProxySearch() {
		ProxySearch s = new ProxySearch();

		// Test if we are a server or a client.
		boolean headless = GraphicsEnvironment.isHeadless();
		
		if (headless) { 
			s.addStrategy(Strategy.JAVA);
			s.addStrategy(Strategy.OS_DEFAULT);
			s.addStrategy(Strategy.ENV_VAR);
		} else {
			s.addStrategy(Strategy.JAVA);
			s.addStrategy(Strategy.BROWSER);
			s.addStrategy(Strategy.OS_DEFAULT);
			s.addStrategy(Strategy.ENV_VAR);
		}

		return s;
	}
	
	/*************************************************************************
	 * Adds an search strategy to the list of proxy searches strategies.
	 * @param strategy the search strategy to add.
	 ************************************************************************/
	
	public void addStrategy(Strategy strategy) {
		switch (strategy) {
		case OS_DEFAULT:
			this.strategies.add(new DesktopProxySearchStrategy());	
			break;
		case BROWSER:
			this.strategies.add(getDefaultBrowserStrategy());	
			break;
		case FIREFOX:
			this.strategies.add(new FirefoxProxySearchStrategy());	
			break;
		case IE:
			this.strategies.add(new IEProxySearchStrategy());	
			break;
		case ENV_VAR:
			this.strategies.add(new EnvProxySearchStrategy());	
			break;
		case WIN:
			this.strategies.add(new WinProxySearchStrategy());	
			break;
		case KDE:
			this.strategies.add(new KdeProxySearchStrategy());	
			break;
		case GNOME:
			this.strategies.add(new GnomeProxySearchStrategy());	
			break;
		case JAVA:
			this.strategies.add(new JavaProxySearchStrategy());	
			break;
		default:
			throw new IllegalArgumentException("Unknown strategy code!");
		}
	}
	
	/*************************************************************************
	 * Sets the cache size of the PAC proxy selector cache.
	 * This defines the number of URLs that are cached together with the PAC
	 * script result. This improves performance because for URLs that are
	 * in the cache the script is not executed again.
	 * You have to set this before you add any strategies that may create a 
	 * PAC script proxy selector.
	 * @param size of the cache. Set it to 0 to disable caching.
	 * @param ttl is the time to live of the cache entries as amount of milliseconds.
	 ************************************************************************/
	
	public void setPacCacheSettings(int size, long ttl) {
		this.pacCacheSize = size;
		this.pacCacheTTL = ttl;
	}
	
	/*************************************************************************
	 * Gets the search strategy for the platforms default browser.
	 * @return a ProxySearchStrategy, null if no supported browser was found.
	 ************************************************************************/
	
	private ProxySearchStrategy getDefaultBrowserStrategy() {
		switch (PlatformUtil.getDefaultBrowser()) {
		 case IE:
			return new IEProxySearchStrategy();
		 case FIREFOX:
			return new FirefoxProxySearchStrategy();
		}
		return null;
	}

	/*************************************************************************
	 * Gets the proxy selector that will use the configured search order.
	 * @return a ProxySelector, null if none was found for the current 
	 * 		builder configuration.
	 ************************************************************************/
	
	public ProxySelector getProxySelector() {
		logger.info("Executing search strategies to find proxy selector");
		for (ProxySearchStrategy strat : this.strategies) {
			try {
				ProxySelector selector = strat.getProxySelector();
				if (selector != null) {
					selector = installBufferingAndFallbackBehaviour(selector);
					logger.info("Proxy found for "+strat.getName());
					return selector;
				}
				else
					logger.info("No proxy found for "+strat.getName()+". Trying next one");
			} catch (ProxyException e) {
				logger.info("Strategy "+strat.getName()+" failed trying next one : "+e.getMessage());
				// Ignore and try next strategy.
			}
		}
		
		return null;
	}

	/*************************************************************************
	 * If it is PAC and we have caching enabled set it here.
	 * @param selector
	 * @return
	 ************************************************************************/
	
	private ProxySelector installBufferingAndFallbackBehaviour(ProxySelector selector) {
		if (selector instanceof PacProxySelector) {
			if (this.pacCacheSize > 0) {
				selector = new BufferedProxySelector(this.pacCacheSize, this.pacCacheTTL, selector);
			}
			selector = new ProxyListFallbackSelector(selector);
		}
		return selector;
	}

	public String getName() {
		return "default";
	}
	
	/*************************************************************************
	 * toString
	 * @see java.lang.Object#toString()
	 ************************************************************************/
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Proxy search: ");
		for (ProxySearchStrategy strat : this.strategies) {
			sb.append(strat.getName());
			sb.append(" ");
		}
		return sb.toString();
	}

}
