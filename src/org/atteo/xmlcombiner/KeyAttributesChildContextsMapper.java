package org.atteo.xmlcombiner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

public class KeyAttributesChildContextsMapper implements ChildContextsMapper {

	@Override
	public ListMultimap<Key, Context> mapChildContexts(Context parent,
			List<String> keyAttributeNames) {
		List<Context> contexts = parent.groupChildContexts();

		ListMultimap<Key, Context> map = LinkedListMultimap.create();
		for (Context context : contexts) {
			Element contextElement = context.getElement();

			if (contextElement != null) {
				Map<String, String> keys = new HashMap<>();
				for (String keyAttributeName : keyAttributeNames) {
					Attr keyNode = contextElement.getAttributeNode(keyAttributeName);
					if (keyNode != null) {
						keys.put(keyAttributeName, keyNode.getValue());
					}
				}
				{
					Attr keyNode = contextElement.getAttributeNode(Context.ID_ATTRIBUTE_NAME);
					if (keyNode != null) {
						keys.put(Context.ID_ATTRIBUTE_NAME, keyNode.getValue());
					}
				}
				Key key = new Key(contextElement.getTagName(), keys);
				map.put(key, context);
			} else {
				map.put(Key.BEFORE_END, context);
			}
		}
		return map;
	}
}
