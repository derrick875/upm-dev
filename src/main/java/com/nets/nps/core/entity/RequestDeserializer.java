package com.nets.nps.core.entity;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.nets.nps.MtiRequestMapper;
import com.nets.upos.commons.logger.ApsLogger;

public class RequestDeserializer extends StdDeserializer<Request> {
	
	private static final ApsLogger logger = new ApsLogger(RequestDeserializer.class);

    private final static String MTI_FIELD_NAME = "mti";
    private final static String PROCESS_CODE_FIELD_NAME = "process_code";

    protected RequestDeserializer() {
        super(Request.class);
    }

    @Override
    public Request deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    	logger.info("RequestDeserializer:deserialize service Started");
        JsonNode node = parser.readValueAsTree();

        String mti = Optional.ofNullable(node.get(MTI_FIELD_NAME))
                             .map(v -> v.asText())
                             .orElseThrow( () -> new JsonMappingException(parser, "No mti provided") );
        String processCode = Optional.ofNullable(node.get(PROCESS_CODE_FIELD_NAME))
                                        .map( v -> v.asText())
                                        .orElseThrow( () -> new JsonMappingException(parser, "No process code provided") );

        logger.info("Mapping MtiRequestMapper [mti=" + mti + ", processCode=" + processCode + "].");
        Optional<MtiRequestMapper> mtiRequestMapper = MtiRequestMapper.find(mti, processCode);
        if (mtiRequestMapper.isPresent()) {
            return parser.getCodec().treeToValue(node, mtiRequestMapper.get().getClzRequest());
        } else {
        	// TODO: handle error in a consistent manner; HttpController.routing() returning error in Json format
        	logger.error("Not supported mti: " + mti + " and process code: " + processCode);
            throw new JsonMappingException(parser, "Not supported mti: " + mti + " and process code: " + processCode);
        }
    }
}
