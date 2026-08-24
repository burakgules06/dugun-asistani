package com.venuecrm.app.model.entity;

import com.venuecrm.app.model.enums.ConversationState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "wa_conversation")
@Getter
@Setter
public class WaConversation extends BaseModel {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "state", nullable = false, columnDefinition = "conversation_state")
    @Enumerated(EnumType.STRING)
    private ConversationState state = ConversationState.IDLE;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "context", nullable = false, columnDefinition = "jsonb")
    private String context = "{}";

    @Column(name = "last_inbound_at")
    private OffsetDateTime lastInboundAt;
}
