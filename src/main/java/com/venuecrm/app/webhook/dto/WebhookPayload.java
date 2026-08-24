package com.venuecrm.app.webhook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookPayload(List<Entry> entry) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Entry(String id, List<Change> changes) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Change(Value value, String field) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Value(
            Metadata metadata,
            List<Contact> contacts,
            List<Message> messages
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Metadata(
            @JsonProperty("display_phone_number") String displayPhoneNumber,
            @JsonProperty("phone_number_id") String phoneNumberId
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Contact(
            Profile profile,
            @JsonProperty("wa_id") String waId,
            @JsonProperty("user_id") String userId
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Profile(String name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Message(
            String from,
            @JsonProperty("from_user_id") String fromUserId,
            String id,
            String timestamp,
            String type,
            Text text,
            Interactive interactive
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Text(String body) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Interactive(
            String type,
            @JsonProperty("button_reply") ButtonReply buttonReply,
            @JsonProperty("list_reply") ListReply listReply
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ButtonReply(String id, String title) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ListReply(String id, String title) {}
}
