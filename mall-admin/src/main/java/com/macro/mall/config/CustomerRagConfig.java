package com.macro.mall.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.ApplicationRunner;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.springframework.ai.document.Document;

@Configuration
@ConditionalOnProperty(name = "easygoing.ai.enabled", havingValue = "true")
public class CustomerRagConfig {
    @Bean
    SimpleVectorStore customerVectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    @Bean
    ApplicationRunner customerKnowledgeBootstrap(SimpleVectorStore vectorStore) {
        return args -> {
            File storeFile = new File("data/customer-rag.json");
            if (storeFile.exists()) {
                vectorStore.load(storeFile);
                return;
            }
            String content = new ClassPathResource("rag/knowledge.md").getContentAsString(StandardCharsets.UTF_8);
            vectorStore.add(List.of(new Document(content, Map.of("source", "易going客服知识库"))));
            storeFile.getParentFile().mkdirs();
            vectorStore.save(storeFile);
        };
    }
}
