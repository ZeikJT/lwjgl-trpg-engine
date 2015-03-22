package unit.TRPG;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.lwjgl.input.Keyboard;
import TRPG.KeyboardEvents;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Keyboard.class)
public class KeyboardEvents_test {
    private class TestListener implements KeyboardEvents.Listener {
        public void onKeyChange(int key, boolean state) {}
    }

    private TestListener listener;

    @Before
    public void beforeEach() {
        PowerMockito.mockStatic(Keyboard.class);
        listener = spy(new TestListener());
    }

    @Test
    public void listenTo() {
        KeyboardEvents.listenTo(new int[]{Keyboard.KEY_A}, listener);

        when(Keyboard.next()).thenReturn(true, false);
        when(Keyboard.getEventKey()).thenReturn(Keyboard.KEY_A);
        when(Keyboard.getEventKeyState()).thenReturn(true);

        KeyboardEvents.pollInput();
        verify(listener).onKeyChange(Keyboard.KEY_A, true);
    }

    @Test
    public void unlistenTo() {
        KeyboardEvents.listenTo(new int[]{Keyboard.KEY_A}, listener);
        KeyboardEvents.unlistenTo(new int[]{Keyboard.KEY_A}, listener);

        when(Keyboard.next()).thenReturn(true, false);
        when(Keyboard.getEventKey()).thenReturn(Keyboard.KEY_A);
        when(Keyboard.getEventKeyState()).thenReturn(true);

        KeyboardEvents.pollInput();
        verify(listener, never()).onKeyChange(Keyboard.KEY_A, true);
    }
}
