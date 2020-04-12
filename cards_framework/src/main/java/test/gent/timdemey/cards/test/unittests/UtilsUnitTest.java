package gent.timdemey.cards.test.unittests;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.test.common.TestBase;
import gent.timdemey.cards.test.helpers.CardStackHelper;

public class UtilsUnitTest extends TestBase
{
    @BeforeClass
    public static void init()
    {
        installMockCardPlugin();
        installMockContextService();
    }

    /**
     * Prints a new int[][]{ ... } array. The output can be used to
     * always shuffle a sorted deck into the same order, e.g. for tests.
     */
    @Test
    @Ignore
    public void printRandomShuffle()
    {
        List<Integer> range = IntStream.rangeClosed(0, 51).boxed().collect(Collectors.toList());
        Collections.shuffle(range);
        
        // new int [][] { { x, y }, ...
        String[] parts = new String[26];
        for (int i = 0; i < 52; i += 2)
        {
            int nr1 = range.get(i);
            int nr2 = range.get(i+1);
            parts[i / 2] = String.format("{ %s, %s }", nr1, nr2);
        }
        String arrayElems = String.join(", ", parts); 

        String full = String.format("new int[][] { %s };", arrayElems);
        System.out.println(full);
    }

    @Test
    public void printCardStack ()
    {
        CardStack cs = CardStackHelper.createCardStack();
        System.out.println(cs.toString());
    }
}
