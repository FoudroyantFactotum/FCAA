/*
 * Copyright (c) 2016 Foudroyant Factotum
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses>.
 */
package com.foudroyantfactotum.mod.fousarchive.midi;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Type;
import java.util.Map;

public class JsonMidiDetails
{
    public final ImmutableMap<ResourceLocation, ImmutableMap<String, String>> midiDetails;

    public JsonMidiDetails(ImmutableMap<ResourceLocation, ImmutableMap<String, String>> midiDetails)
    {
        this.midiDetails = midiDetails;
    }

    public static class Json implements JsonSerializer<JsonMidiDetails>, JsonDeserializer<JsonMidiDetails>
    {
        public static final Json JSD = new Json();

        private Json ()
        {
            //noop
        }

        @Override
        public JsonElement serialize(JsonMidiDetails src, Type typeOfSrc, JsonSerializationContext context)
        {
            final JsonObject joobj = new JsonObject();

            src.midiDetails.entrySet().stream().map((a) ->
            {
                final JsonObject jobj = new JsonObject();

                a.getValue().entrySet().stream().forEach((b) -> jobj.add(b.getKey(), new JsonPrimitive(b.getValue())));

                return Pair.of(a.getKey().toString(), jobj);
            }).forEach((a) -> joobj.add(a.getKey(), a.getValue()));

            return joobj;
        }

        @Override
        public JsonMidiDetails deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            final ImmutableMap.Builder<ResourceLocation, ImmutableMap<String, String>> builder = ImmutableMap.builder();
            final JsonObject joobj = json.getAsJsonObject();

            for (final Map.Entry<String, JsonElement> e : joobj.entrySet())
            {
                final ImmutableMap.Builder<String, String> builderData = ImmutableMap.builder();
                final JsonObject jobject = e.getValue().getAsJsonObject();

                final String resource = e.getKey();

                for (final Map.Entry<String, JsonElement> element : jobject.entrySet())
                {
                    builderData.put(element.getKey(), element.getValue().getAsString());
                }

                builder.put(new ResourceLocation(resource), builderData.build());
            }

            return new JsonMidiDetails(builder.build());
        }
    }

    @Override
    public String toString()
    {
        return midiDetails.toString();
    }
}