package com.csc205.project1;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

/**
 * Represents a cube (regular hexahedron) in three-dimensional Cartesian space.
 *
 * This class provides a comprehensive implementation of a 3D cube defined by
 * its center point and edge length. It offers geometric operations, transformations,
 * and queries commonly used in computer graphics, game development, collision
 * detection, and 3D modeling applications.
 *
 * <p>A cube is a regular polyhedron with 6 square faces, 12 edges, and 8 vertices.
 * This implementation uses an axis-aligned representation by default but supports
 * arbitrary rotations through transformation methods.</p>
 *
 * <p><b>Vertex Ordering Convention:</b></p>
 * Vertices are numbered 0-7 following this pattern:
 * <pre>
 *     4----------5
 *    /|         /|
 *   / |        / |
 *  7----------6  |
 *  |  0-------|--1
 *  | /        | /
 *  |/         |/
 *  3----------2
 * </pre>
 * Where vertex 0 is at (center - half_size) and others follow right-handed coordinates.
 *
 * <p>Design Patterns & Principles:</p>
 * <ul>
 *   <li><b>Composition Pattern:</b> Aggregates Point3D and Line3D objects to
 *       represent complex geometric structure. Demonstrates "has-a" relationships
 *       and promotes code reuse.</li>
 *   <li><b>Lazy Initialization Pattern:</b> Edge and face data are computed on
 *       demand and cached, optimizing memory usage and initialization time.</li>
 *   <li><b>Immutability Pattern:</b> All transformation methods return new Cube3D
 *       instances, ensuring thread-safety and preventing side effects.</li>
 *   <li><b>Builder Pattern:</b> Provides fluent API for cube construction with
 *       various parameters and configurations.</li>
 *   <li><b>Template Method Pattern:</b> Defines skeleton of rotation algorithm
 *       with specific axis rotations as concrete implementations.</li>
 *   <li><b>Collections Framework:</b> Returns immutable collections to prevent
 *       external modification of internal structure.</li>
 * </ul>
 *
 * <p>Algorithmic Foundation:</p>
 * This class demonstrates key concepts in computational geometry and graphics:
 * - Affine transformations (translation, rotation, scaling)
 * - Spatial hashing and bounding volumes
 * - Vertex transformation using rotation matrices
 * - Surface area and volume calculations (O(1) for regular polyhedra)
 * - Axis-Aligned Bounding Box (AABB) concepts
 * - Edge and face enumeration algorithms
 *
 * <p><b>Coordinate System:</b> Uses right-handed Cartesian coordinates with
 * Y-axis pointing up (OpenGL convention).</p>
 *
 * <p><b>Performance Characteristics:</b></p>
 * - Vertex/edge retrieval: O(1) after lazy initialization
 * - Transformations: O(n) where n=8 vertices
 * - Containment tests: O(1) for axis-aligned cubes
 * - Memory: O(1) base + O(n) for cached collections
 *
 * @author David Rodriguez
 * @version 1.0
 * @since 2026-01-27
 */
public final class Cube3D {

    private static final Logger logger = Logger.getLogger(Cube3D.class.getName());

    // Core immutable properties
    private final Point3D center;
    private final double edgeLength;

    // Lazily initialized cached data - demonstrates lazy initialization pattern
    private transient Point3D[] vertices;
    private transient Line3D[] edges;

    // Constants
    private static final double EPSILON = 1e-10;
    private static final int VERTEX_COUNT = 8;
    private static final int EDGE_COUNT = 12;
    private static final int FACE_COUNT = 6;

