/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.atteo.xmlcombiner;

/**
 * Controls the behavior when merging two XML nodes.
 */
public enum CombineSelf {
	/**
	 * Merge elements.
	 * <p>
	 * Attributes from dominant element override those from recessive element.
	 * Child elements are by default paired using their keys (tag name and selected attributes) and combined
	 * recursively. The exact behavior depends on {@link CombineChildren 'combine.children'} attribute value.
	 * </p>
	 * <p>
	 *
	 * Example:<br/>
	 * First:
	 * <pre>
	 * {@code
	 *  <config>
	 *     <service id="1">
	 *         <parameter>parameter</parameter>
	 *     </service>
	 *  </config>
	 * }
	 * </pre>
	 * Second:
	 * <pre>
	 * {@code
	 *  <config>
	 *     <service id="1">
	 *         <parameter2>parameter</parameter2>
	 *     </service>
	 *  </config>
	 * }
	 * </pre>
	 * Result:
	 * <pre>
	 * {@code
	 *  <config>
	 *     <service id="1">
	 *         <parameter>parameter</parameter>
	 *         <parameter2>parameter</parameter2>
	 *     </service>
	 *  </config>
	 * }
	 * </pre>
	 * </p>
	 */
	MERGE,

	/**
	 * Remove entire element with attributes and children.
	 *
	 * <p>
	 * Example:<br/>
	 * First:
	 * <pre>
	 * {@code
	 *  <config>
	 *     <service id="1">
	 *         <parameter>parameter</parameter>
	 *     </service>
	 *  </config>
	 * }
	 * </pre>
	 * Second:
	 * <pre>
	 * {@code
	 *  <config>
	 *     <service id="1" combine.self="REMOVE"/>
	 *  </config>
	 * }
	 * </pre>
	 * Result:
	 * <pre>
	 * {@code
	 *  <config>
	 *  </config>
	 * }
	 * </pre>
	 * </p>
	 */
	REMOVE,

	/**
	 * Behaves exactly like {@link #MERGE} if paired element exists in any subsequent dominant document.
	 * If paired element is not found in any dominant document behaves the same as {@link #REMOVE}
	 * <p>
	 * This behavior is specifically designed to allow specifying default values which are used only
	 * when given element exists in any subsequent document.
	 * </p>
	 * <p>
	 * Example:<br/>
	 * First:
	 * <pre>
	 * {@code
	 *  <config>
	 *     <service id="1" combine.self="DEFAULTS">
	 *         <parameter>parameter</parameter>
	 *     </service>
	 *     <service id="2" combine.self="DEFAULTS"/>
	 *  </config>
	 * }
	 * </pre>
	 * Second:
	 * <pre>
	 * {@code
	 * <config>
	 *     <service id="1">
	 *     </service>
	 *  </config>
	 * }
	 * </pre>
	 * Result:
	 * <pre>
	 * {@code
	 * <config>
	 *     <service id="1">
	 *         <parameter>parameter</parameter>
	 *     </service>
	 *  </config>
	 * }
	 * </pre>
	 * </p>
	 */
	DEFAULTS,

	/**
	 * Override element.
	 * <p>
	 * Completely ignores content from recessive document by overwriting it
	 * with element from dominant document.
	 * </p>
	 *
	 * <p>
	 * Example:<br/>
	 * First:
	 * <pre>
	 * {@code
	 * <config>
	 *     <service id="1">
	 *         <parameter>parameter</parameter>
	 *     </service>
	 *  </config>
	 * }
	 * </pre>
	 * Second:
	 * <pre>
	 * {@code
	 * <config>
	 *     <service id="1" combine.self="override">
	 *         <parameter2>parameter2</parameter2>
	 *     </service>
	 *  </config>
	 * }
	 * </pre>
	 * Result:
	 * <pre>
	 * {@code
	 * <config>
	 *     <service id="1">
	 *         <parameter2>parameter2</parameter2>
	 *     </service>
	 *  </config>
	 * }
	 * </pre>
	 * </p>
	 */
	OVERRIDE,

	/**
	 * Override element.
	 * <p>
	 * Completely ignores content from recessive document by overwriting it
	 * with element from dominant document.
	 * </p>
	 * <p>
	 * The difference with {@link #OVERRIDE} is that OVERRIDABLE is specified on the tag in recessive document.
	 * </p>
	 * <p>
	 * Example:<br/>
	 * First:
	 * <pre>
	 * {@code
	 *  <config>
	 *     <service id="id1" combine.self="OVERRIDABLE">
	 *         <test/>
	 *     </service>
	 *  </config>
	 * }
	 * </pre>
	 * Second:
	 * <pre>
	 * {@code
	 *  <config>
	 *  </config>
	 * }
	 * </pre>
	 * Result:
	 * <pre>
	 * {@code
	 * <config>
	 *     <service id="id1" combine.self="OVERRIDABLE">
	 *         <test/>
	 *     </service>
	 *  </config>
	 * }
	 * </pre>
	 * </p>
	 * <p>
	 * Example2:<br/>
	 * First:
	 * <pre>
	 * {@code
	 *  <config>
	 *     <service id="id1" combine.self="OVERRIDABLE">
	 *         <test/>
	 *     </service>
	 *  </config>
	 * }
	 * </pre>
	 * Second:
	 * <pre>
	 * {@code
	 *  <config>
	 * 		<service id="id1"/>
	 *  </config>
	 * }
	 * </pre>
	 * Result:
	 * <pre>
	 * {@code
	 *  <config>
	 * 		<service id="id1"/>
	 *  </config>
	 * }
	 * </pre>
	 * </p>
	 */
	OVERRIDABLE,

	/**
	 * Override element.
	 * <p>
	 * Completely ignores content from recessive document by overwriting it
	 * with element from dominant document.
	 * </p>
	 * <p>
	 * The difference with {@link #OVERRIDABLE} is that with OVERRIDABLE_BY_TAG recessive element is ignored
 even when the id is different.
 </p>
	 * <p>
	 * Example:<br/>
	 * First:
	 * <pre>
	 * {@code
	 *  <config>
	 *     <service id="id1" combine.self="OVERRIDABLE">
	 *         <test/>
	 *     </service>
	 *  </config>
	 * }
	 * </pre>
	 * Second:
	 * <pre>
	 * {@code
	 *  <config>
	 * 		<service id="id2"/>
	 *  </config>
	 * }
	 * </pre>
	 * Result:
	 * <pre>
	 * {@code
	 *  <config>
	 * 		<service id="id2"/>
	 *  </config>
	 * }
	 * </pre>
	 * </p>
	 */
	OVERRIDABLE_BY_TAG;

	public static final String ATTRIBUTE_NAME = "combine.self";
}

