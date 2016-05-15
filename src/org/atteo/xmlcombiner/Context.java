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
package org.atteo.xmlcombiner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOM {@link Element} with any other non-element nodes which precede it.
 */
class Context {
	public static final String KEYS_ATTRIBUTE_NAME = "combine.keys";
	public static final String ID_ATTRIBUTE_NAME = "combine.id";
	private final List<Node> neighbours = new ArrayList<>();
	private Element element;

	public Context() {
	}

	public static Context fromElement(Element element) {
		Context context = new Context();
		context.setElement(element);
		return context;
	}

	public void addNeighbour(Node node) {
		neighbours.add(node);
	}

	public List<Node> getNeighbours() {
		return neighbours;
	}

	public void setElement(Element element) {
		this.element = element;
	}

	public Element getElement() {
		return element;
	}

	public void addAsChildTo(Node node) {
		for (Node neighbour : neighbours) {
			node.appendChild(neighbour);
		}
		node.appendChild(element);
	}

	public void addAsChildTo(Node node, Document document) {
		for (Node neighbour : neighbours) {
			node.appendChild(document.importNode(neighbour, true));
		}
		if (element != null) {
			node.appendChild(document.importNode(element, true));
		}
	}

	public List<Context> groupChildContexts() {
		if (element == null) {
			return Collections.emptyList();
		}
		NodeList nodes = element.getChildNodes();
		List<Context> contexts = new ArrayList<>(nodes.getLength());

		Context context = new Context();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node instanceof Element) {
				context.setElement((Element) node);
				contexts.add(context);
				context = new Context();
			} else {
				context.addNeighbour(node);
			}
		}
		// add last with empty element
		contexts.add(context);
		return contexts;
	}

	@Override
	public String toString() {
		return "[" + neighbours + ", " + element + "]";
	}
}
