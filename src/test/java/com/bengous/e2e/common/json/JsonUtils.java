package com.bengous.e2e.common.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class JsonUtils {

	private static final ThreadLocal<ObjectMapper> OBJECT_MAPPER = ThreadLocal
			.withInitial(() -> new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT));

	public static String formatString(String jsonString) throws JsonProcessingException {
		var mapper = OBJECT_MAPPER.get();
		return mapper.writeValueAsString(mapper.readTree(jsonString));
	}

	public static void cleanup() {
		OBJECT_MAPPER.remove();
	}

	private static final JsonNode IGNORED_PLACEHOLDER = TextNode.valueOf("${json-unit.ignore-element}");

	public static String makeExpectedLookLikeActual(String expectedJson, String actualJson)
			throws JsonProcessingException {
		JsonNode expectedNode = OBJECT_MAPPER.get().readTree(expectedJson).deepCopy();
		JsonNode actualNode = OBJECT_MAPPER.get().readTree(actualJson);
		reportFieldsFromActualToExpected(expectedNode, actualNode);
		return OBJECT_MAPPER.get().writeValueAsString(expectedNode);
	}

	private static void reportFieldsFromActualToExpected(JsonNode expectedNode, JsonNode actualNode) {
		// Vérifie si les deux nœuds sont des objets JSON
		if (expectedNode.isObject() && actualNode.isObject()) {
			ObjectNode expectedObjectNode = (ObjectNode) expectedNode;
			ObjectNode actualObjectNode = (ObjectNode) actualNode;

			// Parcourt tous les champs de l'objet 'actualNode'
			actualObjectNode.fieldNames().forEachRemaining(fieldName -> {
				if (expectedObjectNode.has(fieldName)) {
					// Si le champ existe aussi dans 'expectedNode', récursion pour comparer les
					// sous-nœuds
					reportFieldsFromActualToExpected(expectedObjectNode.get(fieldName),
							actualObjectNode.get(fieldName));
				} else {
					// Si le champ n'existe pas dans 'expectedNode', ajoute un placeholder pour ce
					// champ
					expectedObjectNode.set(fieldName, IGNORED_PLACEHOLDER);
				}
			});

			// Vérifie si les deux nœuds sont des tableaux JSON
		} else if (expectedNode.isArray() && actualNode.isArray()) {
			ArrayNode expectedArrayNode = (ArrayNode) expectedNode;
			ArrayNode actualArrayNode = (ArrayNode) actualNode;

			// Parcourt chaque élément du tableau 'actualArrayNode'
			for (int i = 0; i < actualArrayNode.size(); i++) {
				if (i < expectedArrayNode.size()) {
					// Si l'index existe aussi dans 'expectedArrayNode', récursion pour comparer les
					// éléments
					reportFieldsFromActualToExpected(expectedArrayNode.get(i), actualArrayNode.get(i));
				} else {
					// Si l'index n'existe pas dans 'expectedArrayNode', ajout d'un placeholder
					// selon le type d'élément
					if (actualArrayNode.get(i).isObject()) {
						// Si l'élément est un objet, ajoute un nouvel objet avec des placeholders pour
						// chaque champ
						ObjectNode newObjectNode = expectedArrayNode.addObject();
						newObjectNode.setAll((ObjectNode) actualArrayNode.get(i)); // Copie tous les champs
						newObjectNode.fieldNames().forEachRemaining(fieldName -> newObjectNode.set(fieldName, IGNORED_PLACEHOLDER));
					} else if (actualArrayNode.get(i).isArray()) {
						// Si l'élément est un tableau, ajoute un tableau vide et applique la récursion
						expectedArrayNode.addArray();
						reportFieldsFromActualToExpected(expectedArrayNode.get(i), actualArrayNode.get(i));
					} else {
						// Si l'élément est de type simple (nombre, chaîne, etc.), ajoute un placeholder
						expectedArrayNode.add(IGNORED_PLACEHOLDER);
					}
				}
			}
		}
	}
}
