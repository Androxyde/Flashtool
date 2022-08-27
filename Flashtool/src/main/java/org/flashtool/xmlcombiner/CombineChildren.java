/*
 * Copyright 2012 Atteo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flashtool.xmlcombiner;

/**
 * Controls the behavior of merging child elements.
 */
public enum CombineChildren {
	/**
	 * Merge subelements from both elements.
	 *
	 * <p>
	 * This is the default.
	 * Those subelements which can be uniquely paired between two documents using the key (tag+selected attributes)
	 * will be merged, those that cannot be paired will be appended.<br/>
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
	 *     <service id="1"/>
	 *         <parameter>other value</parameter>
	 *     </service>
	 *  </config>
	 * }
	 * </pre>
	 * Result:
	 * <pre>
	 * {@code
	 *  <config>
	 *     <service id="1"/>
	 *         <parameter>other value</parameter>
	 *     </service>
	 *  </config>
	 * }
	 * </pre>
	 * </p>
	 */
	MERGE,

	/**
	 * Always append child elements from both recessive and dominant elements.
	 *
	 * <p>
	 * Example:<br/>
	 * First:
	 * <pre>
	 * {@code
	 * <config>
	 *     <service id="1" combine.children="append">
	 *         <parameter>parameter</parameter>
	 *     </service>
	 *  </config>
	 * }
	 * </pre>
	 * Second:
	 * <pre>
	 * {@code
	 * <config>
	 *     <service id="1">
	 *         <parameter>parameter</parameter>
	 *     </service>
	 *  </config>
	 * }
	 * </pre>
	 * Result:
	 * <pre>
	 * {@code
	 * <config>
	 *     <service id="1" combine.children="append">
	 *         <parameter>parameter</parameter>
	 *         <parameter>parameter</parameter>
	 *     </service>
	 *  </config>
	 * }
	 * </pre>
	 * </p>
	 */
	APPEND;

	public static final String ATTRIBUTE_NAME = "combine.children";
}
