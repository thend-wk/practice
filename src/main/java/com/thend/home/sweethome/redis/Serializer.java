package com.thend.home.sweethome.redis;

import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;


public class Serializer {
	
	public static ObjectMapper json = new ObjectMapper();
	
	static {
        json.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
		.configure(Feature.ALLOW_SINGLE_QUOTES, true)
		.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false)
		.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
    }

}
