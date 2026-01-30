package com.csc205.project1;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Optional;

/**
 * Represents a line segment in three-dimensional Cartesian space.
 *
 * This class provides a robust implementation of a 3D line segment defined by
 * two endpoints. It offers mathematical operations commonly used in computational
 * geometry, computer graphics, collision detection, and spatial analysis.
 *
 * <p>A line segment is the portion of a line between two points, with finite
 * length. This differs from an infinite line or ray. For infinite line
 * calculations, see methods that work with the line's direction vector.</p>
 *
 * <p>Design Patterns & Principles:</p>
 * <ul>
 *   <li><b>Composition Pattern:</b> This class is composed of two Point3D objects,
 *       demonstrating "has-a" relationship and code reuse through composition
 *       rather than inheritance.</li>
 *   <li><b>Immutability Pattern:</b> Like Point3D, this class is immutable with
 *       final fields. All transformation methods return new Line3D instances,
 *       ensuring thread-safety and preventing defensive copying.</li>
 *   <li><b>Value Object Pattern:</b> Two lines with the same endpoints are
 *       considered equal regardless of creation context.</li>
 *   <li><b>Null Object Pattern (via Optional):</b> Methods that may not have
 *       valid results return Optional rather than null, preventing
 *       NullPointerExceptions and making the API more explicit.</li>
 *   <li><b>Factory Method Pattern:</b> Static factory methods provide clear,
 *       intention-revealing constructors.</li>
 * </ul>
 *
 * <p>Algorithmic Foundation:</p>
 * This class demonstrates advanced computational geometry algorithms:
 * - Vector arithmetic and projections
 * - Parametric line representation: P(t) = P₀ + t·d
 * - Closest point calculations (point-to-line, line-to-line)
 * - Skew line distance computation in 3D space
 * - Intersection detection with various geometric primitives
 *
 * <p><b>Coordinate System:</b> Uses right-handed Cartesian coordinates with
 * standard basis vectors.</p>
 *
 * @author David Rodriguez
 * @version 1.0
 * @since 2026-01-27
 */
public final class Line3D {

    private static final Logger logger = Logger.getLogger(Line3D.class.getName());

    // Immutable endpoints - demonstrates composition and encapsulation
    private final Point3D start;
    private final Point3D end;

    // Epsilon for floating-point comparisons
    private static final double EPSILON = 1e-10;

    /**
     * Constructs a new Line3D with the specified start and end points.
     *
     * This is the primary constructor. The order of points matters for certain
     * operations (e.g., direction vector). For external use, consider the
     * static factory method {@link #of(Point3D, Point3D)}.
     *
     * <p><b>Validation:</b> This constructor checks that the two points are
     * distinct (not coincident) to ensure a valid line segment is created.</p>
     *
     * <p><b>Time Complexity:</b> O(1)</p>
     * <p><b>Space Complexity:</b> O(1)</p>
     *
     * @param start the starting point of the line segment
     * @param end the ending point of the line segment
     * @throws IllegalArgumentException if either point is null or if points are coincident
     */
    public Line3D(Point3D start, Point3D end) {
        if (start == null || end == null) {
            logger.log(Level.SEVERE, "Cannot create Line3D with null points");
            throw new IllegalArgumentException("Start and end points cannot be null");
        }

        if (start.equals(end)) {
            logger.log(Level.SEVERE,
                    "Cannot create Line3D with coincident points: {0}", start);
            throw new IllegalArgumentException(
                    "Start and end points must be distinct to form a line segment");
        }

        this.start = start;
        this.end = end;

        logger.log(Level.INFO, "Created Line3D from {0} to {1}",
                new Object[]{start, end});
    }

    /**
     * Static factory method for creating a Line3D instance.
     *
     * This method provides a more fluent API compared to the constructor,
     * improving code readability.
     * Example: Line3D.of(Point3D.of(0, 0, 0), Point3D.of(1, 1, 1))
     *
     * <p><b>Design Pattern:</b> Static Factory Method - provides named
     * constructor with clearer semantics than 'new Line3D()'</p>
     *
     * @param start the starting point
     * @param end the ending point
     * @return a new Line3D instance
     * @throws IllegalArgumentException if validation fails
     */
    public static Line3D of(Point3D start, Point3D end) {
        return new Line3D(start, end);
    }

