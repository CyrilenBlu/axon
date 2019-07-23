package blu.eurekafeignclient;

import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
@EnableFeignClients
@EnableEurekaClient
public class EurekaFeignClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaFeignClientApplication.class, args);
    }

}

@FeignClient("spring-cloud-eureka-client")
interface PersonInterface {
    @RequestMapping("/greeting")
    String greeting();
}

@Controller
@AllArgsConstructor
class PersonEndpoint {

    private PersonInterface personInterface;

    @RequestMapping("/get-greeting")
    public String greeting(Model model) {
        model.addAttribute("greeting", personInterface.greeting());
        return "greeting";
    }
}
