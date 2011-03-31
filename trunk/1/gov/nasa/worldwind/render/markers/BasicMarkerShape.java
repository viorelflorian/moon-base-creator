/*
Copyright (C) 2001, 2008 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.render.markers;

import gov.nasa.worldwind.Disposable;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.Logging;

import javax.media.opengl.GL;
import javax.media.opengl.GLContext;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import java.util.ArrayList;

/**
 * @author tag
 * @version $Id: BasicMarkerShape.java 14645 2011-02-07 00:28:29Z pabercrombie $
 */
public class BasicMarkerShape
{
    public static final String SPHERE = "gov.nasa.worldwind.render.markers.Sphere";
    public static final String CUBE = "gov.nasa.worldwind.render.markers.Cube";
    public static final String CONE = "gov.nasa.worldwind.render.markers.Cone";
    public static final String CYLINDER = "gov.nasa.worldwind.render.markers.Cylinder";
    public static final String HEADING_ARROW = "gov.nasa.worldwind.render.markers.HeadingArrow";
    public static final String HEADING_LINE = "gov.nasa.worldwind.render.markers.HeadingLine";
    public static final String ORIENTED_SPHERE = "gov.nasa.worldwind.render.markers.DirectionalSphere";
    public static final String ORIENTED_CUBE = "gov.nasa.worldwind.render.markers.DirectionalCube";
    public static final String ORIENTED_CONE = "gov.nasa.worldwind.render.markers.DirectionalCone";
    public static final String ORIENTED_CYLINDER = "gov.nasa.worldwind.render.markers.DirectionalCylinder";
    public static final String ORIENTED_SPHERE_LINE = "gov.nasa.worldwind.render.markers.DirectionalSphereLine";
    public static final String ORIENTED_CONE_LINE = "gov.nasa.worldwind.render.markers.DirectionalConeLine";
    public static final String ORIENTED_CYLINDER_LINE = "gov.nasa.worldwind.render.markers.DirectionalCylinderLine";

