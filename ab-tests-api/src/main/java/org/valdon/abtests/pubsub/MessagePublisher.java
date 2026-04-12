package org.valdon.abtests.pubsub;

import org.valdon.abtests.dto.user.UserCreatedEvent;

public interface MessagePublisher {

    void publishUserEvent(UserCreatedEvent message);

}
