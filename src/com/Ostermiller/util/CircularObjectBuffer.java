/*
 * Circular Object Buffer
 * Copyright (C) 2002-2010 Stephen Ostermiller
 * http://ostermiller.org/contact.pl?regarding=Java+Utilities
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * See LICENSE.txt for details.
 */
package com.Ostermiller.util;
 
/**
 * Implements the Circular Buffer producer/consumer model for Objects.
 * More information about this class is available from <a target="_top" href=
 * "http://ostermiller.org/utils/CircularObjectBuffer.html">ostermiller.org</a>.
 * <p>
 * This class is thread safe.
 *
 * @see CircularCharBuffer
 * @see CircularByteBuffer
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @param <ElementType> Type of object allowed in this circular buffer
 * @since ostermillerutils 1.00.00
 */
public class CircularObjectBuffer <ElementType> {
 
    /**
     * The default size for a circular object buffer.
     *
     * @since ostermillerutils 1.00.00
     */
    private final static int DEFAULT_SIZE = 1024;
 
    /**
     * A buffer that will grow as things are added.
     *
     * @since ostermillerutils 1.00.00
     */
    public final static int INFINITE_SIZE = -1;
 
    /**
     * The circular buffer.
     * <p>
     * The actual capacity of the buffer is one less than the actual length
     * of the buffer so that an empty and a full buffer can be
     * distinguished.  An empty buffer will have the readPostion and the
     * writePosition equal to each other.  A full buffer will have
     * the writePosition one less than the readPostion.
     * <p>
     * There are two important indexes into the buffer:
     * The readPosition, and the writePosition. The Objects
     * available to be read go from the readPosition to the writePosition,
     * wrapping around the end of the buffer.  The space available for writing
     * goes from the write position to one less than the readPosition,
     * wrapping around the end of the buffer.
     *
     * @since ostermillerutils 1.00.00
     */
    protected ElementType[] buffer;
    /**
     * Index of the first Object available to be read.
     *
     * @since ostermillerutils 1.00.00
     */
    protected volatile int readPosition = 0;
    /**
     * Index of the first Object available to be written.
     *
     * @since ostermillerutils 1.00.00
     */
    protected volatile int writePosition = 0;
    /**
     * If this buffer is infinite (should resize itself when full)
     *
     * @since ostermillerutils 1.00.00
     */
    protected volatile boolean infinite = false;
    /**
     * True if a write to a full buffer should block until the buffer
     * has room, false if the write method should throw an IOException
     *
     * @since ostermillerutils 1.00.00
     */
    protected boolean blockingWrite = true;
 
    /**
     * True when no more input is coming into this buffer.  At that
     * point reading from the buffer may return  null if the buffer
     * is empty, otherwise a read will block until an Object is available.
     *
     * @since ostermillerutils 1.00.00
     */
    protected boolean inputDone = false;
 
    /**
     * Make this buffer ready for reuse.  The contents of the buffer
     * will be cleared and the streams associated with this buffer
     * will be reopened if they had been closed.
     *
     * @since ostermillerutils 1.00.00
     */
    public void clear(){
        synchronized (this){
            readPosition = 0;
            writePosition = 0;
            inputDone = false;
        }
    }
 
    /**
     * Get number of Objects that are available to be read.
     * <p>
     * Note that the number of Objects available plus
     * the number of Objects free may not add up to the
     * capacity of this buffer, as the buffer may reserve some
     * space for other purposes.
     *
     * @return the size in Objects of this buffer
     *
     * @since ostermillerutils 1.00.00
     */
    public int getAvailable(){
        synchronized (this){
            return available();
        }
    }
 
    /**
     * Get the number of Objects this buffer has free for
     * writing.
     * <p>
     * Note that the number of Objects available plus
     * the number of Objects free may not add up to the
     * capacity of this buffer, as the buffer may reserve some
     * space for other purposes.
     *
     * @return the available space in Objects of this buffer
     *
     * @since ostermillerutils 1.00.00
     */
    public int getSpaceLeft(){
        synchronized (this){
            return spaceLeft();
        }
    }
 
