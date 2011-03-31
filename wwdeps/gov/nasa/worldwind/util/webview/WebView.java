/*
Copyright (C) 2001, 2010 United States Government as represented by 
the Administrator of the National Aeronautics and Space Administration. 
All Rights Reserved. 
*/
package gov.nasa.worldwind.util.webview;

import gov.nasa.worldwind.Disposable;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.render.*;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;

/**
 * WebView provides an interface for loading web content, laying out and rendering the content as an OpenGL texture, and
 * interacting with the rendered content. This functionality is divided into four main tasks: <ul> <li>Loading web
 * content into a WebView's frame.</li> <li>Sending input events to the WebView's frame.</li> <li>Receiving link
 * selected events from the WebView's frame.</li> <li>Receiving a rendered representation of the WebView's frame.</li>
 * </ul>
 * <p/>
 * A WebView is configured by specifying its text content and the size of the WebView's frame. The text may be an HTML
 * document, an HTML fragment, simple text, {@code null}, or another text format supported by the implementation. The
 * size of the WebView's frame is specified in pixels, and may not exceed an implementation-defined maximum. Most
 * implementations define the maximum value to be 4096 - the maximum texture size on most platforms.
 * <p/>
 * The user can interact with the WebView using the mouse and keyboard. The application must send input events to the
 * WebView's frame because WebView is not associated with any windowing system. Input events are received and processed
 * in an implementation-defined manner. Sending input events to the WebView causes the WebView to notify its select
 * listeners about any link clicked events generated by user interaction. Listeners can suppress the WebView's default
 * navigation behavior by consuming the WebView's SelectEvents.
 * <p/>
 * The WebView provides a representation of itself as an OpenGL texture. On machines that support non-power-of-two sized
 * textures, this texture has dimensions equal to the WebView's frame size. Otherwise, the texture's dimensions are the
 * smallest power-of-two that captures the WebView's frame size. The WebView's texture representation is standard
 * two-dimensional OpenGL texture that may be mapped onto any OpenGL primitive using texture coordinates.
 * <p/>
 * When the WebView's texture representation changes as a result of an internal event it fires a property change event
 * with the key {@link gov.nasa.worldwind.avlist.AVKey#REPAINT}. This can happen from web content loading, user
 * interaction, or from a programmatic change such as JavaScript.
 *
 * @author dcollins
 * @version $Id: WebView.java 14565 2011-01-26 22:18:14Z pabercrombie $
 */
public interface WebView extends AVList, Disposable
{
    /**
     * Specifies the WebView's text content. The specified string may be one of the following: <ul> <li>HTML
     * document</li> <li>HTML fragment</li> <li>Simple text</li> <li>{@code null}</li> </ul> The WebView displays
     * nothing if the string is {@code null}. If the URL is {@code null}, the WebView interprets relative URLs using the
     * current working directory.
     * <p/>
     * If the application sends input events to the WebView, the user may navigate away from the specified HTML content
     * by interacting with links or buttons in the content.
     *
     * @param string  The WebView's text content, or {@code null} to display an empty WebView.
     * @param baseURL The URL used to resolve relative URLs in the text content, or {@code null} to use the current
     *                working directory.
     */
    void setHTMLString(String string, URL baseURL);

    /**
     * Returns the size in pixels of the WebView's frame. This returns {@code null} if the WebView's frame size is
     * unspecified.
     *
     * @return The size of the WebView's frame in pixels, or {@code null} if it's unspecified.
     *
     * @see #setFrameSize(java.awt.Dimension)
     */
    Dimension getFrameSize();

    /**
     * Specifies the size in pixels of the WebView's frame.
     *
     * @param size The size of the WebView's frame in pixels.
     *
     * @throws IllegalArgumentException if {@code size} is {@code null}, if the width or height are less than one, or if
     *                                  the width or height exceed the implementation-defined maximum.
     */
    void setFrameSize(Dimension size);

    /**
     * Returns an iterable of <code>AVList</code> elements describing this <code>WebView</code>'s visible links. The
     * returned iterable has no elements if this <code>WebView</code> has no links, or if none of the links are
     * currently in the <code>WebView</code>'s visible area. Each <code>AVList</code> describes the parameters for one
     * link (or part of a multi-line link) as follows:
     * <p/>
     * <table> <tr><th align="left">Key</th><th align="left">Value</th></tr> <tr><td>{@link
     * gov.nasa.worldwind.avlist.AVKey#URL}</td><td>A <code>String</code> containing the link's destination.</td></tr>
     * <tr><td>{@link gov.nasa.worldwind.avlist.AVKey#MIME_TYPE}</td><td>A <code>String</code> mime type describing the
     * content type of the link's destination.</td></tr> <tr><td>{@link gov.nasa.worldwind.avlist.AVKey#TARGET}</td><td>The
     * link's target browser, either {@link gov.nasa.worldwind.avlist.AVKey#NAVIGATION_TARGET_CURRENT_BROWSER} or {@link
     * gov.nasa.worldwind.avlist.AVKey#NAVIGATION_TARGET_NEW_BROWSER}.</td></tr> <tr><td>{@link
     * gov.nasa.worldwind.avlist.AVKey#BOUNDS}</td><td>The link's bounding rectangle.</td></tr> </table>
     * <p/>
     * Multi-line links are returned as multiple elements in the iterable. Each element of a multi-line link has a
     * unique bounding rectangle but shares the same <code>AVList</code> parameters.
     * <p/>
     * The links bounding rectangles are in the <code>WebView</code>'s local coordinate system, and is clipped to the
     * <code>WebView</code>'s visible area. The <code>WebView</code>'s coordinate system has its origin in the lower
     * left corner with the X-axis pointing right and the Y-axis pointing up.
     *
     * @return an <code>Iterable</code> describing this <code>WebView</code>'s visible links.
     */
    Iterable<AVList> getLinks();

