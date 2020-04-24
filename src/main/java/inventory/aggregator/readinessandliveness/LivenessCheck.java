package inventory.aggregator.readinessandliveness;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LivenessCheck {
	
	private static final Logger LOG = LoggerFactory.getLogger(LivenessCheck.class);
	
	private boolean live = true;
	
	public boolean isLive() {
		return live;
	}
	
	public void setLive(boolean live) {
		this.live = live;
	}
	
}