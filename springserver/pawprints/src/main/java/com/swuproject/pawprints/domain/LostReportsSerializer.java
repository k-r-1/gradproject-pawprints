package com.swuproject.pawprints.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class LostReportsSerializer extends StdSerializer<LostReports> {

    public LostReportsSerializer() {
        this(null);
    }

    public LostReportsSerializer(Class<LostReports> t) {
        super(t);
    }

    @Override
    public void serialize(LostReports value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("lostId", value.getLostId());
        gen.writeNumberField("petId", value.getPetId());
        gen.writeStringField("lostTitle", value.getLostTitle());
        gen.writeNumberField("lostAreaLat", value.getLostAreaLat());
        gen.writeNumberField("lostAreaLng", value.getLostAreaLng());
        gen.writeObjectField("lostDate", value.getLostDate());
        gen.writeStringField("lostLocation", value.getLostLocation());
        gen.writeStringField("lostDescription", value.getLostDescription());
        gen.writeStringField("lostContact", value.getLostContact());

        gen.writeFieldName("images");
        gen.writeStartArray();
        for (LostReportsImage image : value.getImages()) {
            gen.writeStartObject();
            gen.writeNumberField("lostImageId", image.getLostImageId());
            gen.writeStringField("lostImagePath", image.getLostImagePath());
            gen.writeEndObject();
        }
        gen.writeEndArray();
        gen.writeEndObject();
    }
}
