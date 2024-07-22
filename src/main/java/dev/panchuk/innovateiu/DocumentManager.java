package dev.panchuk.innovateiu;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class DocumentManager {

    private final Map<String, Document> documentStorage = new HashMap<>();
    private int idCounter = 0;

    /**
     * Add the document to storage and generates a unique ID if it does not exist.
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (document.getId() == null || document.getId().isEmpty()) {
            document.setId(generateUniqueId());
        }
        documentStorage.put(document.getId(), document);
        return document;
    }

    /**
     * Finds documents which match the search request.
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        return documentStorage.values().stream()
                .filter(document -> matchesSearchRequest(document, request))
                .collect(Collectors.toList());
    }

    /**
     * Finds a document by ID.
     *
     * @param id - document ID
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(documentStorage.get(id));
    }

    /**
     * Checks if a document matches the search request criteria.
     *
     * @param document - the document to check
     * @param request - the search request criteria
     * @return true if the document matches, false otherwise
     */
    private boolean matchesSearchRequest(Document document, SearchRequest request) {
        boolean matches = true;
        if (request.getTitlePrefixes() != null && !request.getTitlePrefixes().isEmpty()) {
            matches = request.getTitlePrefixes().stream().anyMatch(prefix -> document.getTitle().startsWith(prefix));
        }
        if (matches && request.getContainsContents() != null && !request.getContainsContents().isEmpty()) {
            matches = request.getContainsContents().stream().anyMatch(content -> document.getContent().contains(content));
        }
        if (matches && request.getAuthorIds() != null && !request.getAuthorIds().isEmpty()) {
            matches = request.getAuthorIds().contains(document.getAuthor().getId());
        }
        if (matches && request.getCreatedFrom() != null) {
            matches = document.getCreated().isAfter(request.getCreatedFrom()) || document.getCreated().equals(request.getCreatedFrom());
        }
        if (matches && request.getCreatedTo() != null) {
            matches = document.getCreated().isBefore(request.getCreatedTo()) || document.getCreated().equals(request.getCreatedTo());
        }
        return matches;
    }

    /**
     * Generates a unique ID for a new document.
     *
     * @return the generated unique ID
     */
    private String generateUniqueId() {
        return String.valueOf(++idCounter);
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}