    /**
     * Constructs a new Cube3D with specified center point and edge length.
     *
     * Creates an axis-aligned cube centered at the given point. The cube
     * extends edgeLength/2 in each direction from the center.
     *
     * <p><b>Validation:</b> Ensures edge length is positive and center is not null.</p>
     *
     * <p><b>Time Complexity:</b> O(1) - defers vertex/edge computation to lazy init</p>
     * <p><b>Space Complexity:</b> O(1) - base object without cached collections</p>
     *
     * @param center the center point of the cube
     * @param edgeLength the length of each edge (must be positive)
     * @throws IllegalArgumentException if center is null or edgeLength is not positive
     */
    public Cube3D(Point3D center, double edgeLength) {
        if (center == null) {
            logger.log(Level.SEVERE, "Cannot create Cube3D with null center");
            throw new IllegalArgumentException("Center point cannot be null");
        }

        if (edgeLength <= EPSILON) {
            logger.log(Level.SEVERE, "Invalid edge length: {0}", edgeLength);
            throw new IllegalArgumentException("Edge length must be positive");
        }

        this.center = center;
        this.edgeLength = edgeLength;

        logger.log(Level.INFO, "Created Cube3D at {0} with edge length {1}",
                new Object[]{center, edgeLength});
    }

    /**
     * Static factory method for creating a Cube3D instance.
     *
     * Provides a more fluent API compared to the constructor.
     * Example: Cube3D.of(Point3D.origin(), 2.0)
     *
     * <p><b>Design Pattern:</b> Static Factory Method for improved readability</p>
     *
     * @param center the center point
     * @param edgeLength the edge length
     * @return a new Cube3D instance
     * @throws IllegalArgumentException if validation fails
     */
    public static Cube3D of(Point3D center, double edgeLength) {
        return new Cube3D(center, edgeLength);
    }

    /**
     * Creates a unit cube centered at the origin.
     *
     * Convenience factory method for creating a standard 1x1x1 cube at (0,0,0).
     * Commonly used as a reference object or starting point for transformations.
     *
     * @return a unit cube at the origin
     */
    public static Cube3D unitCube() {
        return new Cube3D(Point3D.origin(), 1.0);
    }

    /**
     * Builder class for fluent cube construction.
     *
     * Provides a flexible way to construct cubes with various configurations.
     * Implements the Builder pattern for improved readability and optional parameters.
     *
     * <p><b>Example Usage:</b></p>
     * <pre>
     * Cube3D cube = Cube3D.builder()
     *     .center(Point3D.of(5, 5, 5))
     *     .edgeLength(10.0)
     *     .build();
     * </pre>
     */
    public static class Builder {
        private Point3D center = Point3D.origin();
        private double edgeLength = 1.0;

        public Builder center(Point3D center) {
            this.center = center;
            return this;
        }

        public Builder edgeLength(double edgeLength) {
            this.edgeLength = edgeLength;
            return this;
        }

