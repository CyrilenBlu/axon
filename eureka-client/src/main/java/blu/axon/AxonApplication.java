package blu.axon;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateLifecycle;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.responsetypes.ResponseTypes;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@SpringBootApplication
public class AxonApplication {
    public static void main(String[] args) {
        SpringApplication.run(AxonApplication.class, args);
    }
}

@AllArgsConstructor
@Data
class BillPayedCommand {
    private final String userId;
    private final String name;
}

@AllArgsConstructor
@Data
class BillPayedEvent {
    private final String userId;
    private final String name;
}

@Aggregate
class PersonAggregate {

    @AggregateIdentifier
    private String userId;
    private String name;

    @CommandHandler
    public PersonAggregate(BillPayedCommand command) {
        AggregateLifecycle
                .apply(new BillPayedEvent(command.getUserId(), command.getName()));
    }

    @EventSourcingHandler
    public void on(BillPayedEvent event) {
        this.userId = event.getUserId();
    }

    protected PersonAggregate() { }
}

@AllArgsConstructor
@Data
class Person {
    private final String userId;
    private final String name;
}

@Service
class PersonEventHandler {

    private final Map<String, Person> people = new HashMap<>();

    @EventHandler
    public void on(BillPayedEvent event) {
        String userId = event.getUserId();
        people.put(userId, new Person(userId, event.getName()));
    }

    @QueryHandler
    public List<Person> handle(FindAllPeopleQuery query) {
        return new ArrayList<>(people.values());
    }
}

class FindAllPeopleQuery { }

@AllArgsConstructor
@RestController
class PersonEndpoint {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @GetMapping("/people")
    public List<Person> findAllPeople() {
        return queryGateway.query(new FindAllPeopleQuery(),
                ResponseTypes.multipleInstancesOf(Person.class)).join();
    }

    @PostMapping("/{name}/pay")
    public void payBills(@PathVariable String name) {
        String userId = UUID.randomUUID().toString();
        commandGateway.send(new BillPayedCommand(userId, name));
    }
}
