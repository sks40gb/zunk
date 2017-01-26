/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.threadpool;

/**
 *
 * @author balab
 */
public class Done {
   /**
  * The number of Worker object
  * threads that are currently working
  * on something.
  */
 private int _activeThreads = 0;

 /**
  * This boolean keeps track of if
  * the very first thread has started
  * or not. This prevents this object
  * from falsely reporting that the ThreadPool 
  * is done, just because the first thread
  * has not yet started.
  */
 private boolean _started = false;
 /**
  * This method can be called to block
  * the current thread until the ThreadPool
  * is done.
  */

 synchronized public void waitDone()
 {
  try {
   while ( _activeThreads>0 ) {
    wait();
   }
  } catch ( InterruptedException e ) {
  }
 }
 /**
  * Called to wait for the first thread to 
  * start. Once this method returns the
  * process has begun.
  */

 synchronized public void waitBegin()
 {
  try {
   while ( !_started ) {
    wait();
   }
  } catch ( InterruptedException e ) {
  }
 }


 /**
  * Called by a Worker object
  * to indicate that it has begun 
  * working on a workload.
  */
 synchronized public void workerBegin()
 {
  _activeThreads++;
  _started = true;
  notify();
 }

 /**
  * Called by a Worker object to 
  * indicate that it has completed a 
  * workload.
  */
 synchronized public void workerEnd()
 {
  _activeThreads--;
  notify();
 }

 /**
  * Called to reset this object to
  * its initial state.
  */
 synchronized public void reset()
 {
  _activeThreads = 0;
 }
}