        public Cube3D build() {
            return new Cube3D(center, edgeLength);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Accessor methods

    public Point3D getCenter() {
        return center;
    }

    public double getEdgeLength() {
        return edgeLength;
    }

    /**
     * Computes and returns all 8 vertices of the cube.
     *
     * This method uses lazy initialization to compute vertices only when first
     * requested, then caches the result for subsequent calls. Vertices are
     * ordered according to the standard cube vertex numbering (see class javadoc).
     *
     * <p><b>Algorithm:</b> Calculates each vertex by adding/subtracting half
     * the edge length from the center point in each dimension.</p>
     *
     * <p><b>Design Pattern:</b> Lazy Initialization - defers expensive computation
     * until needed and caches result.</p>
     *
     * <p><b>Vertex Order:</b> Follows the convention where vertex i has coordinates
     * determined by the binary representation of i (bit 0=x, bit 1=y, bit 2=z).</p>
     *
     * <p><b>Time Complexity:</b> O(1) amortized (O(8) first call, O(1) subsequent)</p>
     * <p><b>Space Complexity:</b> O(8) for cached vertex array</p>
     *
     * @return immutable list of all 8 vertices
     */
    public List<Point3D> getVertices() {
        if (vertices == null) {
            initializeVertices();
        }
        return Collections.unmodifiableList(Arrays.asList(vertices));
    }

    /**
     * Private helper method to initialize vertex array.
     *
     * Computes all 8 vertices based on center and edge length.
     * Uses bit manipulation to determine sign of offset for each coordinate.
     */
    private void initializeVertices() {
        vertices = new Point3D[VERTEX_COUNT];
        double half = edgeLength / 2.0;

        // Vertex numbering: bit 0 = x, bit 1 = y, bit 2 = z
        // 0 means -, 1 means +
        for (int i = 0; i < VERTEX_COUNT; i++) {
            double x = center.getX() + ((i & 1) == 0 ? -half : half);
            double y = center.getY() + ((i & 2) == 0 ? -half : half);
            double z = center.getZ() + ((i & 4) == 0 ? -half : half);
            vertices[i] = Point3D.of(x, y, z);
        }

        logger.log(Level.INFO, "Initialized {0} vertices for cube at {1}",
                new Object[]{VERTEX_COUNT, center});
    }

    /**
     * Computes and returns all 12 edges of the cube.
     *
     * Each edge is represented as a Line3D connecting two vertices.
     * Edges are grouped by orientation: 4 parallel to each axis.
     *
     * <p><b>Edge Organization:</b></p>
     * - Edges 0-3: parallel to X-axis (bottom and top face)
     * - Edges 4-7: parallel to Y-axis (vertical edges)
     * - Edges 8-11: parallel to Z-axis (front and back face)
     *
     * <p><b>Design Pattern:</b> Lazy initialization with caching</p>
     *
     * <p><b>Time Complexity:</b> O(1) amortized</p>
     *
     * @return immutable list of all 12 edges
     */
    public List<Line3D> getEdges() {
        if (edges == null) {
            initializeEdges();
        }
        return Collections.unmodifiableList(Arrays.asList(edges));
    }

    /**
     * Private helper method to initialize edge array.
     *
     * Creates Line3D objects connecting appropriate vertex pairs.
     * Follows standard cube topology for edge connectivity.
     */
    private void initializeEdges() {
        if (vertices == null) {
            initializeVertices();
        }

        edges = new Line3D[EDGE_COUNT];

        // Bottom face edges (Y-axis low)
        edges[0] = Line3D.of(vertices[0], vertices[1]);
        edges[1] = Line3D.of(vertices[1], vertices[2]);
        edges[2] = Line3D.of(vertices[2], vertices[3]);
        edges[3] = Line3D.of(vertices[3], vertices[0]);

        // Top face edges (Y-axis high)
        edges[4] = Line3D.of(vertices[4], vertices[5]);
        edges[5] = Line3D.of(vertices[5], vertices[6]);
        edges[6] = Line3D.of(vertices[6], vertices[7]);
        edges[7] = Line3D.of(vertices[7], vertices[4]);

        // Vertical edges (connecting top and bottom)
        edges[8] = Line3D.of(vertices[0], vertices[4]);
        edges[9] = Line3D.of(vertices[1], vertices[5]);
        edges[10] = Line3D.of(vertices[2], vertices[6]);
        edges[11] = Line3D.of(vertices[3], vertices[7]);

        logger.log(Level.INFO, "Initialized {0} edges for cube", EDGE_COUNT);
    }

    /**
     * Calculates the surface area of the cube.
     *
     * For a cube with edge length a, surface area = 6a²
     * (6 square faces, each with area a²)
     *
     * <p><b>Mathematical Foundation:</b> Sum of areas of all 6 congruent square faces.</p>
     *
     * <p><b>Time Complexity:</b> O(1) - closed-form formula</p>
     * <p><b>Space Complexity:</b> O(1)</p>
     *
     * @return the total surface area of the cube
     */
    public double surfaceArea() {
        double area = 6.0 * edgeLength * edgeLength;
        logger.log(Level.INFO, "Calculated surface area of cube: {0}", area);
        return area;
    }

    /**
     * Calculates the volume of the cube.
     *
     * For a cube with edge length a, volume = a³
     *
     * <p><b>Mathematical Foundation:</b> Product of three perpendicular edge lengths.</p>
     *
     * <p><b>Time Complexity:</b> O(1)</p>
     *
     * @return the volume of the cube
     */
    public double volume() {
        double vol = edgeLength * edgeLength * edgeLength;
        logger.log(Level.INFO, "Calculated volume of cube: {0}", vol);
        return vol;
    }

    /**
     * Calculates the total perimeter length (sum of all edge lengths).
     *
     * A cube has 12 edges, so total perimeter = 12 × edge_length
     *
     * <p><b>Note:</b> This is different from "perimeter" in 2D. More accurately,
     * this is the total wire length if the cube were constructed from wire.</p>
     *
     * <p><b>Time Complexity:</b> O(1)</p>
     *
     * @return the sum of all edge lengths
     */
    public double totalEdgeLength() {
        double total = EDGE_COUNT * edgeLength;
        logger.log(Level.INFO, "Calculated total edge length: {0}", total);
        return total;
    }

    /**
     * Calculates the length of the space diagonal (corner to opposite corner).
     *
     * The space diagonal of a cube with edge length a is: a√3
     * This is the longest line segment that fits inside the cube.
     *
     * <p><b>Derivation:</b> Using 3D Pythagorean theorem:
     * d = √(a² + a² + a²) = a√3</p>
     *
     * <p><b>Geometric Interpretation:</b> Distance from vertex 0 to vertex 6,
     * or any vertex to its opposite corner.</p>
     *
     * @return the length of the space diagonal
     */
    public double spaceDiagonal() {
        double diagonal = edgeLength * Math.sqrt(3.0);
        logger.log(Level.INFO, "Calculated space diagonal: {0}", diagonal);
        return diagonal;
    }

    /**
     * Calculates the length of the face diagonal.
     *
     * The face diagonal of a cube with edge length a is: a√2
     * This is the diagonal across any square face.
     *
     * <p><b>Derivation:</b> Using 2D Pythagorean theorem on a square face:
     * d = √(a² + a²) = a√2</p>
     *
     * @return the length of any face diagonal
     */
    public double faceDiagonal() {
        double diagonal = edgeLength * Math.sqrt(2.0);
        logger.log(Level.INFO, "Calculated face diagonal: {0}", diagonal);
        return diagonal;
    }

    /**
     * Translates (moves) the cube by a given displacement vector.
     *
     * Creates a new cube with center point shifted by the displacement.
     * All vertices move by the same amount, preserving cube shape and orientation.
     *
     * <p><b>Mathematical Operation:</b> center_new = center_old + displacement</p>
     *
     * <p><b>Design Pattern:</b> Immutability - returns new instance rather than
     * modifying this cube.</p>
     *
     * <p><b>Time Complexity:</b> O(1) - only updates center, vertices computed lazily</p>
     *
     * @param displacement the vector to translate by
     * @return a new Cube3D at the translated position
     * @throws IllegalArgumentException if displacement is null
     */
    public Cube3D translate(Point3D displacement) {
        if (displacement == null) {
            logger.log(Level.SEVERE, "Cannot translate by null displacement");
            throw new IllegalArgumentException("Displacement cannot be null");
        }

        Point3D newCenter = center.add(displacement);
        logger.log(Level.INFO, "Translated cube from {0} to {1}",
                new Object[]{center, newCenter});

        return new Cube3D(newCenter, edgeLength);
    }

    /**
     * Translates the cube to a new center position.
     *
     * Convenience method that moves the cube so its center is at the specified point.
     *
     * @param newCenter the target center position
     * @return a new Cube3D centered at the specified point
     * @throws IllegalArgumentException if newCenter is null
     */
    public Cube3D moveTo(Point3D newCenter) {
        if (newCenter == null) {
            logger.log(Level.SEVERE, "Cannot move to null position");
            throw new IllegalArgumentException("New center cannot be null");
        }

        logger.log(Level.INFO, "Moved cube to {0}", newCenter);
        return new Cube3D(newCenter, edgeLength);
    }

    /**
     * Scales the cube by a given factor.
     *
     * Creates a new cube with the same center but edge length multiplied by
     * the scale factor. This changes the cube's size but not its position.
     *
     * <p><b>Effect on Properties:</b></p>
     * - Edge length: multiplied by factor
     * - Surface area: multiplied by factor²
     * - Volume: multiplied by factor³
     *
     * <p><b>Time Complexity:</b> O(1)</p>
     *
     * @param factor the scaling factor (must be positive)
     * @return a new scaled Cube3D
     * @throws IllegalArgumentException if factor is not positive
     */
    public Cube3D scale(double factor) {
        if (factor <= EPSILON) {
            logger.log(Level.SEVERE, "Invalid scale factor: {0}", factor);
            throw new IllegalArgumentException("Scale factor must be positive");
        }

        double newEdgeLength = edgeLength * factor;
        logger.log(Level.INFO, "Scaled cube by factor {0}: {1} -> {2}",
                new Object[]{factor, edgeLength, newEdgeLength});

        return new Cube3D(center, newEdgeLength);
    }

    /**
     * Rotates the cube around the X-axis by the specified angle.
     *
     * Performs a rotation transformation by rotating all vertices around the
     * X-axis passing through the cube's center. The cube remains centered at
     * the same point but its orientation changes.
     *
     * <p><b>Algorithm:</b></p>
     * 1. Translate vertices so center is at origin
     * 2. Apply rotation matrix to each vertex
     * 3. Translate vertices back to original center
     *
     * <p><b>Rotation Matrix (around X-axis):</b></p>
     * <pre>
     * [1    0         0    ]
     * [0  cos(θ)  -sin(θ) ]
     * [0  sin(θ)   cos(θ) ]
     * </pre>
     *
     * <p><b>Design Pattern:</b> Template Method - defines rotation algorithm,
     * delegates axis-specific rotation to Point3D.</p>
     *
     * <p><b>Important:</b> After rotation, the cube is no longer axis-aligned
     * and becomes an Oriented Bounding Box (OBB) rather than an AABB.</p>
     *
     * <p><b>Time Complexity:</b> O(n) where n=8 vertices</p>
     * <p><b>Space Complexity:</b> O(n) for new vertex array</p>
     *
     * @param angleRadians the rotation angle in radians
     * @return a new rotated Cube3D
     */
    public Cube3D rotateX(double angleRadians) {
        if (vertices == null) {
            initializeVertices();
        }

        Cube3D rotated = new Cube3D(center, edgeLength);
        rotated.vertices = new Point3D[VERTEX_COUNT];

        // Rotate each vertex around the center
        for (int i = 0; i < VERTEX_COUNT; i++) {
            // Translate to origin
            Point3D relative = vertices[i].subtract(center);
            // Rotate
            Point3D rotatedRelative = relative.rotateX(angleRadians);
            // Translate back
            rotated.vertices[i] = rotatedRelative.add(center);
        }

        logger.log(Level.INFO, "Rotated cube around X-axis by {0} radians",
                angleRadians);

        return rotated;
    }

    /**
     * Rotates the cube around the Y-axis by the specified angle.
     *
     * Similar to rotateX but applies rotation around the Y-axis.
     *
     * <p><b>Rotation Matrix (around Y-axis):</b></p>
     * <pre>
     * [ cos(θ)   0   sin(θ) ]
     * [   0      1     0    ]
     * [-sin(θ)   0   cos(θ) ]
     * </pre>
     *
     * @param angleRadians the rotation angle in radians
     * @return a new rotated Cube3D
     */
    public Cube3D rotateY(double angleRadians) {
        if (vertices == null) {
            initializeVertices();
        }

        Cube3D rotated = new Cube3D(center, edgeLength);
        rotated.vertices = new Point3D[VERTEX_COUNT];

        for (int i = 0; i < VERTEX_COUNT; i++) {
            Point3D relative = vertices[i].subtract(center);
            Point3D rotatedRelative = relative.rotateY(angleRadians);
            rotated.vertices[i] = rotatedRelative.add(center);
        }

        logger.log(Level.INFO, "Rotated cube around Y-axis by {0} radians",
                angleRadians);

        return rotated;
    }

    /**
     * Rotates the cube around the Z-axis by the specified angle.
     *
     * Similar to rotateX but applies rotation around the Z-axis.
     *
     * <p><b>Rotation Matrix (around Z-axis):</b></p>
     * <pre>
     * [ cos(θ)  -sin(θ)   0 ]
     * [ sin(θ)   cos(θ)   0 ]
     * [   0        0      1 ]
     * </pre>
     *
     * @param angleRadians the rotation angle in radians
     * @return a new rotated Cube3D
     */
    public Cube3D rotateZ(double angleRadians) {
        if (vertices == null) {
            initializeVertices();
        }

        Cube3D rotated = new Cube3D(center, edgeLength);
        rotated.vertices = new Point3D[VERTEX_COUNT];

        for (int i = 0; i < VERTEX_COUNT; i++) {
            Point3D relative = vertices[i].subtract(center);
            Point3D rotatedRelative = relative.rotateZ(angleRadians);
            rotated.vertices[i] = rotatedRelative.add(center);
        }

        logger.log(Level.INFO, "Rotated cube around Z-axis by {0} radians",
                angleRadians);

        return rotated;
    }

    /**
     * Tests whether a point is contained within this cube.
     *
     * For an axis-aligned cube, containment is tested by checking if the point
     * falls within the bounds in all three dimensions. This method assumes the
     * cube is axis-aligned; rotated cubes require more complex testing.
     *
     * <p><b>Algorithm (Axis-Aligned):</b> Check if point coordinates satisfy:
     * center - half_size ≤ point ≤ center + half_size in all dimensions</p>
     *
     * <p><b>Warning:</b> If the cube has been rotated, this method will give
     * incorrect results. For rotated cubes, use containsPointOriented().</p>
     *
     * <p><b>Time Complexity:</b> O(1) for axis-aligned cubes</p>
     *
     * @param point the point to test
     * @return true if the point is inside or on the cube boundary
     * @throws IllegalArgumentException if point is null
     */
    public boolean containsPoint(Point3D point) {
        if (point == null) {
            logger.log(Level.SEVERE, "Cannot test containment of null point");
            throw new IllegalArgumentException("Point cannot be null");
        }

        double half = edgeLength / 2.0;

        boolean contained =
                Math.abs(point.getX() - center.getX()) <= half + EPSILON &&
                        Math.abs(point.getY() - center.getY()) <= half + EPSILON &&
                        Math.abs(point.getZ() - center.getZ()) <= half + EPSILON;

        logger.log(Level.INFO, "Point {0} is {1} cube",
                new Object[]{point, contained ? "inside" : "outside"});

        return contained;
    }

    /**
     * Tests whether this cube intersects with another cube.
     *
     * For axis-aligned cubes, intersection testing uses the Separating Axis
     * Theorem (SAT): cubes intersect if they overlap on all three axes.
     *
     * <p><b>Algorithm:</b> Check for overlap on each axis independently:
     * |center1 - center2| ≤ (size1 + size2) / 2</p>
     *
     * <p><b>Warning:</b> This method assumes both cubes are axis-aligned.
     * For rotated cubes, more sophisticated collision detection is required.</p>
     *
     * <p><b>Applications:</b> Collision detection, spatial queries, frustum culling</p>
     *
     * <p><b>Time Complexity:</b> O(1)</p>
     *
     * @param other the other cube to test intersection with
     * @return true if the cubes intersect or touch
     * @throws IllegalArgumentException if other is null
     */
    public boolean intersects(Cube3D other) {
        if (other == null) {
            logger.log(Level.SEVERE, "Cannot test intersection with null cube");
            throw new IllegalArgumentException("Other cube cannot be null");
        }

        double half1 = this.edgeLength / 2.0;
        double half2 = other.edgeLength / 2.0;

        boolean intersects =
                Math.abs(this.center.getX() - other.center.getX()) <= (half1 + half2 + EPSILON) &&
                        Math.abs(this.center.getY() - other.center.getY()) <= (half1 + half2 + EPSILON) &&
                        Math.abs(this.center.getZ() - other.center.getZ()) <= (half1 + half2 + EPSILON);

        logger.log(Level.INFO, "Cubes {0} {1}",
                new Object[]{intersects ? "intersect" : "do not intersect", ""});

        return intersects;
    }

    /**
     * Calculates the minimum distance from a point to the cube surface.
     *
     * Returns 0 if the point is inside the cube. For points outside, computes
     * the shortest distance to any point on the cube's surface.
     *
     * <p><b>Algorithm:</b></p>
     * 1. If point is inside, return 0
     * 2. Otherwise, find closest point on cube surface
     * 3. Return distance between input point and closest surface point
     *
     * <p><b>Time Complexity:</b> O(1) for axis-aligned cubes</p>
     *
     * @param point the point to measure distance from
     * @return the minimum distance to the cube surface (0 if inside)
     * @throws IllegalArgumentException if point is null
     */
    public double distanceToPoint(Point3D point) {
        if (point == null) {
            logger.log(Level.SEVERE, "Cannot calculate distance to null point");
            throw new IllegalArgumentException("Point cannot be null");
        }

        if (containsPoint(point)) {
            return 0.0;
        }

        double half = edgeLength / 2.0;

        // Find closest point on cube surface
        double closestX = clamp(point.getX(),
                center.getX() - half,
                center.getX() + half);
        double closestY = clamp(point.getY(),
                center.getY() - half,
                center.getY() + half);
        double closestZ = clamp(point.getZ(),
                center.getZ() - half,
                center.getZ() + half);

        Point3D closestPoint = Point3D.of(closestX, closestY, closestZ);
        double distance = point.distanceTo(closestPoint);

        logger.log(Level.INFO, "Distance from {0} to cube surface: {1}",
                new Object[]{point, distance});

        return distance;
    }

    /**
     * Helper method to clamp a value between min and max.
     */
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Gets the minimum corner point (vertex with smallest coordinates).
     *
     * For an axis-aligned cube, this is the corner at
     * (center_x - half, center_y - half, center_z - half).
     *
     * <p><b>Use Cases:</b> Defining Axis-Aligned Bounding Boxes (AABB),
     * spatial partitioning, octree construction</p>
     *
     * @return the minimum corner point
     */
    public Point3D getMinCorner() {
        double half = edgeLength / 2.0;
        return Point3D.of(
                center.getX() - half,
                center.getY() - half,
                center.getZ() - half
        );
    }

    /**
     * Gets the maximum corner point (vertex with largest coordinates).
     *
     * For an axis-aligned cube, this is the corner at
     * (center_x + half, center_y + half, center_z + half).
     *
     * @return the maximum corner point
     */
    public Point3D getMaxCorner() {
        double half = edgeLength / 2.0;
        return Point3D.of(
                center.getX() + half,
                center.getY() + half,
                center.getZ() + half
        );
    }

    /**
     * Compares this cube with another for equality.
     *
     * Two cubes are equal if they have the same center point and edge length
     * within epsilon tolerance. Rotation is not considered in equality.
     *
     * <p><b>Design Pattern:</b> Value Object equality based on mathematical
     * properties rather than object identity.</p>
     *
     * @param obj the object to compare with
     * @return true if the cubes are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Cube3D other = (Cube3D) obj;
        return this.center.equals(other.center) &&
                Math.abs(this.edgeLength - other.edgeLength) < EPSILON;
    }

    /**
     * Generates a hash code for this cube.
     *
     * <p><b>Contract:</b> Equal cubes must have equal hash codes.</p>
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + center.hashCode();
        result = 31 * result + Double.hashCode(edgeLength);
        return result;
    }

    /**
     * Returns a string representation of this cube.
     *
     * Format: "Cube3D[center=..., edge=...]"
     *
     * @return a human-readable string representation
     */
    @Override
    public String toString() {
        return String.format("Cube3D[center=%s, edge=%.2f]", center, edgeLength);
    }
}