    /**
     * Returns a layed out and rendered representation of the WebView's content as a {@link
     * gov.nasa.worldwind.render.WWTexture}. The texture's image source is the WebView, and its dimensions are large
     * enough to capture the WebView's frame size (see {@link #setFrameSize(java.awt.Dimension)}.
     * <p/>
     * On machines that support non-power-of-two sized textures, the texture's dimensions are always equal to the
     * WebView's frame size. Otherwise, the texture's dimensions are the smallest power-of-two that captures the
     * WebView's frame size.
     *
     * @param dc The draw context the WebView is associated with.
     *
     * @return A rendered representation of the WebView's frame as a {@code WWTexture}.
     */
    WWTexture getTextureRepresentation(DrawContext dc);

    /**
     * Returns the delegate owner of the WebView. If non-{@code null}, the returned object replaces the WebView as the
     * picked object returned in any {@link gov.nasa.worldwind.event.SelectEvent} generated by the WebView. If {@code
     * null}, the WebView itself is the picked object.
     *
     * @return the object used as the picked object in any generated SelectEvents, or {@code null} to indicate the
     *         WebView is used.
     */
    Object getDelegateOwner();

    /**
     * Specifies the delegate owner of the WebView. If non-{@code null}, the delegate owner replaces the WebView as the
     * picked object returned in any {@link gov.nasa.worldwind.event.SelectEvent} generated by the WebView. If {@code
     * null}, the WebView itself is the picked object.
     *
     * @param owner the object to use as the picked object in any generated SelectEvents, or {@code null} to use the
     *              WebView.
     */
    void setDelegateOwner(Object owner);

    /**
     * Adds a select listener to the WebView. This calls {@link gov.nasa.worldwind.event.SelectListener#selected(gov.nasa.worldwind.event.SelectEvent)}
     * when a link clicked. If the listener is added to the WebView more than once it receives one event for the number
     * of times it was added. Listeners can suppress the WebView's default navigation behavior by consuming the
     * WebView's SelectEvents.
     * <p/>
     * In order to generate link clicked events, the caller must send input events to the WebView by calling {@link
     * #sendEvent(java.awt.event.InputEvent)}.
     * <p/>
     * This does nothing if the listener is {@code null}.
     *
     * @param listener The select listener to add.
     */
    void addSelectListener(SelectListener listener);

    /**
     * Removes a select listener from the WebView. The specified listener ceases to receive any select events from the
     * WebView. If the listener is added to the WebView more than once this removes the last entry added but retains all
     * others.
     * <p/>
     * This does nothing if the listener is {@code null}, or if the listener is not attached to the WebView.
     *
     * @param listener The select listener to remove.
     *
     * @see #addSelectListener(gov.nasa.worldwind.event.SelectListener)
     */
    void removeSelectListener(SelectListener listener);

    /**
     * Called when this WebView is activated or deactivated. The WebView only receives input events when it is active.
     *
     * @param active {@code true} if this WebView is being activated. {@code false} if this WebView is being
     *               deactivated.
     *
     * @see #sendEvent
     */
    void setActive(boolean active);

    /**
     * Indicates whether or not this WebView is active. The WebView only receives input events when it is active.
     *
     * @return {@code true} if this WebView is active, {@code false} if not.
     */
    boolean isActive();

    /**
     * Sends the specified input event to the WebView. Which events the WebView's responds to and how it responds is
     * implementation-defined. Typical implementations respond to {@link java.awt.event.KeyEvent}, {@link
     * java.awt.event.MouseEvent}, and {@link java.awt.event.MouseWheelEvent}.
     * <p/>
     * The screen coordinates for a {@code MouseEvent} must be transformed into the WebView's local coordinate system,
     * which has its origin in the lower left corner with the X-axis pointing right and the Y-axis pointing up.
     * <p/>
     * This does nothing if the specified event is {@code null}.
     * <p/>
     * Users of the WebView must call {@link #setActive} before sending input events to the WebView. The WebView can be
     * activated and deactivated any number of times. For example, a controller might call {@code setActive(true)} when
     * the mouse enters the WebView texture, and call {@code setActive(false)} when the mouse exits the texture.
     *
     * @param event the event to send.
     *
     * @see #setActive
     */
    void sendEvent(InputEvent event);

    /** Navigate the WebView to the previous page in the browsing history. Has no effect if there is no previous page. */
    void goBack();

    /** Navigate the WebView to the next page in the browsing history. Has no effect if there is no next page. */
    void goForward();
}
