CREATE TABLE IF NOT EXISTS users
(
    id    INT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                         NOT NULL,
    email VARCHAR(512)                         NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    id          INT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    owner_id    INT                                  NOT NULL,
    name        VARCHAR(255)                         NOT NULL,
    description VARCHAR(1024)                        NOT NULL,
    available   BOOLEAN                              NOT NULL,
    CONSTRAINT pk_item PRIMARY KEY (id),
    FOREIGN KEY (owner_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         INT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_time TIMESTAMP WITHOUT TIME ZONE          NOT NULL,
    end_time   TIMESTAMP WITHOUT TIME ZONE          NOT NULL,
    item_id    INT                                  NOT NULL,
    booker_id  INT                                  NOT NULL,
    status     VARCHAR(20)                          NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY (id),
    FOREIGN KEY (item_id) REFERENCES items (id),
    FOREIGN KEY (booker_id) REFERENCES users (id)
)


