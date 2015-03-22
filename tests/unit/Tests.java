package unit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    unit.TRPG.Main_test.class,
    unit.TRPG.KeyboardEvents_test.class
})
public class Tests {}
