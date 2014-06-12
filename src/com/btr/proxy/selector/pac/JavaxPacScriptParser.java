package com.btr.proxy.selector.pac;

import java.lang.reflect.Method;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;
import org.logger.MyLogger;

/*****************************************************************************
 * PAC parser using the Rhino JavaScript engine bundled with Java 1.6<br/>
 * If you need PAC support with Java 1.5 then you should have a look at 
 * RhinoPacScriptParser.
 *  
 * More information about PAC can be found there:<br/>
 * <a href="http://en.wikipedia.org/wiki/Proxy_auto-config">Proxy_auto-config</a><br/>
 * <a href="http://homepages.tesco.net/~J.deBoynePollard/FGA/web-browser-auto-proxy-configuration.html">web-browser-auto-proxy-configuration</a>
 * </p>
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/
public class JavaxPacScriptParser implements PacScriptParser {
	static final String SCRIPT_METHODS_OBJECT = "__pacutil";

	private final PacScriptSource source;
	private final ScriptEngine engine;
	private static Logger logger = Logger.getLogger(JavaxPacScriptParser.class);
	/*************************************************************************
	 * Constructor
	 * 
	 * @param source
	 *            the source for the PAC script.
	 * @throws ProxyEvaluationException
	 *             on error.
	 ************************************************************************/
	public JavaxPacScriptParser(PacScriptSource source)
			throws ProxyEvaluationException {
		this.source = source;
		this.engine = setupEngine();
	}

	/*************************************************************************
	 * Initializes the JavaScript engine and adds aliases for the functions
	 * defined in ScriptMethods.
	 * 
	 * @throws ProxyEvaluationException
	 *             on error.
	 ************************************************************************/
	private ScriptEngine setupEngine() throws ProxyEvaluationException {
		ScriptEngineManager mng = new ScriptEngineManager();
		ScriptEngine engine = mng.getEngineByMimeType("text/javascript");
		engine.put(SCRIPT_METHODS_OBJECT, new PacScriptMethods());

		Class<?> scriptMethodsClazz = ScriptMethods.class;
		Method[] scriptMethods = scriptMethodsClazz.getMethods();

		for (Method method : scriptMethods) {
			String name = method.getName();
			int args = method.getParameterTypes().length;
			StringBuilder toEval = new StringBuilder(name).append(" = function(");
			for (int i = 0; i < args; i++) {
				if (i > 0) {
					toEval.append(",");
				}
				toEval.append("arg").append(i);
			}
			toEval.append(") {return ");
			
			String functionCall = buildFunctionCallCode(name, args);
			
			// If return type is java.lang.String convert it to a JS string
			if (String.class.isAssignableFrom(method.getReturnType())) {
				functionCall = "String("+functionCall+")";
			}
			toEval.append(functionCall).append("; }");
			try {
				engine.eval(toEval.toString());
			} catch (ScriptException e) {
				logger.error("JS evaluation error when creating alias for " + name + " : "+e.getMessage());
				throw new ProxyEvaluationException(
						"Error setting up script engine", e);
			}
		}

		return engine;
	}

	/*************************************************************************
	 * Builds a JavaScript code snippet to call a function that we bind.
	 * @param functionName of the bound function
	 * @param args of the bound function
	 * @return the JS code to invoke the method.
	 ************************************************************************/
	
	private String buildFunctionCallCode(String functionName, int args) {
		StringBuilder functionCall = new StringBuilder();
		functionCall.append(SCRIPT_METHODS_OBJECT)
			  .append(".").append(functionName).append("(");
		for (int i = 0; i < args; i++) {
			if (i > 0) {
				functionCall.append(",");
			}
			functionCall.append("arg").append(i);
		}
		functionCall.append(")");
		return functionCall.toString();
	}

	/***************************************************************************
	 * Gets the source of the PAC script used by this parser.
	 * 
	 * @return a PacScriptSource.
	 **************************************************************************/
	public PacScriptSource getScriptSource() {
		return this.source;
	}

	/*************************************************************************
	 * Evaluates the given URL and host against the PAC script.
	 * 
	 * @param url
	 *            the URL to evaluate.
	 * @param host
	 *            the host name part of the URL.
	 * @return the script result.
	 * @throws ProxyEvaluationException
	 *             on execution error.
	 ************************************************************************/
	public String evaluate(String url, String host)
			throws ProxyEvaluationException {
		try {
			StringBuilder script = new StringBuilder(
					this.source.getScriptContent());
			String evalMethod = " ;FindProxyForURL (\"" + url + "\",\"" + host + "\")";
			script.append(evalMethod);
			Object result = this.engine.eval(script.toString());
			return (String) result;
		} catch (Exception e) {
			logger.error("JS evaluation error : "+e.getMessage());
			throw new ProxyEvaluationException(
					"Error while executing PAC script: " + e.getMessage(), e);
		}

	}
}