    /**
     * Get the capacity of this buffer.
     * <p>
     * Note that the number of Objects available plus
     * the number of Objects free may not add up to the
     * capacity of this buffer, as the buffer may reserve some
     * space for other purposes.
     *
     * @return the size in Objects of this buffer
     *
     * @since ostermillerutils 1.00.00
     */
    public int getSize(){
        synchronized (this){
            return buffer.length;
        }
    }
 
    @SuppressWarnings("unchecked") private ElementType[] createArray(int size){
        return (ElementType[]) new Object[size];
    }
 
    /**
     * double the size of the buffer
     *
     * @since ostermillerutils 1.00.00
     */
    private void resize(){
        ElementType[] newBuffer = createArray(buffer.length * 2);
        int available = available();
        if (readPosition <= writePosition){
            // any space between the read and
            // the first write needs to be saved.
            // In this case it is all in one piece.
            int length = writePosition - readPosition;
            System.arraycopy(buffer, readPosition, newBuffer, 0, length);
        } else {
            int length1 = buffer.length - readPosition;
            System.arraycopy(buffer, readPosition, newBuffer, 0, length1);
            int length2 = writePosition;
            System.arraycopy(buffer, 0, newBuffer, length1, length2);
        }
        buffer = newBuffer;
        readPosition = 0;
        writePosition = available;
    }
 
    /**
     * Space available in the buffer which can be written.
     *
     * @since ostermillerutils 1.00.00
     */
    private int spaceLeft(){
        if (writePosition < readPosition){
            // any space between the first write and
            // the read except one Object is available.
            // In this case it is all in one piece.
            return (readPosition - writePosition - 1);
        }
        // space at the beginning and end.
        return ((buffer.length - 1) - (writePosition - readPosition));
    }
 
    /**
     * Objects available for reading.
     *
     * @since ostermillerutils 1.00.00
     */
    private int available(){
        if (readPosition <= writePosition){
            // any space between the first read and
            // the first write is available.  In this case i
            // is all in one piece.
            return (writePosition - readPosition);
        }
        // space at the beginning and end.
        return (buffer.length - (readPosition - writePosition));
    }
 
    /**
     * Create a new buffer with a default capacity.
     * Writing to a full buffer will block until space
     * is available rather than throw an exception.
     *
     * @since ostermillerutils 1.00.00
     */
    public CircularObjectBuffer(){
        this (DEFAULT_SIZE, true);
    }
 
    /**
     * Create a new buffer with given capacity.
     * Writing to a full buffer will block until space
     * is available rather than throw an exception.
     * <p>
     * Note that the buffer may reserve some Objects for
     * special purposes and capacity number of Objects may
     * not be able to be written to the buffer.
     * <p>
     * Note that if the buffer is of INFINITE_SIZE it will
     * neither block or throw exceptions, but rather grow
     * without bound.
     *
     * @param size desired capacity of the buffer in Objects or CircularObjectBuffer.INFINITE_SIZE.
     *
     * @since ostermillerutils 1.00.00
     */
    public CircularObjectBuffer(int size){
        this (size, true);
    }
 
    /**
     * Create a new buffer with a default capacity and
     * given blocking behavior.
     *
     * @param blockingWrite true writing to a full buffer should block
     *        until space is available, false if an exception should
     *        be thrown instead.
     *
     * @since ostermillerutils 1.00.00
     */
    public CircularObjectBuffer(boolean blockingWrite){
        this (DEFAULT_SIZE, blockingWrite);
    }
 
    /**
     * Create a new buffer with the given capacity and
     * blocking behavior.
     * <p>
     * Note that the buffer may reserve some Objects for
     * special purposes and capacity number of Objects may
     * not be able to be written to the buffer.
     * <p>
     * Note that if the buffer is of INFINITE_SIZE it will
     * neither block or throw exceptions, but rather grow
     * without bound.
     *
     * @param size desired capacity of the buffer in Objects or CircularObjectBuffer.INFINITE_SIZE.
     * @param blockingWrite true writing to a full buffer should block
     *        until space is available, false if an exception should
     *        be thrown instead.
     *
     * @since ostermillerutils 1.00.00
     */
    public CircularObjectBuffer(int size, boolean blockingWrite){
        if (size == INFINITE_SIZE){
            buffer = createArray(DEFAULT_SIZE);
            infinite = true;
        } else {
            buffer = createArray(size);
            infinite = false;
        }
        this.blockingWrite = blockingWrite;
    }
 
 
    /**
     * Get a single Object from this buffer.  This method should be called
     * by the consumer.
     * This method will block until a Object is available or no more
     * objects are available.
     *
     * @return The Object read, or null if there are no more objects
     * @throws InterruptedException if the thread is interrupted while waiting.
     *
     * @since ostermillerutils 1.00.00
     */
    public ElementType read() throws InterruptedException {
        while (true){
            synchronized (this){
                int available = available();
                if (available > 0){
                    ElementType result = buffer[readPosition];
                    readPosition++;
                    if (readPosition == buffer.length){
                        readPosition = 0;
                    }
                    return result;
                } else if (inputDone){
                    return null;
                }
            }
            Thread.sleep(100);
        }
    }
 
