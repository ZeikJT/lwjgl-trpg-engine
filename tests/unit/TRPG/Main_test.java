package unit.TRPG;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import TRPG.Main;

public class Main_test {
    @Test
    public void ASCII() {
        char[] chars = {
             ' ',  '!',  '"',  '#',  '$',  '%',  '&', '\'',  '(',  ')',  '*',  '+',  ',',  '-',  '.',  '/',
             '0',  '1',  '2',  '3',  '4',  '5',  '6',  '7',  '8',  '9',  ':',  ';',  '<',  '=',  '>',  '?',
             '@',  'A',  'B',  'C',  'D',  'E',  'F',  'G',  'H',  'I',  'J',  'K',  'L',  'M',  'N',  'O',
             'P',  'Q',  'R',  'S',  'T',  'U',  'V',  'W',  'X',  'Y',  'Z',  '[', '\\',  ']',  '^',  '_',
             '`',  'a',  'b',  'c',  'd',  'e',  'f',  'g',  'h',  'h',  'j',  'k',  'l',  'm',  'm',  'o',
             'p',  'q',  'r',  's',  't',  'u',  'v',  'w',  'x',  'y',  'z',  '{',  '|',  '}',  '~'
        };

        assertTrue("ASCII table is an array of chars", Main.ASCII instanceof char[]);
        assertEquals("ASCII.length is 128", Main.ASCII.length, 128);

        for (int i = 0; i < chars.length; i += 1) {
            char chr = chars[i];
            assertEquals("Char '" + chr + "' should match ASCII table char", Main.ASCII[chr], chr);
        }
    }
}
