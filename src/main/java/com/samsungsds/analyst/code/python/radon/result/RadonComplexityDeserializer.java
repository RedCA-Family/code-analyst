/*
Copyright 2018 Samsung SDS

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.samsungsds.analyst.code.python.radon.result;

import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RadonComplexityDeserializer implements JsonDeserializer<Map<String, List<RadonComplexityItem>>> {
    private static final Logger LOGGER = LogManager.getLogger(RadonComplexityDeserializer.class);

    @Override
    public Map<String, List<RadonComplexityItem>> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<String, List<RadonComplexityItem>> result = new HashMap<>();

        for (Map.Entry<String, JsonElement> files : json.getAsJsonObject().entrySet()) {
            String path  = files.getKey();
            LOGGER.debug("{} : {}" , path, files.getValue().toString());
            if (!files.getValue().isJsonArray()) {
                LOGGER.debug("Skip : no list");
                continue;
            }
            List<RadonComplexityItem> list = new ArrayList<>();
            for (JsonElement element : files.getValue().getAsJsonArray()) {
                JsonObject item = element.getAsJsonObject();
                RadonComplexityItem complexity = getRadonComplexityItem(item);

                if (item.has("methods")) {
                    complexity.setMethods(getMethodsInClass(item));
                } else {
                    complexity.setMethods(new ArrayList<>());   // empty list
                }

                list.add(complexity);
            }
            result.put(path, list);
        }

        return result;
    }

    private List<RadonComplexityItem> getMethodsInClass(JsonObject item) {
        List<RadonComplexityItem> methods = new ArrayList<>();

        for (JsonElement subElement : item.get("methods").getAsJsonArray()) {
            JsonObject subItem = subElement.getAsJsonObject();
            RadonComplexityItem subComplexity = getRadonComplexityItem(subItem);

            methods.add(subComplexity);
        }
        return methods;
    }

    private RadonComplexityItem getRadonComplexityItem(JsonObject item) {
        return new RadonComplexityItem(
            item.get("type").getAsString(),
            item.get("rank").getAsString(),
            item.get("lineno").getAsInt(),
            item.get("name").getAsString(),
            item.get("complexity").getAsInt()
        );
    }
}
