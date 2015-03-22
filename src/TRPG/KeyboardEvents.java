package TRPG;

import java.util.ArrayList;
import java.util.HashMap;
import org.lwjgl.input.Keyboard;

public class KeyboardEvents {
    public static interface Listener {
        public void onKeyChange(int key, boolean state);
    }

    private static HashMap<Integer, ArrayList<Listener>> keyMap = new HashMap<>();
    public static HashMap<Integer, Boolean> keyState = new HashMap<>();

    public static void pollInput() {
        while (Keyboard.next()) {
            int key = Keyboard.getEventKey();

            if (keyMap.containsKey(key)) {
                boolean state = Keyboard.getEventKeyState();
                keyState.put(key, state);
                for (Listener listener : keyMap.get(key)) {
                    listener.onKeyChange(key, state);
                }
            }
        }
    }

    public static void listenTo(int[] keys, Listener listener) {
        for (int key : keys) {
            ArrayList<Listener> listeners;
            if (keyMap.containsKey(key)) {
                listeners = keyMap.get(key);
            } else {
                keyMap.put(key, listeners = new ArrayList<>());
                keyState.put(key, Keyboard.isKeyDown(key));
            }

            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }
    }

    public static void unlistenTo(int[] keys, Listener listener) {
        for (int key : keys) {
            if (keyMap.containsKey(key)) {
                ArrayList<Listener> listeners = keyMap.get(key);
                if (listeners.size() == 1) {
                    keyMap.remove(key);
                    keyState.remove(key);
                } else {
                    listeners.remove(listener);
                }
            }
        }
    }
}