    /**
     * Creates a Line3D from a point and a direction vector.
     *
     * This factory method constructs a line segment of specified length
     * starting from the given point and extending in the direction of the
     * direction vector.
     *
     * <p><b>Mathematical Representation:</b> L(t) = start + t·direction,
     * where t ∈ [0, length]</p>
     *
     * @param start the starting point
     * @param direction the direction vector (will be normalized)
     * @param length the desired length of the line segment
     * @return a new Line3D instance
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public static Line3D fromDirection(Point3D start, Point3D direction, double length) {
        if (start == null || direction == null) {
            logger.log(Level.SEVERE, "Cannot create Line3D from null parameters");
            throw new IllegalArgumentException("Start and direction cannot be null");
        }

        if (length <= EPSILON) {
            logger.log(Level.SEVERE, "Invalid line length: {0}", length);
            throw new IllegalArgumentException("Length must be positive");
        }

        Point3D normalizedDir = direction.normalize();
        Point3D end = start.add(normalizedDir.scale(length));

        logger.log(Level.INFO,
                "Created Line3D from point {0} with direction {1} and length {2}",
                new Object[]{start, direction, length});

        return new Line3D(start, end);
    }

    // Accessor methods - demonstrates encapsulation

    /**
     * Returns the starting point of this line segment.
     *
     * <p><b>Immutability:</b> Since Point3D is immutable, returning the
     * reference directly is safe and doesn't break encapsulation.</p>
     *
     * @return the start point
     */
    public Point3D getStart() {
        return start;
    }

    /**
     * Returns the ending point of this line segment.
     *
     * @return the end point
     */
    public Point3D getEnd() {
        return end;
    }

    /**
     * Calculates the length of this line segment.
     *
     * This method computes the Euclidean distance between the start and end
     * points using the distance formula in 3D space.
     *
     * <p><b>Algorithm:</b> Delegates to Point3D.distanceTo() which implements
     * the standard Euclidean distance calculation.</p>
     *
     * <p><b>Time Complexity:</b> O(1) - constant time calculation</p>
     * <p><b>Space Complexity:</b> O(1) - no additional space required</p>
     *
     * @return the length of the line segment
     */
    public double length() {
        double len = start.distanceTo(end);
        logger.log(Level.INFO, "Calculated length of {0}: {1}",
                new Object[]{this, len});
        return len;
    }

    /**
     * Returns the direction vector of this line.
     *
     * The direction vector points from the start point to the end point.
     * This is an unnormalized vector with magnitude equal to the line's length.
     *
     * <p><b>Mathematical Definition:</b> d = end - start</p>
     *
     * <p><b>Use Cases:</b></p>
     * <ul>
     *   <li>Parametric line equations</li>
     *   <li>Determining line orientation</li>
     *   <li>Parallel line detection</li>
     * </ul>
     *
     * @return a Point3D representing the direction vector
     */
    public Point3D getDirection() {
        return end.subtract(start);
    }

    /**
     * Returns the normalized direction vector of this line.
     *
     * This is a unit vector (magnitude = 1) pointing from start to end.
     * Useful for direction-only calculations where length is irrelevant.
     *
     * @return a normalized Point3D representing the direction
     */
    public Point3D getDirectionNormalized() {
        return getDirection().normalize();
    }

    /**
     * Calculates the midpoint of this line segment.
     *
     * The midpoint is the point exactly halfway between the start and end points.
     * Formula: midpoint = (start + end) / 2
     *
     * <p><b>Geometric Property:</b> The midpoint divides the line segment into
     * two equal parts.</p>
     *
     * <p><b>Time Complexity:</b> O(1)</p>
     *
     * @return a Point3D representing the midpoint
     */
    public Point3D midpoint() {
        Point3D mid = start.add(end).scale(0.5);
        logger.log(Level.INFO, "Calculated midpoint of {0}: {1}",
                new Object[]{this, mid});
        return mid;
    }

