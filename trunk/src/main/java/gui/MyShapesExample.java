package gui;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.AbstractLayer;
import gov.nasa.worldwind.layers.CompassLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;

import java.util.ArrayList;

import javax.swing.JInternalFrame;

public class MyShapesExample{
	
	/**
	 * 
	 */
	/**
	 * 
	 */
	/**
	 * 
	 */
	/**
	 * 
	 */
	MyShapesExample(){
		 RenderableLayer layer = new RenderableLayer();

         // Create and set an attribute bundle.
         ShapeAttributes sideAttributes = new BasicShapeAttributes();
         sideAttributes.setInteriorMaterial(Material.MAGENTA);
         sideAttributes.setOutlineOpacity(0.5);
         sideAttributes.setInteriorOpacity(0.5);
         sideAttributes.setOutlineMaterial(Material.GREEN);
         sideAttributes.setOutlineWidth(2);
         sideAttributes.setDrawOutline(true);
         sideAttributes.setDrawInterior(true);
         sideAttributes.setEnableLighting(true);

         ShapeAttributes sideHighlightAttributes = new BasicShapeAttributes(sideAttributes);
         sideHighlightAttributes.setOutlineMaterial(Material.WHITE);
         sideHighlightAttributes.setOutlineOpacity(1);

         ShapeAttributes capAttributes = new BasicShapeAttributes(sideAttributes);
         capAttributes.setInteriorMaterial(Material.YELLOW);
         capAttributes.setInteriorOpacity(0.8);
         capAttributes.setDrawInterior(true);
         capAttributes.setEnableLighting(true);

         // Create a path, set some of its properties and set its attributes.
         ArrayList<Position> pathPositions = new ArrayList<Position>();
         pathPositions.add(Position.fromDegrees(0, -4.5, 3e5));
         pathPositions.add(Position.fromDegrees(4, -6, 3e5));
         pathPositions.add(Position.fromDegrees(4, -5, 3e5));
         pathPositions.add(Position.fromDegrees(1.5, -4, 3e5));
         pathPositions.add(Position.fromDegrees(4, -3, 3e5));
         pathPositions.add(Position.fromDegrees(4, -2, 3e5));
         pathPositions.add(Position.fromDegrees(0, -3.5, 3e5));
         
         ExtrudedPolygon pgon = new ExtrudedPolygon(pathPositions);
         pgon.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
         pgon.setSideAttributes(sideAttributes);
         pgon.setSideHighlightAttributes(sideHighlightAttributes);
         pgon.setCapAttributes(capAttributes);
         layer.addRenderable(pgon);
         

         ArrayList<LatLon> pathLocations = new ArrayList<LatLon>();
         pathLocations.add(LatLon.fromDegrees(0, -0.5));
         pathLocations.add(LatLon.fromDegrees(4, -0.5));
         pathLocations.add(LatLon.fromDegrees(4, 0.5));
         pathLocations.add(LatLon.fromDegrees(0, 0.5));
         pgon = new ExtrudedPolygon(pathLocations, 3e5);
         pgon.setSideAttributes(sideAttributes);
         pgon.setSideHighlightAttributes(sideHighlightAttributes);
         pgon.setCapAttributes(capAttributes);
         layer.addRenderable(pgon);
         
         
         ArrayList<LatLon> pathLocations1 = new ArrayList<LatLon>();
         pathLocations1.add(LatLon.fromDegrees(0, 3));
         pathLocations1.add(LatLon.fromDegrees(1, 2));
         pathLocations1.add(LatLon.fromDegrees(3, 2));
         pathLocations1.add(LatLon.fromDegrees(4, 3));
         pathLocations1.add(LatLon.fromDegrees(4, 4));
         pathLocations1.add(LatLon.fromDegrees(3, 5));
         pathLocations1.add(LatLon.fromDegrees(1, 5));
         pathLocations1.add(LatLon.fromDegrees(0, 4));
         pgon = new ExtrudedPolygon(pathLocations1, 3e5);
         
         pathLocations1.clear();
         pathLocations1.add(LatLon.fromDegrees(0.5, 3));
         pathLocations1.add(LatLon.fromDegrees(1, 2.5));
         pathLocations1.add(LatLon.fromDegrees(3, 2.5));
         pathLocations1.add(LatLon.fromDegrees(3.5, 3));
         pathLocations1.add(LatLon.fromDegrees(3.5, 4));
         pathLocations1.add(LatLon.fromDegrees(3, 4.5));
         pathLocations1.add(LatLon.fromDegrees(1, 4.5));
         pathLocations1.add(LatLon.fromDegrees(0.5, 4));
         
        pgon.addInnerBoundary(pathLocations1);
         
         
         
         pgon.setSideAttributes(sideAttributes);
         pgon.setSideHighlightAttributes(sideHighlightAttributes);
         pgon.setCapAttributes(capAttributes);
         layer.addRenderable(pgon);

         //LayerList layers = BaseFrame.desktop.getSelectedFrame().getContentPane().getjPanel.worldWindowGLCanvas.getModel().getLayers();
         
         
         //TODO finish this quik
         
         
         LayerList layers = MoonWorkspaceInternalFrame.getStuff();
         layers.add(layer);
         System.out.println("Shape added");
         
         
         // Add the layer to the model.
        // insertBeforeCompass(getWwd(), layer);

         // Update layer panel
         //this.getLayerPanel().update(this.getWwd());
     }


	

}