    /**
     * Get Objects into an array from this buffer.  This method should
     * be called by the consumer.
     * This method will block until some input is available,
     * or there is no more input.
     *
     * @param buf Destination buffer.
     * @return The number of Objects read, or -1 there will
     *     be no more objects available.
     * @throws InterruptedException if the thread is interrupted while waiting.
     *
     * @since ostermillerutils 1.00.00
     */
    public int read(ElementType[] buf) throws InterruptedException {
        return read(buf, 0, buf.length);
    }
 
    /**
     * Get Objects into a portion of an array from this buffer.  This
     * method should be called by the consumer.
     * This method will block until some input is available,
     * an I/O error occurs, or the end of the stream is reached.
     *
     * @param buf Destination buffer.
     * @param off Offset at which to start storing Objects.
     * @param len Maximum number of Objects to read.
     * @return The number of Objects read, or -1 there will
     *     be no more objects available.
     * @throws InterruptedException if the thread is interrupted while waiting.
     *
     * @since ostermillerutils 1.00.00
     */
    public int read(ElementType[] buf, int off, int len) throws InterruptedException {
        while (true){
            synchronized (this){
                int available = available();
                if (available > 0){
                    int length = Math.min(len, available);
                    int firstLen = Math.min(length, buffer.length - readPosition);
                    int secondLen = length - firstLen;
                    System.arraycopy(buffer, readPosition, buf, off, firstLen);
                    if (secondLen > 0){
                        System.arraycopy(buffer, 0, buf, off+firstLen,  secondLen);
                        readPosition = secondLen;
                    } else {
                        readPosition += length;
                    }
                    if (readPosition == buffer.length) {
                        readPosition = 0;
                    }
                    return length;
                } else if (inputDone){
                    return -1;
                }
            }
            Thread.sleep(100);
        }
    }
 
 
    /**
     * Skip Objects.  This method should be used by the consumer
     * when it does not care to examine some number of Objects.
     * This method will block until some Objects are available,
     * or there will be no more Objects available.
     *
     * @param n The number of Objects to skip
     * @return The number of Objects actually skipped
     * @throws IllegalArgumentException if n is negative.
     * @throws InterruptedException if the thread is interrupted while waiting.
     *
     * @since ostermillerutils 1.00.00
     */
    public long skip(long n) throws InterruptedException, IllegalArgumentException {
        while (true){
            synchronized (this){
                int available = available();
                if (available > 0){
                    int length = Math.min((int)n, available);
                    int firstLen = Math.min(length, buffer.length - readPosition);
                    int secondLen = length - firstLen;
                    if (secondLen > 0){
                        readPosition = secondLen;
                    } else {
                        readPosition += length;
                    }
                    if (readPosition == buffer.length) {
                        readPosition = 0;
                    }
                    return length;
                } else if (inputDone){
                    return 0;
                }
            }
            Thread.sleep(100);
        }
    }
 
    /**
     * This method should be used by the producer to signal to the consumer
     * that the producer is done producing objects and that the consumer
     * should stop asking for objects once it has used up buffered objects.
     * <p>
     * Once the producer has signaled that it is done, further write() invocations
     * will cause an IllegalStateException to be thrown. Calling done() multiple times,
     * however, has no effect.
     *
     * @since ostermillerutils 1.00.00
     */
    public void done(){
        synchronized (this){
            inputDone = true;
        }
    }
 
