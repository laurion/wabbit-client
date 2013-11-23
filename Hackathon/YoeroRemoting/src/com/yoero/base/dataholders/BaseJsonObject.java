package com.yoero.base.dataholders;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BaseJsonObject {

	@JsonIgnore
	public Object EntityState;
	@JsonIgnore
	public Object EntityKey;

	public static <T> T parseJson(String str, Class<T> cc) {
		try {
			return generateMapper().readValue(str, cc);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static <T> T parseJson(String str, TypeReference<T> cc) {
		try {
			return generateMapper().readValue(str, cc);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	private static ObjectMapper generateMapper() { 
		ObjectMapper om = new ObjectMapper();
		// om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		om.setDateFormat(new JacksonSimpleDateFormat());
		
		return om;
	}

	public String toJson() {
		try {
			return generateMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

}
