package net.metrosystems.inventoryaggregator.config.intercept;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
public class GeneralIntercept implements HandlerInterceptor {
    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {


        if (request.getHeader("traceId") != null) {
            UUID uuid = UUID.randomUUID();
            MDC.put("transactionId", uuid.toString());
            MDC.put("traceId", request.getHeader("traceId"));
            MDC.put("Flow", request.getHeader("Flow"));

        }
        return true;
    }


    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView) throws Exception {
        MDC.clear();
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
