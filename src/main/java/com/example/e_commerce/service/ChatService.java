package com.example.e_commerce.service;

import com.example.e_commerce.dto.chat.ChatMessageRequest;
import com.example.e_commerce.dto.chat.ChatMessageResponse;
import com.example.e_commerce.entity.user.ChatMessage;
import com.example.e_commerce.entity.user.User;
import com.example.e_commerce.exception.UserNotFoundException;
import com.example.e_commerce.repository.ChatMessageRepository;
import com.example.e_commerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatMessageResponse saveFromCurrentUser(ChatMessageRequest request) {
        User sender = getCurrentUser();
        User recipient = userRepository.findById(request.recipientId())
                .orElseThrow(() -> new UserNotFoundException("Recipient not found"));

        ChatMessage message = new ChatMessage();
        message.setSenderId(sender.getUserId());
        message.setRecipientId(recipient.getUserId());
        message.setContent(request.content().trim());
        message.setTimestamp(LocalDateTime.now());

        return toResponse(chatMessageRepository.save(message), sender, recipient);
    }

    @Transactional
    public ChatMessageResponse saveFromUsername(String username, ChatMessageRequest request) {
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Sender not found"));
        User recipient = userRepository.findById(request.recipientId())
                .orElseThrow(() -> new UserNotFoundException("Recipient not found"));

        ChatMessage message = new ChatMessage();
        message.setSenderId(sender.getUserId());
        message.setRecipientId(recipient.getUserId());
        message.setContent(request.content().trim());
        message.setTimestamp(LocalDateTime.now());

        return toResponse(chatMessageRepository.save(message), sender, recipient);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getConversation(Long otherUserId) {
        User currentUser = getCurrentUser();
        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return chatMessageRepository.findBySenderIdAndRecipientIdOrSenderIdAndRecipientIdOrderByTimestampAsc(
                        currentUser.getUserId(), otherUser.getUserId(),
                        otherUser.getUserId(), currentUser.getUserId()
                ).stream()
                .map(message -> toResponse(message, currentUser, otherUser))
                .toList();
    }

    public String conversationTopic(Long userId1, Long userId2) {
        long first = Math.min(userId1, userId2);
        long second = Math.max(userId1, userId2);
        return "/topic/chat/" + first + "-" + second;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found " + username));
    }

    private ChatMessageResponse toResponse(ChatMessage message, User userA, User userB) {
        User sender = message.getSenderId().equals(userA.getUserId()) ? userA : userB;
        User recipient = message.getRecipientId().equals(userA.getUserId()) ? userA : userB;

        return new ChatMessageResponse(
                message.getId(),
                message.getSenderId(),
                sender.getUsername(),
                message.getRecipientId(),
                recipient.getUsername(),
                message.getContent(),
                message.getTimestamp()
        );
    }
}
