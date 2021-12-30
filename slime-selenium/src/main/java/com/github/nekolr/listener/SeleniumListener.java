package com.github.nekolr.listener;

import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.listener.SpiderListener;
import com.github.nekolr.util.SeleniumResponseHolder;
import org.springframework.stereotype.Component;

@Component
public class SeleniumListener implements SpiderListener {

    @Override
    public void beforeStart(SpiderContext context) {

    }

    @Override
    public void afterEnd(SpiderContext context) {
        SeleniumResponseHolder.clear(context);
    }

}