    /**
     * Fill this buffer with array of Objects.  This method should be called
     * by the producer.
     * If the buffer allows blocking writes, this method will block until
     * all the data has been written rather than throw a BufferOverflowException.
     *
     * @param buf Array of Objects to be written
     * @throws BufferOverflowException if buffer does not allow blocking writes
     *   and the buffer is full.  If the exception is thrown, no data
     *   will have been written since the buffer was set to be non-blocking.
     * @throws IllegalStateException if done() has been called.
     * @throws InterruptedException if the write is interrupted.
     *
     * @since ostermillerutils 1.00.00
     */
    public void write(ElementType[] buf) throws BufferOverflowException, IllegalStateException, InterruptedException {
        write(buf, 0, buf.length);
    }
 
    /**
     * Fill this buffer with a portion of an array of Objects.
     * This method should be called by the producer.
     * If the buffer allows blocking writes, this method will block until
     * all the data has been written rather than throw an IOException.
     *
     * @param buf Array of Objects
     * @param off Offset from which to start writing Objects
     * @param len - Number of Objects to write
     * @throws BufferOverflowException if buffer does not allow blocking writes
     *   and the buffer is full.  If the exception is thrown, no data
     *   will have been written since the buffer was set to be non-blocking.
     * @throws IllegalStateException if done() has been called.
     * @throws InterruptedException if the write is interrupted.
     *
     * @since ostermillerutils 1.00.00
     */
    public void write(ElementType[] buf, int off, int len) throws BufferOverflowException, IllegalStateException, InterruptedException {
        while (len > 0){
            synchronized (CircularObjectBuffer.this){
                if (inputDone) throw new IllegalStateException("CircularObjectBuffer.done() has been called, CircularObjectBuffer.write() failed.");
                int spaceLeft = spaceLeft();
                while (infinite && spaceLeft < len){
                    resize();
                    spaceLeft = spaceLeft();
                }
                if (!blockingWrite && spaceLeft < len) throw new BufferOverflowException("CircularObjectBuffer is full; cannot write " + len + " Objects");
                int realLen = Math.min(len, spaceLeft);
                int firstLen = Math.min(realLen, buffer.length - writePosition);
                int secondLen = Math.min(realLen - firstLen, buffer.length - readPosition - 1);
                int written = firstLen + secondLen;
                if (firstLen > 0){
                    System.arraycopy(buf, off, buffer, writePosition, firstLen);
                }
                if (secondLen > 0){
                    System.arraycopy(buf, off+firstLen, buffer, 0, secondLen);
                    writePosition = secondLen;
                } else {
                    writePosition += written;
                }
                if (writePosition == buffer.length) {
                    writePosition = 0;
                }
                off += written;
                len -= written;
            }
            if (len > 0){
                Thread.sleep(100);
            }
        }
    }
 
    /**
     * Add a single Object to this buffer.  This method should be
     * called by the producer.
     * If the buffer allows blocking writes, this method will block until
     * all the data has been written rather than throw an IOException.
     *
     * @param o Object to be written.
     * @throws BufferOverflowException if buffer does not allow blocking writes
     *   and the buffer is full.  If the exception is thrown, no data
     *   will have been written since the buffer was set to be non-blocking.
     * @throws IllegalStateException if done() has been called.
     * @throws InterruptedException if the write is interrupted.
     *
     * @since ostermillerutils 1.00.00
     */
    public void write(ElementType o) throws BufferOverflowException, IllegalStateException, InterruptedException {
        boolean written = false;
        while (!written){
            synchronized (CircularObjectBuffer.this){
                if (inputDone) throw new IllegalStateException("CircularObjectBuffer.done() has been called, CircularObjectBuffer.write() failed.");
                int spaceLeft = spaceLeft();
                while (infinite && spaceLeft < 1){
                    resize();
                    spaceLeft = spaceLeft();
                }
                if (!blockingWrite && spaceLeft < 1) throw new BufferOverflowException("CircularObjectBuffer is full; cannot write 1 Object");
                if (spaceLeft > 0){
                    buffer[writePosition] = o;
                    writePosition++;
                    if (writePosition == buffer.length) {
                        writePosition = 0;
                    }
                    written = true;
                }
            }
            if (!written){
                Thread.sleep(100);
            }
        }
    }
}