    /**
     * Finds the point on this line segment closest to a given point.
     *
     * This method implements the point-to-line-segment distance algorithm by:
     * 1. Projecting the point onto the infinite line
     * 2. Clamping the projection to the line segment bounds [0, 1]
     * 3. Computing the resulting closest point
     *
     * <p><b>Algorithm Details:</b></p>
     * The parametric line equation is: P(t) = start + t·(end - start)
     * where t ∈ [0, 1] for the segment.
     *
     * We find t by projecting: t = (point - start)·direction / |direction|²
     * Then clamp t to [0, 1] to stay within the segment bounds.
     *
     * <p><b>Edge Cases:</b></p>
     * - If t < 0: closest point is the start point
     * - If t > 1: closest point is the end point
     * - If 0 ≤ t ≤ 1: closest point is on the segment interior
     *
     * <p><b>Time Complexity:</b> O(1)</p>
     * <p><b>Space Complexity:</b> O(1)</p>
     *
     * @param point the point to find the closest point to
     * @return the point on this line segment closest to the given point
     * @throws IllegalArgumentException if point is null
     */
    public Point3D closestPointTo(Point3D point) {
        if (point == null) {
            logger.log(Level.SEVERE, "Cannot find closest point to null");
            throw new IllegalArgumentException("Point cannot be null");
        }

        Point3D direction = getDirection();
        Point3D startToPoint = point.subtract(start);

        // Project point onto line using dot product
        double directionLengthSquared = direction.dot(direction);
        double t = startToPoint.dot(direction) / directionLengthSquared;

        // Clamp t to [0, 1] to stay within segment
        t = Math.max(0.0, Math.min(1.0, t));

        Point3D closest = start.add(direction.scale(t));

        logger.log(Level.INFO,
                "Found closest point on {0} to {1}: {2} (t={3})",
                new Object[]{this, point, closest, t});

        return closest;
    }

    /**
     * Calculates the shortest distance from a point to this line segment.
     *
     * This is the Euclidean distance from the point to its closest point on
     * the line segment. The method delegates to {@link #closestPointTo(Point3D)}
     * and then calculates the distance.
     *
     * <p><b>Geometric Interpretation:</b> This represents the perpendicular
     * distance from the point to the line (if the perpendicular intersects
     * the segment), or the distance to the nearest endpoint.</p>
     *
     * <p><b>Time Complexity:</b> O(1)</p>
     *
     * @param point the point to measure distance from
     * @return the shortest distance from the point to this line segment
     * @throws IllegalArgumentException if point is null
     */
    public double distanceToPoint(Point3D point) {
        if (point == null) {
            logger.log(Level.SEVERE, "Cannot calculate distance to null point");
            throw new IllegalArgumentException("Point cannot be null");
        }

        Point3D closest = closestPointTo(point);
        double distance = point.distanceTo(closest);

        logger.log(Level.INFO, "Distance from point {0} to {1}: {2}",
                new Object[]{point, this, distance});

        return distance;
    }

    /**
     * Calculates the shortest distance between this line segment and another.
     *
     * This method handles the complex case of computing distance between two
     * line segments in 3D space, which can be:
     * - Zero (if lines intersect)
     * - Distance between closest points on the segments
     * - Distance between an endpoint and the other segment
     *
     * <p><b>Algorithm Overview:</b></p>
     * 1. Check if lines are parallel (direction vectors are parallel)
     * 2. For non-parallel lines, find closest points using the parametric method
     * 3. Clamp parameters to [0, 1] for segment bounds
     * 4. Handle edge cases where optimal points lie outside segment bounds
     *
     * <p><b>Mathematical Foundation:</b></p>
     * For two lines L1(s) = P1 + s·d1 and L2(t) = P2 + t·d2:
     * - Minimize |L1(s) - L2(t)|²
     * - This leads to a system of linear equations solved for s and t
     * - The method uses the cross product to detect parallel lines
     *
     * <p><b>Special Cases:</b></p>
     * - Parallel lines: Use perpendicular distance formula
     * - Intersecting lines: Distance is zero
     * - Skew lines: Distance is the minimum across all point pairs
     *
     * <p><b>Time Complexity:</b> O(1) - fixed number of operations</p>
     * <p><b>Space Complexity:</b> O(1)</p>
     *
     * @param other the other line segment
     * @return the shortest distance between the two line segments
     * @throws IllegalArgumentException if other is null
     */
    public double distanceToLine(Line3D other) {
        if (other == null) {
            logger.log(Level.SEVERE, "Cannot calculate distance to null line");
            throw new IllegalArgumentException("Other line cannot be null");
        }

        Point3D d1 = this.getDirection();
        Point3D d2 = other.getDirection();
        Point3D w0 = this.start.subtract(other.start);

        double a = d1.dot(d1);  // |d1|²
        double b = d1.dot(d2);  // d1·d2
        double c = d2.dot(d2);  // |d2|²
        double d = d1.dot(w0);  // d1·w0
        double e = d2.dot(w0);  // d2·w0

        double denominator = a * c - b * b;

        double s, t;

        // Check if lines are parallel (denominator near zero)
        if (Math.abs(denominator) < EPSILON) {
            logger.log(Level.INFO, "Lines are parallel, using perpendicular distance");

            // Lines are parallel - find distance between any point on one line
            // to the other line
            s = 0.0;
            t = (b > c ? d / b : e / c);
            t = Math.max(0.0, Math.min(1.0, t));
        } else {
            // Lines are not parallel - find closest points
            s = (b * e - c * d) / denominator;
            t = (a * e - b * d) / denominator;

            // Clamp s and t to [0, 1] for segment bounds
            s = Math.max(0.0, Math.min(1.0, s));
            t = Math.max(0.0, Math.min(1.0, t));
        }

        // Calculate the two closest points
        Point3D point1 = this.start.add(d1.scale(s));
        Point3D point2 = other.start.add(d2.scale(t));

        double distance = point1.distanceTo(point2);

        // Handle edge cases - check if clamping affected the result
        // If so, we need to check endpoint-to-segment distances
        if (s == 0.0 || s == 1.0 || t == 0.0 || t == 1.0) {
            double d1_start = other.distanceToPoint(this.start);
            double d1_end = other.distanceToPoint(this.end);
            double d2_start = this.distanceToPoint(other.start);
            double d2_end = this.distanceToPoint(other.end);

            distance = Math.min(distance,
                    Math.min(Math.min(d1_start, d1_end),
                            Math.min(d2_start, d2_end)));
        }

        logger.log(Level.INFO, "Distance between {0} and {1}: {2}",
                new Object[]{this, other, distance});

        return distance;
    }

