package inventory.aggregator.readinessandliveness;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class ReadinessCheckController {
	// private static final Logger LOG = LoggerFactory.getLogger(ReadinessCheckController.class);	
	@Autowired	private ReadinessCheck readinessCheckService;	
	
	@RequestMapping(value = { "/.well-known/ready" }, method = { RequestMethod.GET, RequestMethod.HEAD })	
	public ResponseEntity<String> live(HttpServletRequest request, HttpServletResponse response) {		
		ResponseEntity<String> re = null;		
		boolean ready = readinessCheckService.isReady();		
		if (ready) {			
			re = new ResponseEntity<>(HttpStatus.NO_CONTENT);		
		} else {			
			re = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);		
		}		
		return re;	
	}
}
