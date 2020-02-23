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
public class LivenessCheckController {
	@Autowired	
	private LivenessCheck livenessCheckService;	
	
	@RequestMapping(value = "/.well-known/live", method = { RequestMethod.GET, RequestMethod.HEAD })	
	public ResponseEntity<String> live(HttpServletRequest request, HttpServletResponse response) {		
		ResponseEntity<String> re = null;		
		boolean live = livenessCheckService.isLive();		
		if (live) {			
			re = new ResponseEntity<>(HttpStatus.NO_CONTENT);		
		} else {			
			re = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);		
		}		
		return re;	
	}
}