    /**
     * Determines if this line segment is parallel to another line segment.
     *
     * Two lines are parallel if their direction vectors are parallel, which
     * occurs when their cross product is the zero vector (or very close to it).
     *
     * <p><b>Mathematical Test:</b> Lines are parallel if d1 × d2 ≈ 0</p>
     *
     * <p><b>Implementation Note:</b> Uses epsilon comparison to handle
     * floating-point precision issues.</p>
     *
     * @param other the other line to test
     * @return true if the lines are parallel (within epsilon tolerance)
     * @throws IllegalArgumentException if other is null
     */
    public boolean isParallelTo(Line3D other) {
        if (other == null) {
            logger.log(Level.SEVERE, "Cannot check parallelism with null line");
            throw new IllegalArgumentException("Other line cannot be null");
        }

        Point3D cross = this.getDirectionNormalized().cross(other.getDirectionNormalized());
        boolean parallel = cross.magnitude() < EPSILON;

        logger.log(Level.INFO, "{0} is {1} to {2}",
                new Object[]{this, parallel ? "parallel" : "not parallel", other});

        return parallel;
    }

    /**
     * Determines if this line segment is perpendicular to another line segment.
     *
     * Two lines are perpendicular if their direction vectors are orthogonal,
     * which occurs when their dot product is zero.
     *
     * <p><b>Mathematical Test:</b> Lines are perpendicular if d1 · d2 ≈ 0</p>
     *
     * @param other the other line to test
     * @return true if the lines are perpendicular (within epsilon tolerance)
     * @throws IllegalArgumentException if other is null
     */
    public boolean isPerpendicularTo(Line3D other) {
        if (other == null) {
            logger.log(Level.SEVERE, "Cannot check perpendicularity with null line");
            throw new IllegalArgumentException("Other line cannot be null");
        }

        double dotProduct = this.getDirectionNormalized()
                .dot(other.getDirectionNormalized());
        boolean perpendicular = Math.abs(dotProduct) < EPSILON;

        logger.log(Level.INFO, "{0} is {1} to {2}",
                new Object[]{this,
                        perpendicular ? "perpendicular" : "not perpendicular",
                        other});

        return perpendicular;
    }

