package dev.panchuk.innovateiu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentManagerTest {

    private DocumentManager documentManager;

    @BeforeEach
    public void setUp() {
        documentManager = new DocumentManager();
    }

    @Test
    public void testSaveNewDocument() {
        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("1")
                .name("Author1")
                .build();

        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Title1")
                .content("Content1")
                .author(author)
                .created(Instant.now())
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);

        assertNotNull(savedDocument.getId());
        assertEquals("Title1", savedDocument.getTitle());
        assertEquals("Content1", savedDocument.getContent());
        assertEquals(author, savedDocument.getAuthor());
    }

    @Test
    public void testSaveExistingDocument() {
        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("1")
                .name("Author1")
                .build();

        DocumentManager.Document document = DocumentManager.Document.builder()
                .id("1")
                .title("Title1")
                .content("Content1")
                .author(author)
                .created(Instant.now())
                .build();

        documentManager.save(document);

        DocumentManager.Document updatedDocument = DocumentManager.Document.builder()
                .id("1")
                .title("Updated Title")
                .content("Updated Content")
                .author(author)
                .created(Instant.now())
                .build();

        DocumentManager.Document savedDocument = documentManager.save(updatedDocument);

        assertEquals("1", savedDocument.getId());
        assertEquals("Updated Title", savedDocument.getTitle());
        assertEquals("Updated Content", savedDocument.getContent());
    }

    @Test
    public void testFindById() {
        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("1")
                .name("Author1")
                .build();

        DocumentManager.Document document = DocumentManager.Document.builder()
                .id("1")
                .title("Title1")
                .content("Content1")
                .author(author)
                .created(Instant.now())
                .build();

        documentManager.save(document);

        Optional<DocumentManager.Document> foundDocument = documentManager.findById("1");

        assertTrue(foundDocument.isPresent());
        assertEquals("Title1", foundDocument.get().getTitle());
        assertEquals("Content1", foundDocument.get().getContent());
    }

    @Test
    public void testSearchByTitlePrefix() {
        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("1")
                .name("Author1")
                .build();

        DocumentManager.Document document1 = DocumentManager.Document.builder()
                .title("Title1")
                .content("Content1")
                .author(author)
                .created(Instant.now())
                .build();

        DocumentManager.Document document2 = DocumentManager.Document.builder()
                .title("AnotherTitle")
                .content("Content2")
                .author(author)
                .created(Instant.now())
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .titlePrefixes(List.of("Title"))
                .build();

        List<DocumentManager.Document> results = documentManager.search(request);

        assertEquals(1, results.size());
        assertEquals("Title1", results.get(0).getTitle());
    }

    @Test
    public void testSearchByContent() {
        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("1")
                .name("Author1")
                .build();

        DocumentManager.Document document1 = DocumentManager.Document.builder()
                .title("Title1")
                .content("Content1")
                .author(author)
                .created(Instant.now())
                .build();

        DocumentManager.Document document2 = DocumentManager.Document.builder()
                .title("AnotherTitle")
                .content("AnotherContent")
                .author(author)
                .created(Instant.now())
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .containsContents(List.of("Content"))
                .build();

        List<DocumentManager.Document> results = documentManager.search(request);

        assertEquals(2, results.size());
    }

    @Test
    public void testSearchByAuthorId() {
        DocumentManager.Author author1 = DocumentManager.Author.builder()
                .id("1")
                .name("Author1")
                .build();

        DocumentManager.Author author2 = DocumentManager.Author.builder()
                .id("2")
                .name("Author2")
                .build();

        DocumentManager.Document document1 = DocumentManager.Document.builder()
                .title("Title1")
                .content("Content1")
                .author(author1)
                .created(Instant.now())
                .build();

        DocumentManager.Document document2 = DocumentManager.Document.builder()
                .title("AnotherTitle")
                .content("AnotherContent")
                .author(author2)
                .created(Instant.now())
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .authorIds(List.of("1"))
                .build();

        List<DocumentManager.Document> results = documentManager.search(request);

        assertEquals(1, results.size());
        assertEquals("Title1", results.get(0).getTitle());
    }

}
