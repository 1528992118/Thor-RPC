package org.cxl.thor.rpc.core.loadbalance;

import com.google.common.collect.Lists;
import org.cxl.thor.rpc.common.exception.ThorException;
import org.cxl.thor.rpc.register.LoadBalance;

import java.util.Random;
import java.util.Set;

import static org.cxl.thor.rpc.common.exception.ThorException.NO_PROVIDER_ERROR;

public class RandomLoadBalance implements LoadBalance {

    private Random random = new Random();

    @Override
    public String select(Set<String> providers) {
        if (null == providers || providers.size() == 0) {
            throw new ThorException(NO_PROVIDER_ERROR, "no provider!");
        }
        return Lists.newArrayList(providers).get(random.nextInt(providers.size()));
    }

}
