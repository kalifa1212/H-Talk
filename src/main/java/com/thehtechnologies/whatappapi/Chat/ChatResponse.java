package com.thehtechnologies.whatappapi.Chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatResponse {

    private String id;
    private String name;
    private long unreadCount;
    private String lastMessage;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastMessageTime;
    private boolean isRecipientOnline;
    private String senderId;
    private String receiverId;
}
