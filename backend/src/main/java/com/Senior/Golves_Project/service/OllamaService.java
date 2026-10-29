package com.Senior.Golves_Project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OllamaService {

    private final RestTemplate restTemplate;

    public OllamaService() {

        org.springframework.http.client.SimpleClientHttpRequestFactory factory =
                new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(15000);
        factory.setReadTimeout(60000);
        this.restTemplate = new RestTemplate(factory);
    }

    @Value("${ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${ollama.model:tinyllama}")
    private String model;

    public String buildSentence(String meaning) {

        if (meaning == null ||
                meaning.isBlank() ||
                meaning.equalsIgnoreCase("Unknown gesture") ||
                meaning.equalsIgnoreCase("UNKNOWN_GESTURE")) {

            return "Unknown gesture.";
        }

        try {

            Map<String, Object> body = new HashMap<>();

            body.put("model", model);
            body.put("stream", false);

            body.put(
                    "prompt",
                    "You are an AI assistant for people with speech disabilities.\n" +
                            "Generate one short natural communication sentence only.\n" +
                            "Do not explain anything.\n" +
                            "Do not add quotation marks.\n" +
                            "Intent: " + meaning
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(body, headers);


                    ResponseEntity<Map> response =
                            restTemplate.postForEntity(
                                    ollamaBaseUrl + "/api/generate",
                                    entity,
                                    Map.class
                            );

            Object text = response.getBody() == null
                    ? null
                    : response.getBody().get("response");

            System.out.println("BUILD SENTENCE RAW RESPONSE:");
            System.out.println(text);

            if (text == null) {
                return meaning;
            }

            return text.toString().trim();

        } catch (Exception ex) {

            System.out.println("BUILD SENTENCE ERROR:");
            ex.printStackTrace();

            return meaning;
        }
    }

    public String buildSentenceFromMeanings(List<String> meanings) {

        if (meanings == null || meanings.isEmpty()) {
            return "";
        }

        return buildSentence(String.join(" + ", meanings));
    }

    public List<String> buildSuggestions(
            String detectedWord,
            String mode,
            String language
    ) {

        if (detectedWord == null ||
                detectedWord.isBlank() ||
                detectedWord.equalsIgnoreCase("Unknown gesture") ||
                detectedWord.equalsIgnoreCase("UNKNOWN_GESTURE") ||
                detectedWord.equalsIgnoreCase("Unknown")) {

            return List.of("No suggestions available");
        }

        try {

            System.out.println("========== AI SUGGESTION REQUEST ==========");
            System.out.println("WORD: " + detectedWord);
            System.out.println("MODE: " + mode);
            System.out.println("LANGUAGE: " + language);

            Map<String, Object> body = new HashMap<>();

            String commonRules =
                    "\nSTRICT RULES:\n" +
                            "- Generate sentences ONLY about the detected word.\n" +
                            "- NEVER mention other words.\n" +
                            "- NEVER mention another emergency, food, or health topic.\n" +
                            "- Every sentence must clearly include or directly refer to: " + detectedWord + "\n" +
                            "- Maximum 10 words per sentence.\n" +
                            "- Return ONLY the sentences.\n" +
                            "- One sentence per line.\n" +
                            "- No numbering.\n" +
                            "- No quotation marks.\n";

            String prompt;

            if (mode != null && mode.equalsIgnoreCase("Emergency")) {

                prompt =
                        "You are an emergency communication assistant.\n\n" +

                                "Generate exactly 3 realistic emergency communication sentences.\n\n" +

                                "Detected word: " + detectedWord + "\n" +

                                commonRules +

                                "\nExamples:\n" +
                                "fire -> There is a fire nearby.\n" +
                                "police -> Please call the police.\n" +
                                "hospital -> I need the hospital immediately.\n" +
                                "help -> I need urgent help.\n\n" +

                                "Now generate the 3 sentences.";
            }

            else if (mode != null && mode.equalsIgnoreCase("Food")) {

                prompt =
                        "You are a food communication assistant.\n\n" +

                                "Generate exactly 3 food-related communication sentences.\n\n" +

                                "Detected word: " + detectedWord + "\n" +

                                commonRules +

                                "\nExamples:\n" +
                                "coffee -> Can I have coffee please?\n" +
                                "water -> I would like some water.\n" +
                                "pizza -> I want pizza for dinner.\n\n" +

                                "Now generate the 3 sentences.";
            }

            else if (mode != null && mode.equalsIgnoreCase("Health")) {

                prompt =
                        "You are a health communication assistant.\n\n" +

                                "Generate exactly 3 medical or health-related sentences.\n\n" +

                                "Detected word: " + detectedWord + "\n" +

                                commonRules +

                                "\nExamples:\n" +
                                "pain -> I feel pain in my chest.\n" +
                                "medicine -> I need my medicine.\n" +
                                "doctor -> I need to see a doctor.\n\n" +

                                "Now generate the 3 sentences.";
            }

            else {

                prompt =
                        "You are a communication assistant.\n\n" +

                                "Generate exactly 3 short communication sentences.\n\n" +

                                "Detected word: " + detectedWord + "\n" +

                                commonRules +

                                "\nNow generate the 3 sentences.";
            }

            body.put("model", model);
            body.put("stream", false);
            body.put("prompt", prompt);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(body, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(
                            ollamaBaseUrl + "/api/generate",
                            entity,
                            Map.class
                    );

            Object text = response.getBody() == null
                    ? null
                    : response.getBody().get("response");

            System.out.println("========== OLLAMA RAW RESPONSE ==========");
            System.out.println(text);

            if (text == null) {

                System.out.println("NULL RESPONSE -> USING FALLBACK");

                return fallbackSuggestions(detectedWord, mode);
            }

            List<String> suggestions =
                    text.toString()
                            .lines()
                            .map(String::trim)
                            .filter(s -> !s.isBlank())
                            .map(s -> s.replaceFirst("^[-*•]\\s*", ""))
                            .map(s -> s.replaceFirst("^\\d+[.)]\\s*", ""))
                            .map(s -> s.replace("\"", ""))
                            .filter(s -> s.toLowerCase()
                                    .contains(detectedWord.toLowerCase()))
                            .limit(3)
                            .toList();

            System.out.println("========== PARSED SUGGESTIONS ==========");
            suggestions.forEach(System.out::println);

            if (suggestions.isEmpty()) {

                System.out.println("EMPTY PARSED LIST -> USING FALLBACK");

                return fallbackSuggestions(detectedWord, mode);
            }

            return suggestions;

        } catch (Exception e) {

            System.out.println("========== OLLAMA ERROR ==========");
            e.printStackTrace();

            return fallbackSuggestions(detectedWord, mode);
        }
    }
    public String translateText(String text, String targetLanguage) {

        if (text == null || text.isBlank()) {
            return "";
        }

        try {

            Map<String, Object> body = new HashMap<>();

            String prompt =
                    "Translate the following sentence to "
                            + targetLanguage + ".\n" +

                            "Return ONLY the translated sentence.\n" +

                            "Do not explain anything.\n" +

                            "Do not add quotation marks.\n\n" +

                            "Sentence: " + text;

            body.put("model", model);
            body.put("stream", false);
            body.put("prompt", prompt);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(body, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(
                            ollamaBaseUrl + "/api/generate",
                            entity,
                            Map.class
                    );

            Object result = response.getBody() == null
                    ? null
                    : response.getBody().get("response");

            if (result == null) {
                return text;
            }

            return result.toString()
                    .trim()
                    .replace("\"", "");

        } catch (Exception e) {

            e.printStackTrace();

            return text;
        }
    }
    private List<String> fallbackSuggestions(String detectedWord, String mode) {

        System.out.println("========== FALLBACK SUGGESTIONS ==========");

        if (mode != null && mode.equalsIgnoreCase("Emergency")) {

            return List.of(
                    "I need help with " + detectedWord + ".",
                    "Please respond to " + detectedWord + " now.",
                    "This is about " + detectedWord + "."
            );
        }

        if (mode != null && mode.equalsIgnoreCase("Food")) {

            return List.of(
                    "I would like " + detectedWord + ".",
                    "Can I have " + detectedWord + " please?",
                    "Please bring me " + detectedWord + "."
            );
        }

        if (mode != null && mode.equalsIgnoreCase("Health")) {

            return List.of(
                    "I need help with " + detectedWord + ".",
                    "Please assist me with " + detectedWord + ".",
                    "I need support for " + detectedWord + "."
            );
        }

        return List.of(
                "I need " + detectedWord + ".",
                "Please help me with " + detectedWord + ".",
                "Can you assist me with " + detectedWord + "?"
        );
    }
}