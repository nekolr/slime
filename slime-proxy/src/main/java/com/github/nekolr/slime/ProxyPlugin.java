package com.github.nekolr.slime;

import com.github.nekolr.slime.model.Plugin;
import com.github.nekolr.slime.support.Pluggable;
import org.springframework.stereotype.Component;

@Component
public class ProxyPlugin implements Pluggable {

    @Override
    public Plugin plugin() {
        return new Plugin("代理管理", "proxy.html");
    }
}
