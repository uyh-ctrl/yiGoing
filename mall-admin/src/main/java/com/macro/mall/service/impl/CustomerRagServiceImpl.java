package com.macro.mall.service.impl;

import com.macro.mall.dto.CustomerChatResult;
import com.macro.mall.service.CustomerRagService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(name = "easygoing.ai.enabled", havingValue = "true")
public class CustomerRagServiceImpl implements CustomerRagService {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public CustomerRagServiceImpl(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    @Override
    public CustomerChatResult answer(String question) {
        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder().query(question).topK(4).build());
        String context = documents.stream().map(Document::getText).reduce("", (left, right) -> left + "\n---\n" + right);
        String answer = chatClient.prompt()
                .system("你是易going商城的智能客服。只能依据给定的知识库内容回答；信息不足时请明确说明并建议人工客服跟进。回答简明、友好、使用中文。\n知识库内容：\n" + context)
                .user(question)
                .call()
                .content();
        List<String> sources = documents.stream()
                .map(document -> String.valueOf(document.getMetadata().getOrDefault("source", "易going知识库")))
                .distinct()
                .toList();
        return new CustomerChatResult(answer, sources);
    }
}
