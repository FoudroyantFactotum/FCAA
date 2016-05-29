package com.foudroyantfactotum.mod.fousarchive.utility;

import gnu.trove.TIntCollection;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.queue.TIntQueue;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;
import java.util.NoSuchElementException;

public class TIntQueueF implements TIntQueue
{
    private int head;
    private int[] queue;

    public TIntQueueF()
    {
        this(10);
    }

    public TIntQueueF(int size)
    {
        head = -1;
        queue = new int[size];
    }

    @Override
    public int element()
    {
        if (head > 0)
            throw new NoSuchElementException();

        return queue[head--];
    }

    @Override
    public boolean offer(int e)
    {
        if (head >= queue.length)
        {
            final int[] nq = new int[queue.length*2];
            System.arraycopy(queue, 0, nq, 0, queue.length);
            queue = nq;
        }

        queue[++head] = e;

        return true;
    }

    @Override
    public int peek()
    {
        if (head < 0)
            return -1;

        return queue[head];
    }

    @Override
    public int poll()
    {
        if (head < 0)
            return -1;

        return queue[head--];
    }

    @Override
    public int getNoEntryValue()
    {
        return -1;
    }

    @Override
    public int size()
    {
        return head;
    }

    @Override
    public boolean isEmpty()
    {
        return head < 0;
    }

    @Override
    public boolean contains(int entry)
    {
        for (int i = 0; i <= head; ++i)
            if (queue[i] == entry)
                return true;

        return false;
    }

    @Override
    public TIntIterator iterator()
    {
        throw new NotImplementedException();
    }

    @Override
    public int[] toArray()
    {
        if (head < 1)
            return new int[0];

        final int[] rq = new int[head];
        System.arraycopy(queue, 0, rq, 0, head);

        return rq;
    }

    @Override
    public int[] toArray(int[] dest)
    {
        throw new NotImplementedException();
    }

    @Override
    public boolean add(int entry)
    {
        throw new NotImplementedException();
    }

    @Override
    public boolean remove(int entry)
    {
        throw new NotImplementedException();
    }

    @Override
    public boolean containsAll(Collection<?> collection)
    {
        throw new NotImplementedException();
    }

    @Override
    public boolean containsAll(TIntCollection collection)
    {
        throw new NotImplementedException();
    }

    @Override
    public boolean containsAll(int[] array)
    {
        throw new NotImplementedException();
    }

    @Override
    public boolean addAll(Collection<? extends Integer> collection)
    {
        throw new NotImplementedException();
    }

    @Override
    public boolean addAll(TIntCollection collection)
    {
        throw new NotImplementedException();
    }

    @Override
    public boolean addAll(int[] array)
    {
        throw new NotImplementedException();
    }

    @Override
    public boolean retainAll(Collection<?> collection)
    {
        throw new NotImplementedException();
    }

    @Override
    public boolean retainAll(TIntCollection collection)
    {
        throw new NotImplementedException();
    }

    @Override
    public boolean retainAll(int[] array)
    {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeAll(Collection<?> collection)
    {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeAll(TIntCollection collection)
    {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeAll(int[] array)
    {
        throw new NotImplementedException();
    }

    @Override
    public void clear()
    {
        head = -1;
    }

    @Override
    public boolean forEach(TIntProcedure procedure)
    {
        for (int i = 0; i <= head; ++i)
        {
            if (!procedure.execute(queue[i]))
                return false;
        }

        return true;
    }
}
