/* Copyright 2009 British Broadcasting Corporation
   Copyright 2009 Meta Broadcast Ltd

Licensed under the Apache License, Version 2.0 (the "License"); you
may not use this file except in compliance with the License. You may
obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing
permissions and limitations under the License. */

package org.uriplay.beans;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.uriplay.feeds.OembedOutput;
import org.uriplay.media.entity.Encoding;
import org.uriplay.media.entity.Item;
import org.uriplay.media.entity.Location;
import org.uriplay.media.entity.Version;

/**
 * General translator to build an oEmbed representation from information in the bean graph.
 *  
 * @author Robert Chatley (robert@metabroadcast.com)
 */
public class OembedTranslator implements BeanGraphExtractor<InputStream>, BeanGraphWriter {

	interface OutputFactory {
		OembedOutput createOutput();
	}
	
	private final OutputFactory feedFactory;
	
	public OembedTranslator(OutputFactory outputFactory) {
		this.feedFactory = outputFactory;
	}

	public Representation extractFrom(InputStream source) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	public Representation extractFrom(InputStream source, DescriptionMode mode) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	public void writeTo(Collection<Object> graph, OutputStream stream) {

		OembedOutput output = feedFactory.createOutput();
		
		for (Object bean : graph) {
			
			if (bean instanceof Item) {
				
				Item item = (Item) bean;
				
				output.setTitle(item.getTitle());
				output.setProviderUrl(item.getPublisher());
				output.setType("video");
				
				if (item.getVersions() != null) {
					for (Version version : item.getVersions()) {
						if (version.getManifestedAs() != null) {
							for (Encoding encoding : version.getManifestedAs()) {
								if (encoding.getVideoVerticalSize() != null) {
									output.setHeight(encoding.getVideoVerticalSize());
								}
								if (encoding.getVideoHorizontalSize() != null) {
									output.setWidth(encoding.getVideoHorizontalSize());
								}
								for (Location location : encoding.getAvailableAt()) {
									output.setEmbedCode(escapeQuotes(location.getEmbedCode()));
								}
							}
						}
					}
				}
			}
		}
		
		output.writeTo(stream);
	}


	private String escapeQuotes(String unescaped) {
		if (unescaped == null) { return null; }
		return unescaped.replace("\"", "\\\"");
	}

}
