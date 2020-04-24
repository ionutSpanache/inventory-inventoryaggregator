package net.metrosystems.inventoryaggregator.readinessandliveness;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class LivenessCheckController {

    private static final ResponseEntity<String> reOK = new ResponseEntity<>(HttpStatus.OK);

    @Hidden
    @RequestMapping(value = { "/.well-known/live" }, method = { RequestMethod.GET, RequestMethod.HEAD })
    public ResponseEntity<String> live(HttpServletRequest request, HttpServletResponse response) {
        return reOK;
    }
    @Hidden
    @RequestMapping(value = { "/.well-known/ready" }, method = { RequestMethod.GET, RequestMethod.HEAD })
    public ResponseEntity<String> ready(HttpServletRequest request, HttpServletResponse response) {
        return reOK;
    }
}

