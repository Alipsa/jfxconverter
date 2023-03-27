/*------------------------------------------------------------------------------
 * Copyright (C) 2022 Herve Girod
 *
 * Distributable under the terms of either the Apache License (Version 2.0) or
 * the GNU Lesser General Public License, as specified in the COPYING file.
 ------------------------------------------------------------------------------*/
package org.mdiutil.xml.tree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents a bound prefix.
 *
 * @since 1.2.40
 */
class BoundPrefix {

    /**
     * The URI to prefix map.
     */
    Map<String, String> uriToPrefix = null;
    /**
     * The prefix to URI map.
     */
    Map<String, String> prefixToURI = null;
    /**
     * The schemaLocation declaration.
     */
    SortableQName schemaLocationDecl = null;
    /**
     * The schemaLocation value.
     */    
    String schemaLocationValue = null;

    BoundPrefix() {
    }

    BoundPrefix(BoundPrefix thePrefix) {
        this.uriToPrefix = thePrefix.uriToPrefix;
        this.prefixToURI = thePrefix.prefixToURI;
        this.schemaLocationDecl = thePrefix.schemaLocationDecl;
        this.schemaLocationValue = thePrefix.schemaLocationValue;
    }

    private Map<String, String> createPrefixToURI(Map<String, String> uriToPrefix) {
        Map<String, String> _prefixToURI = new HashMap<>();
        Iterator<Map.Entry<String, String>> it = uriToPrefix.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            String uri = entry.getKey();
            String prefix = entry.getValue();
            _prefixToURI.put(prefix, uri);
        }
        return _prefixToURI;
    }

    /**
     * Merge the prefixes from a node with the current prefixes.
     *
     * @param node the node
     * @return the merged prefixes
     */
    Map<String, String> getResultBoundPrefixes(XMLNode node) {
        SortableQName nodeSchemaDecl = node.getSchemaLocationDeclaration();
        if (nodeSchemaDecl != null) {
            schemaLocationDecl = nodeSchemaDecl;
            schemaLocationValue = node.getSchemaLocationValue();
        }
        Map<String, String> nodeUriToPrefix = node.getBoundPrefixes();
        if (uriToPrefix == null) {
            if (nodeUriToPrefix == null) {
                return null;
            } else {
                uriToPrefix = new HashMap<>(nodeUriToPrefix);
                prefixToURI = createPrefixToURI(nodeUriToPrefix);
                return uriToPrefix;
            }
        } else if (nodeUriToPrefix == null) {
            return null;
        } else {
            Map<String, String> resultPrefix = new HashMap<>();
            Iterator<Map.Entry<String, String>> it = nodeUriToPrefix.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                String uri = entry.getKey();
                String prefix = entry.getValue();
                if (uriToPrefix.containsKey(uri) && (prefix.isEmpty() || uriToPrefix.get(uri).equals(prefix))) {
                    resultPrefix.remove(uri);
                } else {
                    if (prefixToURI.containsKey(prefix)) {
                        String theURI = prefixToURI.get(prefix);
                        uriToPrefix.remove(theURI);
                        resultPrefix.remove(theURI);
                    }
                    uriToPrefix.put(uri, prefix);
                    resultPrefix.put(uri, prefix);
                    prefixToURI.put(prefix, uri);
                }
            }
            return resultPrefix;
        }
    }
}
