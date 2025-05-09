package com.cognito.config.utilities;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cognito.pkce.state.PKCEState;

public abstract class PKCEStore {

	private static final Map<String, PKCEState> store = new ConcurrentHashMap<>();

    public static void put(String state, PKCEState pkceEStore) {
        store.put(state, pkceEStore);
    }

    public static PKCEState consume(String state) {
        return store.remove(state); // remove to prevent reuse
    }
}
