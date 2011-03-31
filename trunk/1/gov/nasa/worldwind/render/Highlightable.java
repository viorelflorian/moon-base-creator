/*
Copyright (C) 2001, 2009 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.render;

/**
 * An interface to control a shape's highlighting. Shapes implementing this interface have their own highlighting
 * behaviors and attributes and the means for setting them.
 *
 * @author tag
 * @version $Id: Highlightable.java 13620 2010-08-11 04:05:16Z tgaskins $
 */
public interface Highlightable
{
    /**
     * Indicates whether to highlight the shape.
     *
     * @return true to highlight the shape, otherwise false.
     */
    boolean isHighlighted();

    /**
     * Specifies whether to highlight the shape.
     *
     * @param highlighted true to highlight the shape, otherwise false.
     */
    void setHighlighted(boolean highlighted);
}
