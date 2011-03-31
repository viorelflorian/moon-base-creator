/*
 * Copyright (C) 2001, 2010 United States Government
 * as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.util.webview;

import gov.nasa.worldwind.avlist.AVList;

import java.awt.event.*;
import java.beans.PropertyChangeListener;

/**
 * JNI bindings for the Windows WebView library. This library provides functions for creating and destroying native
 * WebViews, sending user input to a WebView, and adding listeners to a WebView.
 * <p/>
 * <h3>Message loops</h3>
 * <p/>
 * WebViews created by this library must be managed by a message loop in native code. This class provides methods for
 * creating and running a native message loop using a Java thread. Each WebView must be associated with one message
 * loop. Each message loop can handle any number of WebViews.
 * <p/>
 * To create a WebView message loop: <ol> <li> Create a new Java thread to run the message loop.</li> <li> Call {@link
 * #newMessageLoop()} from the message loop thread.</li> <li> Call {@link #runMessageLoop()} from the message loop
 * thread. This enters a blocking loop in native code. It will not return until {@link #releaseMessageLoop} is called by
 * another thread.</li> </ol>
 * <p/>
 * Here is an example of creating and running  a message loop:
 * <p/>
 * <pre>
 * long webViewMessageLoop = 0;
 * <p/>
 * // Create a new thread to run the WebView message loop.
 * webViewUI = new Thread("WebView UI")
 * {
 *      public void run()
 *      {
 *          // Create a message loop in native code. This call must return
 *          // before any messages are sent to the WebView.
 *          webViewMessageLoop = WindowsWebViewJNI.newMessageLoop();
 * <p/>
 *          // Notify the outer thread that the message loop is ready.
 *          synchronized (webViewUILock)
 *          {
 *              webViewUILock.notify();
 *          }
 * <p/>
 *          // Process messages in native code until the message loop
 *          // is terminated.
 *          WindowsWebViewJNI.runMessageLoop();
 *      }
 *  };
 *  webViewUI.start();
 * <p/>
 *  // Wait for the newly started thread to create the message loop. We cannot
 *  // safely use the WebView until the message loop has been initialized.
 *  while (webViewMessageLoop == 0)
 *  {
 *      try
 *      {
 *          webViewUILock.wait();
 *      }
 *      catch (InterruptedException ignored)
 *      {
 *      }
 *  }
 * </pre>
 *
 * @author pabercrombie
 * @version $Id: WindowsWebViewJNI.java 14560 2011-01-25 19:53:39Z pabercrombie $
 */
public class WindowsWebViewJNI
{
    static
    {
        String architecture = System.getProperty("os.arch");
        if ("x86".equals(architecture))
            System.loadLibrary("webview32");
        else
            System.loadLibrary("webview64");

        initialize();
    }

    /** Initialize the native library. This method must be called before any of the other methods in this class. */
    protected static native void initialize();

    /**
     * Create a new native message loop.
     *
     * @return An identifier for the new loop, or zero if creation fails.
     */
    public static native long newMessageLoop();

    /** Run a native message loop. This method call does not return until the message loop is terminated. */
    public static native void runMessageLoop();

    /**
     * Release a previously allocated message loop.
     *
     * @param messageLoop identifier of the message loop to release.
     *
     * @see #newMessageLoop()
     */
    public static native void releaseMessageLoop(long messageLoop);

    /**
     * Release a WebView window.
     *
     * @param webViewWindowPtr pointer to the window to release.
     */
    public static native void releaseWebView(long webViewWindowPtr);

    /**
     * Release a COM object.
     *
     * @param unknownPtr pointer to object to release
     */
    public static native void releaseComObject(long unknownPtr);

    /**
     * Create a new WebView window.
     *
     * @param messageLoop message loop that will handle events for the window.
     *
     * @return Identifier for the new window, or zero if creation fails.
     */
    public static native long newWebViewWindow(long messageLoop);

    /**
     * Set a WebViewWindow to be active or inactive. The window only handles simulated input when it is active.
     *
     * @param webViewWindowPtr window to set active or inactive.
     * @param active           {@code true} if the window is being activated. {@code false} if the window is being
     *                         deactivated.
     */
    public static native void setActive(long webViewWindowPtr, boolean active);