    @SuppressWarnings( {"StringEquality"})
    public static MarkerShape createShapeInstance(String shapeType)
    {
        if (shapeType == null)
        {
            String message = Logging.getMessage("nullValue.ShapeType");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        // String identity rather than equality is wanted here, to avoid a bunch of unnecessary string compares
        if (shapeType == BasicMarkerShape.SPHERE)
            return new Sphere();
        else if (shapeType == BasicMarkerShape.CUBE)
            return new Cube();
        else if (shapeType == BasicMarkerShape.CONE)
            return new Cone();
        else if (shapeType == BasicMarkerShape.CYLINDER)
            return new Cylinder();
        else if (shapeType == BasicMarkerShape.HEADING_ARROW)
            return new HeadingArrow();
        else if (shapeType == BasicMarkerShape.HEADING_LINE)
            return new HeadingLine();
        else if (shapeType == BasicMarkerShape.ORIENTED_SPHERE)
            return new CompoundShape(BasicMarkerShape.ORIENTED_SPHERE, "Oriented Sphere", new Sphere(),
                new HeadingArrow());
        else if (shapeType == BasicMarkerShape.ORIENTED_CUBE)
        {
            Cube cube = new Cube();
            cube.setApplyOrientation(false);  // Heading arrow shows orientation, do not rotate shape
            return new CompoundShape(BasicMarkerShape.ORIENTED_CUBE, "Oriented Cube", cube, new HeadingArrow(),
                .6);
        }
        else if (shapeType == BasicMarkerShape.ORIENTED_CONE)
        {
            Cone cone = new Cone();
            cone.setApplyOrientation(false); // Heading arrow shows orientation, do not rotate shape
            return new CompoundShape(BasicMarkerShape.ORIENTED_CONE, "Oriented Cone", cone, new HeadingArrow(), 0.6);
        }
        else if (shapeType == BasicMarkerShape.ORIENTED_CYLINDER)
        {
            Cylinder cylinder = new Cylinder();
            cylinder.setApplyOrientation(false);  // Heading arrow shows orientation, do not rotate shape
            return new CompoundShape(BasicMarkerShape.ORIENTED_CYLINDER, "Oriented Cylinder", cylinder,
                new HeadingArrow(), .6);
        }
        else if (shapeType == BasicMarkerShape.ORIENTED_SPHERE_LINE)
            return new CompoundShape(BasicMarkerShape.ORIENTED_SPHERE_LINE, "Oriented Sphere Line", new Sphere(),
                new HeadingLine(), 1);
        else if (shapeType == BasicMarkerShape.ORIENTED_CONE_LINE)
        {
            Cone cone = new Cone();
            cone.setApplyOrientation(false);  // Heading arrow shows orientation, do not rotate shape
            return new CompoundShape(BasicMarkerShape.ORIENTED_CONE_LINE, "Oriented Cone Line", cone,
                new HeadingLine(), 2);
        }
        else if (shapeType == BasicMarkerShape.ORIENTED_CYLINDER_LINE)
        {
            Cylinder cylinder = new Cylinder();
            cylinder.setApplyOrientation(false);  // Heading arrow shows orientation, do not rotate shape
            return new CompoundShape(BasicMarkerShape.ORIENTED_CYLINDER_LINE, "Oriented Cylinder Line", cylinder,
                new HeadingLine(), 2);
        }
        else
            return new Sphere();
    }

    private static class CompoundShape implements MarkerShape, Disposable
    {
        protected String name;
        protected String shapeType;
        private ArrayList<MarkerShape> shapes = new ArrayList<MarkerShape>(2);
        private double offset = 0;

        public CompoundShape(String shapeType, String name, MarkerShape shape1, MarkerShape shape2)
        {
            this.name = name;
            this.shapes.add(shape1);
            this.shapes.add(shape2);
        }

        public CompoundShape(String shapeType, String name, MarkerShape shape1, MarkerShape shape2, double offset)
        {
            this.name = name;
            this.shapes.add(shape1);
            this.shapes.add(shape2);
            this.offset = offset;
        }

        public void dispose()
        {
            for (MarkerShape shape : this.shapes)
            {
                if (shape instanceof Disposable)
                    ((Disposable) shape).dispose();
            }
        }

        public String getName()
        {
            return name;
        }

        public String getShapeType()
        {
            return shapeType;
        }

        public void render(DrawContext dc, Marker marker, Vec4 point, double radius)
        {
            this.shapes.get(0).render(dc, marker, point, radius, false);
            if (this.offset != 0)
            {
                Position pos = dc.getGlobe().computePositionFromPoint(point);
                point = dc.getGlobe().computePointFromPosition(pos.getLatitude(), pos.getLongitude(),
                    pos.getElevation() + radius * this.offset);
            }
            this.shapes.get(1).render(dc, marker, point, radius, false);
        }

        public void render(DrawContext dc, Marker marker, Vec4 point, double radius, boolean isRelative)
        {
            this.shapes.get(0).render(dc, marker, point, radius, isRelative);
            if (this.offset != 0)
            {
                Position pos = dc.getGlobe().computePositionFromPoint(point);
                point = dc.getGlobe().computePointFromPosition(pos.getLatitude(), pos.getLongitude(),
                    pos.getElevation() + radius * this.offset);
            }
            this.shapes.get(1).render(dc, marker, point, radius, isRelative);
        }
    }

    protected static abstract class Shape implements MarkerShape, Disposable
    {
        protected String name;
        protected String shapeType;
        protected int glListId;
        protected GLUquadric quadric;
        protected boolean isInitialized = false;
        /** Indicates that the shape must apply heading, pitch, and roll. */
        protected boolean applyOrientation = true;

        abstract protected void doRender(DrawContext dc, Marker marker, Vec4 point, double radius);

        protected void initialize(DrawContext dc)
        {
            this.glListId = dc.getGL().glGenLists(1);
            this.quadric = dc.getGLU().gluNewQuadric();
            dc.getGLU().gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
            dc.getGLU().gluQuadricNormals(quadric, GLU.GLU_SMOOTH);
            dc.getGLU().gluQuadricOrientation(quadric, GLU.GLU_OUTSIDE);
            dc.getGLU().gluQuadricTexture(quadric, false);
        }

        public void dispose()
        {
            if (this.isInitialized)
            {
                GLU glu = new GLU();
                glu.gluDeleteQuadric(this.quadric);
                this.isInitialized = false;

                GLContext glc = GLContext.getCurrent();
                if (glc == null)
                    return;

                glc.getGL().glDeleteLists(this.glListId, 1);

                this.glListId = -1;
            }
        }

        public String getName()
        {
            return this.name;
        }

        public String getShapeType()
        {
            return this.shapeType;
        }

        /**
         * Indicates whether or not the shape applies heading, pitch, and roll when it draws itself. Even if this field
         * is {@code true}, the shape may not apply all of the rotations.
         *
         * @return {@code true} if orientation is applied to the rendered shape, {@code false} if not.
         */
        public boolean isApplyOrientation()
        {
            return this.applyOrientation;
        }

        /**
         * Specifies whether or not the shape applies heading, pitch, and roll when it renders.
         *
         * @param applyOrientation {@code true} if the shape must apply heading, pitch, and roll (if they are supported
         *                         by the shape), {@code false} if it the shape must not apply this orientation.
         */
        public void setApplyOrientation(boolean applyOrientation)
        {
            this.applyOrientation = applyOrientation;
        }

        public void render(DrawContext dc, Marker marker, Vec4 point, double radius)
        {
            render(dc, marker, point, radius, true);
        }

        public void render(DrawContext dc, Marker marker, Vec4 point, double radius,
            boolean isRelative)
        {
            if (!this.isInitialized)
                this.initialize(dc);

            if (!isRelative)
            {
                dc.getView().pushReferenceCenter(dc, point);
            }
            else
            {
                dc.getGL().glPushMatrix();
                dc.getGL().glTranslated(point.x, point.y, point.z);
            }
            this.doRender(dc, marker, point, radius);
            if (!isRelative)
            {
                dc.getView().popReferenceCenter(dc);
            }
            else
            {
                dc.getGL().glPopMatrix();
            }
        }

        /**
         * Compute a direction vector given a point, heading and pitch.
         *
         * @param dc      current draw context
         * @param point   point at which to compute direction vector
         * @param normal  surface normal at {@code point}
         * @param heading desired heading
         * @param pitch   desired pitch
         *
         * @return A vector pointing in the direction of the desired heading and pitch
         */
        protected Vec4 computeOrientationVector(DrawContext dc, Vec4 point, Vec4 normal, Angle heading,
            Angle pitch)
        {
            // To compute rotation of the shape toward the proper heading, find a second point in that direction.
            Globe globe = dc.getGlobe();
            Position pos = globe.computePositionFromPoint(point);
            LatLon p2ll = LatLon.greatCircleEndPosition(pos, heading, Angle.fromDegrees(0.1));
            Vec4 p2 = globe.computePointFromPosition(p2ll.getLatitude(), p2ll.getLongitude(),
                pos.getElevation());

            // Find vector in the direction of the heading
            Vec4 p1p2 = p2.subtract3(point).normalize3();

            // Take cross product of normal vector and heading vector to create an axis around which to apply pitch
            // rotation.
            Vec4 pitchAxis = normal.cross3(p1p2);

            return normal.transformBy3(Matrix.fromAxisAngle(pitch, pitchAxis));
        }
    }

    private static class Sphere extends Shape
    {
        @Override
        protected void initialize(DrawContext dc)
        {
            super.initialize(dc);

            this.name = "Sphere";
            this.shapeType = BasicMarkerShape.SPHERE;
            double radius = 1;
            int slices = 36;
            int stacks = 18;

            dc.getGL().glNewList(this.glListId, GL.GL_COMPILE);
            dc.getGLU().gluSphere(this.quadric, radius, slices, stacks);
            dc.getGL().glEndList();

            this.isInitialized = true;
        }

        protected void doRender(DrawContext dc, Marker marker, Vec4 point, double radius)
        {
            // Sphere is symmetric about all axes, so no need to apply heading, pitch, or roll.
            dc.getGL().glScaled(radius, radius, radius);
            dc.getGL().glCallList(this.glListId);
        }
    }

    /** Cube marker shape. The cube can be oriented using heading, pitch, and roll. */
    protected static class Cube extends Shape
    {
        @Override
        protected void initialize(DrawContext dc)
        {
            super.initialize(dc);

            // Vertices of a cube, 2 units on each side, with the center of the bottom face on the origin.
            float[][] v = {{-1f, 1f, 0f}, {-1f, 1f, 2f}, {1f, 1f, 2f}, {1f, 1f, 0f},
                {-1f, -1f, 2f}, {1f, -1f, 2f}, {1f, -1f, 0f}, {-1f, -1f, 0f}};

            // Array to group vertices into faces
            int[][] faces = {{0, 1, 2, 3}, {2, 5, 6, 3}, {1, 4, 5, 2}, {0, 7, 4, 1}, {0, 7, 6, 3}, {4, 7, 6, 5}};

            // Normal vectors for each face
            float[][] n = {{0, 1, 0}, {1, 0, 0}, {0, 0, 1}, {-1, 0, 0}, {0, 0, -1}, {0, -1, 0}};

            this.name = "Cube";
            this.shapeType = BasicMarkerShape.SPHERE;

            GL gl = dc.getGL();
            gl.glNewList(this.glListId, GL.GL_COMPILE);

            gl.glBegin(GL.GL_QUADS);

            for (int i = 0; i < faces.length; i++)
            {
                gl.glNormal3f(n[i][0], n[i][1], n[i][2]);

                for (int j = 0; j < faces[0].length; j++)
                {
                    gl.glVertex3d(v[faces[i][j]][0], v[faces[i][j]][1], v[faces[i][j]][2]);
                }
            }

            gl.glEnd();
            gl.glEndList();

            this.isInitialized = true;
        }

        protected void doRender(DrawContext dc, Marker marker, Vec4 point, double size)
        {
            Vec4 normal = dc.getGlobe().computeSurfaceNormalAtPoint(point);

            // This performs the same operation as Vec4.axisAngle() but with a "v2" of <0, 0, 1>.
            // Compute rotation angle
            Angle angle = Angle.fromRadians(Math.acos(normal.z));
            // Compute the direction cosine factors that define the rotation axis
            double A = -normal.y;
            double B = normal.x;
            double L = Math.sqrt(A * A + B * B);

            // Rotate the cube so that one of the faces points north
            Position position = dc.getGlobe().computePositionFromPoint(point);
            Vec4 north = dc.getGlobe().computeNorthPointingTangentAtLocation(position.getLatitude(),
                position.getLongitude());
            Vec4 rotatedY = Vec4.UNIT_NEGATIVE_Y.transformBy3(Matrix.fromAxisAngle(angle, A / L, B / L, 0));
            Angle northAngle = rotatedY.angleBetween3(north);

            GL gl = dc.getGL();
            gl.glRotated(angle.degrees, A / L, B / L, 0);  // rotate cube normal to globe

            gl.glRotated(northAngle.degrees, 0, 0, 1); // rotate to face north

            // Apply heading, pitch, and roll
            if (this.isApplyOrientation())
            {
                if (marker.getHeading() != null)
                    gl.glRotated(marker.getHeading().degrees, 0, 0, 1);
                if (marker.getPitch() != null)
                    gl.glRotated(marker.getPitch().degrees, 1, 0, 0);
                if (marker.getRoll() != null)
                    gl.glRotated(marker.getRoll().degrees, 0, 0, 1);
            }

            gl.glScaled(size, size, size);
            gl.glCallList(this.glListId);
        }
    }

    /** A cone marker shape. The cone can be oriented using heading and pitch. */
    protected static class Cone extends Shape
    {
        @Override
        protected void initialize(DrawContext dc)
        {
            super.initialize(dc);

            this.name = "Cone";
            this.shapeType = BasicMarkerShape.CONE;
            int slices = 30;
            int stacks = 30;
            int loops = 2;

            dc.getGL().glNewList(this.glListId, GL.GL_COMPILE);
            dc.getGLU().gluQuadricOrientation(quadric, GLU.GLU_OUTSIDE);
            dc.getGLU().gluCylinder(quadric, 1d, 0d, 2d, slices, (int) (2 * (Math.sqrt(stacks)) + 1));
            dc.getGLU().gluDisk(quadric, 0d, 1d, slices, loops);
            dc.getGL().glEndList();

            this.isInitialized = true;
        }

        protected void doRender(DrawContext dc, Marker marker, Vec4 point, double size)
        {
            // By default, the shape is normal to the globe (0 heading, 0 pitch, 0 roll)
            Vec4 orientation = dc.getGlobe().computeSurfaceNormalAtPoint(point);

            // Heading only applies to cone if pitch is also specified. A heading without pitch spins the cone
            // around its axis. A heading with pitch spins the cone, and then tilts it in the direction of the
            // heading.
            if (this.isApplyOrientation() && marker.getPitch() != null)
            {
                orientation = this.computeOrientationVector(dc, point, orientation,
                    marker.getHeading() != null ? marker.getHeading() : Angle.ZERO,
                    marker.getPitch());
            }

            // This code performs the same operation as Vec4.axisAngle() but with a "v2" of <0, 0, 1>.
            // Compute rotation angle
            Angle angle = Angle.fromRadians(Math.acos(orientation.z));
            // Compute the direction cosine factors that define the rotation axis
            double A = -orientation.y;
            double B = orientation.x;
            double L = Math.sqrt(A * A + B * B);

            dc.getGL().glRotated(angle.degrees, A / L, B / L, 0);  // rotate shape to proper heading and pitch

            dc.getGL().glScaled(size, size, size);                 // scale
            dc.getGL().glCallList(this.glListId);                  // draw
        }
    }

    /** A cylinder marker shape. The cylinder can be oriented using heading and pitch. */
    protected static class Cylinder extends Shape
    {
        @Override
        protected void initialize(DrawContext dc)
        {
            super.initialize(dc);

            this.name = "Cylinder";
            this.shapeType = BasicMarkerShape.CYLINDER;
            int slices = 30;
            int stacks = 1;
            int loops = 1;

            dc.getGL().glNewList(this.glListId, GL.GL_COMPILE);
            dc.getGLU().gluCylinder(quadric, 1d, 1d, 2d, slices, (int) (2 * (Math.sqrt(stacks)) + 1));
            dc.getGLU().gluDisk(quadric, 0d, 1d, slices, loops);
            dc.getGL().glTranslated(0, 0, 2);
            dc.getGLU().gluDisk(quadric, 0d, 1d, slices, loops);
            dc.getGL().glTranslated(0, 0, -2);
            dc.getGL().glEndList();

            this.isInitialized = true;
        }

        protected void doRender(DrawContext dc, Marker marker, Vec4 point, double size)
        {
            Vec4 orientation = dc.getGlobe().computeSurfaceNormalAtPoint(point);

            // Heading only applies to cylinder if pitch is also specified. A heading without pitch spins the cylinder
            // around its axis. A heading with pitch spins the cylinder, and then tilts it in the direction of the
            // heading.
            if (this.isApplyOrientation() && marker.getPitch() != null)
            {
                orientation = this.computeOrientationVector(dc, point, orientation,
                    marker.getHeading() != null ? marker.getHeading() : Angle.ZERO,
                    marker.getPitch());
            }

            // This performs the same operation as Vec4.axisAngle() but with a "v2" of <0, 0, 1>.
            // Compute rotation angle
            Angle angle = Angle.fromRadians(Math.acos(orientation.z));
            // Compute the direction cosine factors that define the rotation axis
            double A = -orientation.y;
            double B = orientation.x;
            double L = Math.sqrt(A * A + B * B);

            dc.getGL().glRotated(angle.degrees, A / L, B / L, 0);  // rotate to proper heading and pitch

            dc.getGL().glScaled(size, size, size);                 // scale
            dc.getGL().glCallList(this.glListId);                  // draw
        }
    }

    /** A line that indicates heading. This shape indicates heading; it ignores pitch and roll. */
    protected static class HeadingLine extends Shape
    {
        @Override
        protected void initialize(DrawContext dc)
        {
            super.initialize(dc);

            this.name = "Heading Line";
            this.shapeType = BasicMarkerShape.HEADING_LINE;

            dc.getGL().glNewList(this.glListId, GL.GL_COMPILE);
            dc.getGL().glBegin(GL.GL_LINE_STRIP);
            dc.getGL().glNormal3f(0f, 1f, 0f);
            dc.getGL().glVertex3d(0, 0, 0);
            dc.getGL().glVertex3d(0, 0, 1);
            dc.getGL().glEnd();
            dc.getGL().glEndList();

            this.isInitialized = true;
        }

        protected void doRender(DrawContext dc, Marker marker, Vec4 point, double size)
        {
            GL gl = dc.getGL();
            MarkerAttributes attrs = marker.getAttributes();

            if (marker.getHeading() == null)
                return;

            // Apply heading material if different from marker's
            if (!dc.isPickingMode() && attrs.getHeadingMaterial() != null
                && !attrs.getHeadingMaterial().equals(attrs.getMaterial()))
            {
                if (attrs.getOpacity() < 1)
                    attrs.getHeadingMaterial().apply(dc.getGL(), GL.GL_FRONT, (float) attrs.getOpacity());
                else
                    attrs.getHeadingMaterial().apply(dc.getGL(), GL.GL_FRONT);
            }

            // To compute rotation of the line axis toward the proper heading, find a second point in that direction.
            Position pos = dc.getGlobe().computePositionFromPoint(point);
            LatLon p2ll = LatLon.greatCircleEndPosition(pos, marker.getHeading(), Angle.fromDegrees(.1));
            Vec4 p2 = dc.getGlobe().computePointFromPosition(p2ll.getLatitude(), p2ll.getLongitude(),
                pos.getElevation());

            // This method then performs the same operation as Vec4.axisAngle() but with a "v2" of <0, 0, 1>.
            Vec4 p1p2 = p2.subtract3(point).normalize3();
            // Compute rotation angle
            Angle directionAngle = Angle.fromRadians(Math.acos(p1p2.z));
            // Compute the direction cosine factors that define the rotation axis
            double A = -p1p2.y;
            double B = p1p2.x;
            double L = Math.sqrt(A * A + B * B);

            gl.glRotated(directionAngle.degrees, A / L, B / L, 0);  // point line toward p2
            double scale = attrs.getHeadingScale() * size;
            gl.glScaled(scale, scale, scale);                       // scale
            dc.getGL().glCallList(this.glListId);                   // draw

            // Restore the marker material if the heading material was applied
            if (!dc.isPickingMode() && attrs.getHeadingMaterial() != null
                && !attrs.getHeadingMaterial().equals(attrs.getMaterial()))
                attrs.apply(dc);
        }
    }

    /** An arrow that indicates heading. This shape indicates heading; it ignores pitch and roll. */
    protected static class HeadingArrow extends Shape
    {
        @Override
        protected void initialize(DrawContext dc)
        {
            super.initialize(dc);

            this.name = "Heading Arrow";
            this.shapeType = BasicMarkerShape.HEADING_ARROW;

            dc.getGL().glNewList(this.glListId, GL.GL_COMPILE);
            dc.getGL().glBegin(GL.GL_POLYGON);
            dc.getGL().glNormal3f(0f, 1f, 0f);
            dc.getGL().glVertex3d(-.5, 0, 0);
            dc.getGL().glVertex3d(0, 0, 1);
            dc.getGL().glVertex3d(.5, 0, 0);
            dc.getGL().glVertex3d(-.5, 0, 0);
            dc.getGL().glEnd();
            dc.getGL().glEndList();

            this.isInitialized = true;
        }

        protected void doRender(DrawContext dc, Marker marker, Vec4 point, double size)
        {
            GL gl = dc.getGL();
            MarkerAttributes attrs = marker.getAttributes();

            if (marker.getHeading() == null)
                return;

            // Apply heading material if different from marker's
            if (!dc.isPickingMode() && attrs.getHeadingMaterial() != null
                && !attrs.getHeadingMaterial().equals(attrs.getMaterial()))
            {
                if (attrs.getOpacity() < 1)
                    attrs.getHeadingMaterial().apply(dc.getGL(), GL.GL_FRONT, (float) attrs.getOpacity());
                else
                    attrs.getHeadingMaterial().apply(dc.getGL(), GL.GL_FRONT);
            }

            // To compute rotation of the arrow axis toward the proper heading, find a second point in that direction.
            Position pos = dc.getGlobe().computePositionFromPoint(point);
            LatLon p2ll = LatLon.greatCircleEndPosition(pos, marker.getHeading(), Angle.fromDegrees(.1));
            Vec4 p2 = dc.getGlobe().computePointFromPosition(p2ll.getLatitude(), p2ll.getLongitude(),
                pos.getElevation());

            // This method then performs the same operation as Vec4.axisAngle() but with a "v2" of <0, 0, 1>.
            Vec4 p1p2 = p2.subtract3(point).normalize3();
            // Compute rotation angle
            Angle directionAngle = Angle.fromRadians(Math.acos(p1p2.z));
            // Compute the direction cosine factors that define the rotation axis
            double A = -p1p2.y;
            double B = p1p2.x;
            double L = Math.sqrt(A * A + B * B);

            // Compute rotation angle on z (roll) to keep the arrow plane parallel to the ground
            Vec4 horizontalVector = dc.getGlobe().computeSurfaceNormalAtPoint(point).cross3(p1p2);
            Vec4 rotatedX = Vec4.UNIT_X.transformBy3(Matrix.fromAxisAngle(directionAngle, A / L, B / L, 0));
            Angle rollAngle = rotatedX.angleBetween3(horizontalVector);
            // Find out which way to do the roll
            double rollDirection = Math.signum(-horizontalVector.cross3(rotatedX).dot3(p1p2));

            gl.glRotated(directionAngle.degrees, A / L, B / L, 0);  // point arrow toward p2
            gl.glRotated(rollAngle.degrees, 0, 0, rollDirection);   // roll arrow to keep it parallel to the ground
            double scale = attrs.getHeadingScale() * size;
            gl.glScaled(scale, scale, scale);                       // scale
            dc.getGL().glCallList(this.glListId);                   // draw

            // Restore the marker material if the heading material was applied
            if (!dc.isPickingMode() && attrs.getHeadingMaterial() != null
                && !attrs.getHeadingMaterial().equals(attrs.getMaterial()))
                attrs.apply(dc);
        }
    }
}
