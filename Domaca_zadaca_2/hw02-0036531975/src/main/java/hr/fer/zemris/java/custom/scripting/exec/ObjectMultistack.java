package hr.fer.zemris.java.custom.scripting.exec;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;

public class ObjectMultistack {
    private Map<String, MultistackEntry> multistacksMap = new HashMap<>();
    public static class MultistackEntry {
        private ValueWrapper object;
        private MultistackEntry next = null;
        public MultistackEntry(ValueWrapper object) {
            this.object = object;
        }

        public void setObject(ValueWrapper object) {
            this.object = object;
        }

        public void setNext(MultistackEntry next) {
            this.next = next;
        }
    }

    public void push(String keyName, ValueWrapper valueWrapper) {

        if (!multistacksMap.containsKey(keyName)) {
            multistacksMap.put(keyName, new MultistackEntry(valueWrapper));
            return;
        }

        MultistackEntry entry = multistacksMap.get(keyName);

        MultistackEntry newEntry = new MultistackEntry(valueWrapper);

        newEntry.next = entry;

        multistacksMap.put(keyName, newEntry);
    }

    public ValueWrapper pop(String keyName) {

        MultistackEntry entry = multistacksMap.get(keyName);

        if (entry == null) throw new EmptyStackException();

        ValueWrapper valueWrapper = entry.object;

        multistacksMap.put(keyName, entry.next);

        return valueWrapper;
    }

    public ValueWrapper peek(String keyName) {

        MultistackEntry entry = multistacksMap.get(keyName);

        if (entry == null) throw new EmptyStackException();

        return entry.object;
    }

    public boolean isEmpty(String keyName) {
        return multistacksMap.get(keyName) == null;
    }
}