    /**
     * Attempts to find the intersection point between this line and another.
     *
     * In 3D space, two line segments generally do not intersect unless they
     * are coplanar and their paths cross. This method checks for intersection
     * and returns the intersection point if it exists.
     *
     * <p><b>Algorithm:</b></p>
     * 1. Solve the parametric equations: P1 + s·d1 = P2 + t·d2
     * 2. Check if solution exists with s,t ∈ [0,1]
     * 3. Verify points are actually coincident (accounting for floating-point error)
     *
     * <p><b>Return Value:</b> Uses Optional to explicitly indicate that
     * intersection may not exist, following the Null Object pattern and
     * preventing NullPointerExceptions.</p>
     *
     * <p><b>Special Cases:</b></p>
     * - Parallel lines: No intersection (unless coincident, not handled)
     * - Skew lines: No intersection
     * - Coplanar intersecting lines: Returns intersection point
     *
     * @param other the other line to check for intersection
     * @return Optional containing intersection point if exists, empty otherwise
     * @throws IllegalArgumentException if other is null
     */
    public Optional<Point3D> intersectionWith(Line3D other) {
        if (other == null) {
            logger.log(Level.SEVERE, "Cannot find intersection with null line");
            throw new IllegalArgumentException("Other line cannot be null");
        }

        // Check if lines are parallel first
        if (isParallelTo(other)) {
            logger.log(Level.INFO, "Lines are parallel, no intersection");
            return Optional.empty();
        }

        Point3D d1 = this.getDirection();
        Point3D d2 = other.getDirection();
        Point3D w0 = this.start.subtract(other.start);

        double a = d1.dot(d1);
        double b = d1.dot(d2);
        double c = d2.dot(d2);
        double d = d1.dot(w0);
        double e = d2.dot(w0);

        double denominator = a * c - b * b;

        if (Math.abs(denominator) < EPSILON) {
            return Optional.empty();
        }

        double s = (b * e - c * d) / denominator;
        double t = (a * e - b * d) / denominator;

        // Check if intersection is within both segments
        if (s < 0.0 || s > 1.0 || t < 0.0 || t > 1.0) {
            logger.log(Level.INFO,
                    "Intersection outside segment bounds (s={0}, t={1})",
                    new Object[]{s, t});
            return Optional.empty();
        }

        // Calculate both points and verify they're actually the same
        Point3D point1 = this.start.add(d1.scale(s));
        Point3D point2 = other.start.add(d2.scale(t));

        // If points are not close enough, lines don't actually intersect
        if (point1.distanceTo(point2) > EPSILON) {
            logger.log(Level.INFO, "Lines are skew, no intersection");
            return Optional.empty();
        }

        logger.log(Level.INFO, "Found intersection at {0}", point1);
        return Optional.of(point1);
    }

    /**
     * Reverses the direction of this line segment.
     *
     * Creates a new Line3D with start and end points swapped. This is useful
     * for operations where direction matters (e.g., ray tracing, normal calculations).
     *
     * <p><b>Immutability:</b> Returns a new instance rather than modifying this one.</p>
     *
     * @return a new Line3D with reversed direction
     */
    public Line3D reverse() {
        logger.log(Level.INFO, "Reversed direction of {0}", this);
        return new Line3D(end, start);
    }

    /**
     * Extends this line segment by a specified factor.
     *
     * Creates a new line segment with the same midpoint and direction but
     * with length multiplied by the given factor.
     *
     * <p><b>Example:</b> A factor of 2.0 doubles the length while maintaining
     * the same center point. A factor of 0.5 halves the length.</p>
     *
     * @param factor the scaling factor (must be positive)
     * @return a new scaled Line3D
     * @throws IllegalArgumentException if factor is not positive
     */
    public Line3D scale(double factor) {
        if (factor <= 0) {
            logger.log(Level.SEVERE, "Invalid scale factor: {0}", factor);
            throw new IllegalArgumentException("Scale factor must be positive");
        }

        Point3D mid = midpoint();
        Point3D direction = getDirection().scale(0.5 * factor);

        Point3D newStart = mid.subtract(direction);
        Point3D newEnd = mid.add(direction);

        logger.log(Level.INFO, "Scaled {0} by factor {1}",
                new Object[]{this, factor});

        return new Line3D(newStart, newEnd);
    }

    /**
     * Compares this line with another for equality.
     *
     * Two lines are considered equal if they have the same start and end points
     * (within epsilon tolerance). Note that reversed lines are NOT equal by
     * this definition.
     *
     * <p><b>Design Pattern:</b> Implements Value Object equality contract.</p>
     *
     * @param obj the object to compare with
     * @return true if the lines are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Line3D other = (Line3D) obj;
        return this.start.equals(other.start) && this.end.equals(other.end);
    }

    /**
     * Generates a hash code for this line.
     *
     * <p><b>Contract:</b> Equal objects must have equal hash codes.</p>
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + start.hashCode();
        result = 31 * result + end.hashCode();
        return result;
    }

    /**
     * Returns a string representation of this line.
     *
     * Format: "Line3D[start -> end]"
     *
     * @return a human-readable string representation
     */
    @Override
    public String toString() {
        return String.format("Line3D[%s -> %s]", start, end);
    }
}