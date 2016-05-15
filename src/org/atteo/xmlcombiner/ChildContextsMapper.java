package org.atteo.xmlcombiner;

import java.util.List;

import com.google.common.collect.ListMultimap;

public interface ChildContextsMapper {

	ListMultimap<Key, Context> mapChildContexts(Context parent, List<String> keyAttributeNames);

}
