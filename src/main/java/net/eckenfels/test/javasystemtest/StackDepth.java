package net.eckenfels.test.javasystemtest;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StackDepth
{
    private static final int REPEAT = 5;

    private final String name;
    private final int privDepth;

    private int depth = 0;

    public StackDepth(String name, int pri)
    {
        this.name = name;
        this.privDepth = pri;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException
    {
        ExecutorService ex = Executors.newFixedThreadPool(1);
        for(int i=0; i < REPEAT;i++)
        {
            new StackDepth("main thread #" + i,0).test();
        }

        for(int i=0; i < REPEAT;i++)
        {
            new StackDepth("main thread priv1000 #" + i,1000).test();
        }

        for(int i=0; i < REPEAT;i++)
        {
            final int n = i;
            ex.submit(() -> new StackDepth("pooled thread #"+n, 0).test()).get();
        }

        for(int i=0; i < REPEAT;i++)
        {
            final int n = i;
            ex.submit(() -> new StackDepth("pooled thread priv500 #"+n, 500).test()).get();
        }

        for(int i=0; i < REPEAT;i++)
        {
            final int n = i;
            ex.submit(() -> new StackDepth("pooled thread priv1000 #"+n, 1000).test()).get();
        }

        ex.shutdown();
    }

    private void test()
    {
        try {
            nested();
        } catch (StackOverflowError so)
        {
            System.out.println("Overflow in " + name + " after " + depth + ": " + so);
        }
    }

    private void nested()
    {
        depth++;
        if (depth == privDepth)
            AccessController.doPrivileged((PrivilegedAction<Object>)() -> {nested(); return null;});
        else
            nested();
    }

}

/* Result 8u77 x64 win10

Overflow in main thread #0 after 14797: java.lang.StackOverflowError
Overflow in main thread #1 after 41838: java.lang.StackOverflowError
Overflow in main thread #2 after 41838: java.lang.StackOverflowError
Overflow in main thread #3 after 41838: java.lang.StackOverflowError
Overflow in main thread #4 after 41838: java.lang.StackOverflowError
Overflow in main thread priv1000 #0 after 12100: java.lang.StackOverflowError
Overflow in main thread priv1000 #1 after 5687: java.lang.StackOverflowError
Overflow in main thread priv1000 #2 after 62558: java.lang.StackOverflowError
Overflow in main thread priv1000 #3 after 62558: java.lang.StackOverflowError
Overflow in main thread priv1000 #4 after 62558: java.lang.StackOverflowError
Overflow in pooled thread #0 after 62700: java.lang.StackOverflowError
Overflow in pooled thread #1 after 62700: java.lang.StackOverflowError
Overflow in pooled thread #2 after 62700: java.lang.StackOverflowError
Overflow in pooled thread #3 after 62700: java.lang.StackOverflowError
Overflow in pooled thread #4 after 62700: java.lang.StackOverflowError
Overflow in pooled thread priv500 #0 after 62500: java.lang.StackOverflowError
Overflow in pooled thread priv500 #1 after 62500: java.lang.StackOverflowError
Overflow in pooled thread priv500 #2 after 62500: java.lang.StackOverflowError
Overflow in pooled thread priv500 #3 after 62500: java.lang.StackOverflowError
Overflow in pooled thread priv500 #4 after 62500: java.lang.StackOverflowError
Overflow in pooled thread priv1000 #0 after 62500: java.lang.StackOverflowError
Overflow in pooled thread priv1000 #1 after 62500: java.lang.StackOverflowError
Overflow in pooled thread priv1000 #2 after 62500: java.lang.StackOverflowError
Overflow in pooled thread priv1000 #3 after 62500: java.lang.StackOverflowError
Overflow in pooled thread priv1000 #4 after 62500: java.lang.StackOverflowError

*/
