CREATE SEQUENCE IF NOT EXISTS chat_messages_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS chat_messages (
    id           BIGINT NOT NULL DEFAULT nextval('chat_messages_seq'),
    sender_id    BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    content      TEXT NOT NULL,
    timestamp    TIMESTAMP(6) NOT NULL,
    CONSTRAINT pk_chat_messages PRIMARY KEY (id),
    CONSTRAINT fk_chat_messages_sender FOREIGN KEY (sender_id) REFERENCES users (user_id),
    CONSTRAINT fk_chat_messages_recipient FOREIGN KEY (recipient_id) REFERENCES users (user_id)
);

CREATE INDEX IF NOT EXISTS idx_chat_messages_conversation
    ON chat_messages (sender_id, recipient_id, timestamp);
