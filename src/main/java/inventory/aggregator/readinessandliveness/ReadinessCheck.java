package inventory.aggregator.readinessandliveness;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReadinessCheck {
	
	private static final Logger LOG = LoggerFactory.getLogger(ReadinessCheck.class);
	
	private boolean ready = true;
	
	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		LOG.info("Set ready to {}", ready);
		this.ready = ready;
	}
}