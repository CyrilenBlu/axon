package blu.axon;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class PersonAggregateTest {

    AggregateTestFixture fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new AggregateTestFixture<>(PersonAggregate.class);
    }

    @Test
    public void testAggregate() {
        String userId = UUID.randomUUID().toString();
        String name = "Luke";

        fixture.givenNoPriorActivity()
                .when(new BillPayedCommand(userId, name))
                .expectEvents(new BillPayedEvent(userId, name));
    }
}