    /**
     * Retrieves the interval at which the native WebView updates its captured image.
     *
     * @param webViewWindowPtr pointer to a native WebView
     *
     * @return The WebView's refresh interval, in milliseconds.
     */
    public static native long getRefreshInterval(long webViewWindowPtr);

    /**
     * Set the interval at which the native WebView updates its captured image.
     *
     * @param webViewWindowPtr pointer to a native WebView.
     * @param refreshInterval  interval in milliseconds. The WebView will periodically update the captured image of the
     *                         web page at this interval.
     */
    public static native void setRefreshInterval(long webViewWindowPtr, long refreshInterval);

    /**
     * Create a new notification adapter to bridge changes in the native WebView to PropertyChangeEvents.
     *
     * @param listener listener that will receive PropertyChangeEvents caused by changes in the native WebView
     *
     * @return identifier for the new notification adapter, or zero if creation fails. The notification adapter must be
     *         freed by {@code releaseComObject}.
     *
     * @see #releaseComObject
     */
    public static native long newNotificationAdapter(PropertyChangeListener listener);

    /**
     * Set the HTML content of a WebView.
     *
     * @param webViewWindowPtr WebView window to set content of
     * @param htmlString       new HTML content
     * @param baseUrlString    base URL against which to resolve relative links
     */
    public static native void setHTMLString(long webViewWindowPtr, String htmlString, String baseUrlString);

    /**
     * Set the size of a WebView window.
     *
     * @param webViewWindowPtr window to set size of
     * @param width            new width
     * @param height           new height
     */
    public static native void setFrameSize(long webViewWindowPtr, int width, int height);

    /**
     * Set the navigation policy for a WebView window. The navigation policy determines what happens when a link is
     * activated.
     *
     * @param webViewWindowPtr window in which to set navigation policy
     * @param policy           new navigation policy
     */
    public static native void setWebNavigationPolicy(long webViewWindowPtr, WebNavigationPolicy policy);

    /**
     * Send an input event to a WebView window. The AWT InputEvent will translated into native Windows input messages.
     *
     * @param webViewWindowPtr window to send input to.
     * @param event            input event to send.
     */
    public static native void sendEvent(long webViewWindowPtr, InputEvent event);

    /**
     * Get link parameters for a point in the WebView.
     *
     * @param webViewWindowPtr window to query
     * @param params           list to receive link parameters
     * @param x                X coordinate of point
     * @param y                Y coordinate of point
     */
    public static native void getLinkParamsAtPoint(long webViewWindowPtr, AVList params, int x, int y);

    /**
     * Get the time at which the WebView rendered contents last changed.
     *
     * @param webViewWindowPtr pointer to native WebView
     *
     * @return The time (in milliseconds since the system started) at which the WebView rendered content last changed.
     */
    public static native long getUpdateTime(long webViewWindowPtr);

    /**
     * Add an observer that will be notified when the rendered contents of the WebView change (due to animation, user
     * input, etc)
     *
     * @param webViewWindowPtr pointer to native WebView to observe
     * @param observerPtr      notification adapter allocated by {@link #newNotificationAdapter}
     */
    public static native void addWindowUpdateObserver(long webViewWindowPtr, long observerPtr);

    /**
     * Remove an update observer from a WebView.
     *
     * @param webViewWindowPtr pointer to native WebView from which to remove observer
     * @param observerPtr      observer to remove
     */
    public static native void removeWindowUpdateObserver(long webViewWindowPtr, long observerPtr);

    /**
     * Load the captured WebView image into an OpenGL texture.
     *
     * @param webViewWindowPtr pointer to native WebView to load into texture
     * @param target           GL texture identifier
     */
    public static native void loadDisplayInGLTexture(long webViewWindowPtr, int target);

    /**
     * Navigate a WebView window to the previous page in the navigation history. Has no effect if there is no previous
     * page.
     *
     * @param webViewWindowPtr WebView window to navigate.
     */
    public static native void goBack(long webViewWindowPtr);

    /**
     * Navigate a WebView window to the next page in the navigation history. Has no effect if there is no next page.
     *
     * @param webViewWindowPtr WebView window to navigate.
     */
    public static native void goForward(long webViewWindowPtr);
}
