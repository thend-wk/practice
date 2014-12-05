package com.thend.home.sweethome.redis;

import java.io.StringWriter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;


public class Serializer {
	
	public static ObjectMapper json = new ObjectMapper();
	
	private static final JsonFactory jsonFactory = new JsonFactory();
	
	static {
        json.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
		.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
		.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false)
		.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false)
		.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
    }
	
	public static String toJson(Object pojo, boolean prettyPrint) {
        try {
            StringWriter sw = new StringWriter();
            JsonGenerator jg = jsonFactory.createJsonGenerator(sw);
            if (prettyPrint) {
            	jg.useDefaultPrettyPrinter();
            }
            json.writeValue(jg, pojo);
            return sw.toString();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
	}
}
