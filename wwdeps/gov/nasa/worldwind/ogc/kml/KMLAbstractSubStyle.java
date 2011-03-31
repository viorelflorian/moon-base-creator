/*
Copyright (C) 2001, 2010 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.ogc.kml;

/**
 * Represents the KML <i>SubStyle</i> element.
 *
 * @author tag
 * @version $Id: KMLAbstractSubStyle.java 13396 2010-05-25 21:02:14Z tgaskins $
 */
public abstract class KMLAbstractSubStyle extends KMLAbstractObject
{
    /**
     * Construct an instance.
     *
     * @param namespaceURI the qualifying namespace URI. May be null to indicate no namespace qualification.
     */
    protected KMLAbstractSubStyle(String namespaceURI)
    {
        super(namespaceURI);
    }
}
