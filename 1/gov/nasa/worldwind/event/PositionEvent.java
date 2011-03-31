/*
Copyright (C) 2001, 2006 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.event;

import gov.nasa.worldwind.geom.Position;

/**
 * @author tag
 * @version $Id: PositionEvent.java 14228 2010-12-13 22:21:50Z dcollins $
 */
public class PositionEvent extends WWEvent
{
    private final java.awt.Point screenPoint;
    private final Position position;
    private final Position previousPosition;

    public PositionEvent(Object source, java.awt.Point screenPoint, Position previousPosition, Position position)
    {
        super(source);
        this.screenPoint = screenPoint;
        this.position = position;
        this.previousPosition = previousPosition;
    }

    public java.awt.Point getScreenPoint()
    {
        return screenPoint;
    }

    public Position getPosition()
    {
        return position;
    }

    public Position getPreviousPosition()
    {
        return previousPosition;
    }

    @Override
    public String toString()
    {
        return this.getClass().getName() + " "
            + (this.previousPosition != null ? this.previousPosition : "null")
            + " --> "
            + (this.position != null ? this.position : "null");
    }
}
