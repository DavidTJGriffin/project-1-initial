package com.csc205.project1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit test suite for Point3D class.
 *
 * This test class demonstrates thorough testing practices following industry
 * standards and JUnit 5 best practices. It covers:
 * - Normal cases (typical usage scenarios)
 * - Edge cases (boundary conditions, extreme values)
 * - Corner cases (unusual but valid combinations)
 * - Error cases (invalid inputs, null handling)
 *
 * <p><b>Testing Patterns Used:</b></p>
 * <ul>
 *   <li><b>Arrange-Act-Assert (AAA):</b> Clear test structure for readability</li>
 *   <li><b>Nested Test Classes:</b> Logical grouping of related tests</li>
 *   <li><b>Parameterized Tests:</b> Testing multiple inputs efficiently</li>
 *   <li><b>Descriptive Naming:</b> Test names clearly describe what is being tested</li>
 *   <li><b>Epsilon-based Assertions:</b> Proper floating-point comparison</li>
 * </ul>
 *
 * <p><b>Test Coverage Categories:</b></p>
 * - Construction and factory methods
 * - Accessor methods
 * - Distance calculations
 * - Vector operations (add, subtract, scale)
 * - Rotation transformations
 * - Dot and cross products
 * - Normalization
 * - Equality and hash code contracts
 * - Null handling and error conditions
 *
 * @author David Griffin
 * @version 1.0
 * @since 2026-01-27
 */
@DisplayName("Point3D Unit Tests")
class Point3DTest {

    private static final double EPSILON = 1e-10;
    private static final double DELTA = 1e-9;  // For assertion tolerance

    @Nested
    @DisplayName("Construction and Factory Methods")
    class ConstructionTests {

        @Test
        @DisplayName("Should create point with valid coordinates")
        void testConstructorWithValidCoordinates() {
            // Arrange & Act
            Point3D point = new Point3D(1.0, 2.0, 3.0);

            // Assert
            assertNotNull(point);
            assertEquals(1.0, point.getX(), DELTA);
            assertEquals(2.0, point.getY(), DELTA);
            assertEquals(3.0, point.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should create point using static factory method 'of'")
        void testStaticFactoryMethodOf() {
            // Arrange & Act
            Point3D point = Point3D.of(5.0, -3.0, 7.5);

            // Assert
            assertNotNull(point);
            assertEquals(5.0, point.getX(), DELTA);
            assertEquals(-3.0, point.getY(), DELTA);
            assertEquals(7.5, point.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should create origin point at (0, 0, 0)")
        void testOriginFactory() {
            // Arrange & Act
            Point3D origin = Point3D.origin();

            // Assert
            assertNotNull(origin);
            assertEquals(0.0, origin.getX(), DELTA);
            assertEquals(0.0, origin.getY(), DELTA);
            assertEquals(0.0, origin.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should handle negative coordinates")
        void testNegativeCoordinates() {
            // Arrange & Act
            Point3D point = new Point3D(-1.5, -2.5, -3.5);

            // Assert
            assertEquals(-1.5, point.getX(), DELTA);
            assertEquals(-2.5, point.getY(), DELTA);
            assertEquals(-3.5, point.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should handle zero coordinates")
        void testZeroCoordinates() {
            // Arrange & Act
            Point3D point = new Point3D(0.0, 0.0, 0.0);

            // Assert
            assertEquals(0.0, point.getX(), DELTA);
            assertEquals(0.0, point.getY(), DELTA);
            assertEquals(0.0, point.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should handle very large coordinates")
        void testVeryLargeCoordinates() {
            // Arrange & Act
            Point3D point = new Point3D(1e100, 1e100, 1e100);

            // Assert
            assertEquals(1e100, point.getX(), DELTA);
            assertEquals(1e100, point.getY(), DELTA);
            assertEquals(1e100, point.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should handle very small coordinates")
        void testVerySmallCoordinates() {
            // Arrange & Act
            Point3D point = new Point3D(1e-100, 1e-100, 1e-100);

            // Assert
            assertEquals(1e-100, point.getX(), DELTA);
            assertEquals(1e-100, point.getY(), DELTA);
            assertEquals(1e-100, point.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should handle mixed positive and negative coordinates")
        void testMixedSignCoordinates() {
            // Arrange & Act
            Point3D point = new Point3D(-5.0, 3.0, -2.0);

            // Assert
            assertEquals(-5.0, point.getX(), DELTA);
            assertEquals(3.0, point.getY(), DELTA);
            assertEquals(-2.0, point.getZ(), DELTA);
        }
    }

    @Nested
    @DisplayName("Distance Calculations")
    class DistanceTests {

        private Point3D origin;

        @BeforeEach
        void setUp() {
            origin = Point3D.origin();
        }

        @Test
        @DisplayName("Should calculate distance between two points correctly")
        void testDistanceTo() {
            // Arrange
            Point3D p1 = new Point3D(0.0, 0.0, 0.0);
            Point3D p2 = new Point3D(3.0, 4.0, 0.0);

            // Act
            double distance = p1.distanceTo(p2);

            // Assert
            assertEquals(5.0, distance, DELTA);  // 3-4-5 triangle
        }

        @Test
        @DisplayName("Should calculate 3D distance correctly")
        void testDistanceTo3D() {
            // Arrange
            Point3D p1 = new Point3D(1.0, 2.0, 3.0);
            Point3D p2 = new Point3D(4.0, 6.0, 8.0);

            // Act
            double distance = p1.distanceTo(p2);

            // Assert
            // sqrt((4-1)² + (6-2)² + (8-3)²) = sqrt(9 + 16 + 25) = sqrt(50)
            double expected = Math.sqrt(50);
            assertEquals(expected, distance, DELTA);
        }

        @Test
        @DisplayName("Should return zero distance for same point")
        void testDistanceToSamePoint() {
            // Arrange
            Point3D point = new Point3D(5.0, 5.0, 5.0);

            // Act
            double distance = point.distanceTo(point);

            // Assert
            assertEquals(0.0, distance, DELTA);
        }

        @Test
        @DisplayName("Should calculate distance symmetrically")
        void testDistanceSymmetry() {
            // Arrange
            Point3D p1 = new Point3D(1.0, 2.0, 3.0);
            Point3D p2 = new Point3D(7.0, 8.0, 9.0);

            // Act
            double distance1 = p1.distanceTo(p2);
            double distance2 = p2.distanceTo(p1);

            // Assert
            assertEquals(distance1, distance2, DELTA);
        }

        @Test
        @DisplayName("Should throw exception when calculating distance to null")
        void testDistanceToNull() {
            // Arrange
            Point3D point = new Point3D(1.0, 2.0, 3.0);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                point.distanceTo(null);
            });
        }

        @Test
        @DisplayName("Should calculate squared distance correctly")
        void testDistanceSquared() {
            // Arrange
            Point3D p1 = new Point3D(0.0, 0.0, 0.0);
            Point3D p2 = new Point3D(3.0, 4.0, 0.0);

            // Act
            double distanceSquared = p1.distanceSquared(p2);

            // Assert
            assertEquals(25.0, distanceSquared, DELTA);  // 3² + 4² = 25
        }

        @Test
        @DisplayName("Should match squared distance with regular distance")
        void testDistanceSquaredConsistency() {
            // Arrange
            Point3D p1 = new Point3D(1.0, 2.0, 3.0);
            Point3D p2 = new Point3D(4.0, 5.0, 6.0);

            // Act
            double distance = p1.distanceTo(p2);
            double distanceSquared = p1.distanceSquared(p2);

            // Assert
            assertEquals(distance * distance, distanceSquared, DELTA);
        }

        @Test
        @DisplayName("Should calculate magnitude from origin")
        void testMagnitude() {
            // Arrange
            Point3D point = new Point3D(3.0, 4.0, 0.0);

            // Act
            double magnitude = point.magnitude();

            // Assert
            assertEquals(5.0, magnitude, DELTA);
        }

        @Test
        @DisplayName("Should calculate magnitude for 3D point")
        void testMagnitude3D() {
            // Arrange
            Point3D point = new Point3D(1.0, 2.0, 2.0);

            // Act
            double magnitude = point.magnitude();

            // Assert
            assertEquals(3.0, magnitude, DELTA);  // sqrt(1 + 4 + 4) = 3
        }

        @Test
        @DisplayName("Should return zero magnitude for origin")
        void testMagnitudeOfOrigin() {
            // Arrange
            Point3D origin = Point3D.origin();

            // Act
            double magnitude = origin.magnitude();

            // Assert
            assertEquals(0.0, magnitude, DELTA);
        }

        @ParameterizedTest
        @CsvSource({
                "1.0, 0.0, 0.0, 1.0",
                "0.0, 1.0, 0.0, 1.0",
                "0.0, 0.0, 1.0, 1.0",
                "3.0, 4.0, 0.0, 5.0",
                "1.0, 1.0, 1.0, 1.732050807568877"
        })
        @DisplayName("Should calculate magnitude correctly for various points")
        void testMagnitudeParameterized(double x, double y, double z, double expectedMagnitude) {
            // Arrange
            Point3D point = new Point3D(x, y, z);

            // Act
            double magnitude = point.magnitude();

            // Assert
            assertEquals(expectedMagnitude, magnitude, DELTA);
        }
    }

    @Nested
    @DisplayName("Vector Arithmetic Operations")
    class VectorArithmeticTests {

        @Test
        @DisplayName("Should add two points correctly")
        void testAdd() {
            // Arrange
            Point3D p1 = new Point3D(1.0, 2.0, 3.0);
            Point3D p2 = new Point3D(4.0, 5.0, 6.0);

            // Act
            Point3D result = p1.add(p2);

            // Assert
            assertEquals(5.0, result.getX(), DELTA);
            assertEquals(7.0, result.getY(), DELTA);
            assertEquals(9.0, result.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should add with negative values")
        void testAddWithNegatives() {
            // Arrange
            Point3D p1 = new Point3D(5.0, -3.0, 2.0);
            Point3D p2 = new Point3D(-2.0, 3.0, -1.0);

            // Act
            Point3D result = p1.add(p2);

            // Assert
            assertEquals(3.0, result.getX(), DELTA);
            assertEquals(0.0, result.getY(), DELTA);
            assertEquals(1.0, result.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should not modify original points when adding")
        void testAddImmutability() {
            // Arrange
            Point3D p1 = new Point3D(1.0, 2.0, 3.0);
            Point3D p2 = new Point3D(4.0, 5.0, 6.0);

            // Act
            Point3D result = p1.add(p2);

            // Assert
            assertEquals(1.0, p1.getX(), DELTA);
            assertEquals(2.0, p1.getY(), DELTA);
            assertEquals(3.0, p1.getZ(), DELTA);
            assertEquals(4.0, p2.getX(), DELTA);
            assertEquals(5.0, p2.getY(), DELTA);
            assertEquals(6.0, p2.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should throw exception when adding null point")
        void testAddNull() {
            // Arrange
            Point3D point = new Point3D(1.0, 2.0, 3.0);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                point.add(null);
            });
        }

        @Test
        @DisplayName("Should subtract two points correctly")
        void testSubtract() {
            // Arrange
            Point3D p1 = new Point3D(5.0, 7.0, 9.0);
            Point3D p2 = new Point3D(2.0, 3.0, 4.0);

            // Act
            Point3D result = p1.subtract(p2);

            // Assert
            assertEquals(3.0, result.getX(), DELTA);
            assertEquals(4.0, result.getY(), DELTA);
            assertEquals(5.0, result.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should subtract with negative values")
        void testSubtractWithNegatives() {
            // Arrange
            Point3D p1 = new Point3D(3.0, -2.0, 5.0);
            Point3D p2 = new Point3D(1.0, -4.0, 2.0);

            // Act
            Point3D result = p1.subtract(p2);

            // Assert
            assertEquals(2.0, result.getX(), DELTA);
            assertEquals(2.0, result.getY(), DELTA);
            assertEquals(3.0, result.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should result in zero vector when subtracting point from itself")
        void testSubtractSelf() {
            // Arrange
            Point3D point = new Point3D(5.0, 5.0, 5.0);

            // Act
            Point3D result = point.subtract(point);

            // Assert
            assertEquals(0.0, result.getX(), DELTA);
            assertEquals(0.0, result.getY(), DELTA);
            assertEquals(0.0, result.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should throw exception when subtracting null point")
        void testSubtractNull() {
            // Arrange
            Point3D point = new Point3D(1.0, 2.0, 3.0);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                point.subtract(null);
            });
        }

        @Test
        @DisplayName("Should scale point by positive scalar")
        void testScalePositive() {
            // Arrange
            Point3D point = new Point3D(2.0, 3.0, 4.0);

            // Act
            Point3D result = point.scale(2.5);

            // Assert
            assertEquals(5.0, result.getX(), DELTA);
            assertEquals(7.5, result.getY(), DELTA);
            assertEquals(10.0, result.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should scale point by negative scalar")
        void testScaleNegative() {
            // Arrange
            Point3D point = new Point3D(2.0, 3.0, 4.0);

            // Act
            Point3D result = point.scale(-2.0);

            // Assert
            assertEquals(-4.0, result.getX(), DELTA);
            assertEquals(-6.0, result.getY(), DELTA);
            assertEquals(-8.0, result.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should scale point by zero")
        void testScaleByZero() {
            // Arrange
            Point3D point = new Point3D(5.0, 10.0, 15.0);

            // Act
            Point3D result = point.scale(0.0);

            // Assert
            assertEquals(0.0, result.getX(), DELTA);
            assertEquals(0.0, result.getY(), DELTA);
            assertEquals(0.0, result.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should scale point by one (identity)")
        void testScaleByOne() {
            // Arrange
            Point3D point = new Point3D(3.0, 4.0, 5.0);

            // Act
            Point3D result = point.scale(1.0);

            // Assert
            assertEquals(3.0, result.getX(), DELTA);
            assertEquals(4.0, result.getY(), DELTA);
            assertEquals(5.0, result.getZ(), DELTA);
        }

        @ParameterizedTest
        @ValueSource(doubles = {0.5, 2.0, -1.0, 10.0, 0.1})
        @DisplayName("Should scale correctly with various scalars")
        void testScaleParameterized(double scalar) {
            // Arrange
            Point3D point = new Point3D(2.0, 4.0, 6.0);

            // Act
            Point3D result = point.scale(scalar);

            // Assert
            assertEquals(2.0 * scalar, result.getX(), DELTA);
            assertEquals(4.0 * scalar, result.getY(), DELTA);
            assertEquals(6.0 * scalar, result.getZ(), DELTA);
        }
    }

    @Nested
    @DisplayName("Rotation Transformations")
    class RotationTests {

        @Test
        @DisplayName("Should rotate point around X-axis by 90 degrees")
        void testRotateX90Degrees() {
            // Arrange
            Point3D point = new Point3D(0.0, 1.0, 0.0);
            double angle = Math.PI / 2;  // 90 degrees

            // Act
            Point3D rotated = point.rotateX(angle);

            // Assert
            assertEquals(0.0, rotated.getX(), DELTA);
            assertEquals(0.0, rotated.getY(), DELTA);
            assertEquals(1.0, rotated.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should rotate point around X-axis by 180 degrees")
        void testRotateX180Degrees() {
            // Arrange
            Point3D point = new Point3D(0.0, 1.0, 1.0);
            double angle = Math.PI;  // 180 degrees

            // Act
            Point3D rotated = point.rotateX(angle);

            // Assert
            assertEquals(0.0, rotated.getX(), DELTA);
            assertEquals(-1.0, rotated.getY(), DELTA);
            assertEquals(-1.0, rotated.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should not change X coordinate when rotating around X-axis")
        void testRotateXPreservesXCoordinate() {
            // Arrange
            Point3D point = new Point3D(5.0, 3.0, 4.0);
            double angle = Math.PI / 4;

            // Act
            Point3D rotated = point.rotateX(angle);

            // Assert
            assertEquals(5.0, rotated.getX(), DELTA);
        }

        @Test
        @DisplayName("Should rotate point around Y-axis by 90 degrees")
        void testRotateY90Degrees() {
            // Arrange
            Point3D point = new Point3D(1.0, 0.0, 0.0);
            double angle = Math.PI / 2;  // 90 degrees

            // Act
            Point3D rotated = point.rotateY(angle);

            // Assert
            assertEquals(0.0, rotated.getX(), DELTA);
            assertEquals(0.0, rotated.getY(), DELTA);
            assertEquals(-1.0, rotated.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should not change Y coordinate when rotating around Y-axis")
        void testRotateYPreservesYCoordinate() {
            // Arrange
            Point3D point = new Point3D(3.0, 5.0, 4.0);
            double angle = Math.PI / 3;

            // Act
            Point3D rotated = point.rotateY(angle);

            // Assert
            assertEquals(5.0, rotated.getY(), DELTA);
        }

        @Test
        @DisplayName("Should rotate point around Z-axis by 90 degrees")
        void testRotateZ90Degrees() {
            // Arrange
            Point3D point = new Point3D(1.0, 0.0, 0.0);
            double angle = Math.PI / 2;  // 90 degrees

            // Act
            Point3D rotated = point.rotateZ(angle);

            // Assert
            assertEquals(0.0, rotated.getX(), DELTA);
            assertEquals(1.0, rotated.getY(), DELTA);
            assertEquals(0.0, rotated.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should not change Z coordinate when rotating around Z-axis")
        void testRotateZPreservesZCoordinate() {
            // Arrange
            Point3D point = new Point3D(3.0, 4.0, 5.0);
            double angle = Math.PI / 6;

            // Act
            Point3D rotated = point.rotateZ(angle);

            // Assert
            assertEquals(5.0, rotated.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should preserve magnitude after rotation")
        void testRotationPreservesMagnitude() {
            // Arrange
            Point3D point = new Point3D(3.0, 4.0, 5.0);
            double originalMagnitude = point.magnitude();

            // Act
            Point3D rotatedX = point.rotateX(Math.PI / 4);
            Point3D rotatedY = point.rotateY(Math.PI / 4);
            Point3D rotatedZ = point.rotateZ(Math.PI / 4);

            // Assert
            assertEquals(originalMagnitude, rotatedX.magnitude(), DELTA);
            assertEquals(originalMagnitude, rotatedY.magnitude(), DELTA);
            assertEquals(originalMagnitude, rotatedZ.magnitude(), DELTA);
        }

        @Test
        @DisplayName("Should return to original position after 360 degree rotation")
        void testRotation360Degrees() {
            // Arrange
            Point3D point = new Point3D(2.0, 3.0, 4.0);
            double fullRotation = 2 * Math.PI;

            // Act
            Point3D rotatedX = point.rotateX(fullRotation);
            Point3D rotatedY = point.rotateY(fullRotation);
            Point3D rotatedZ = point.rotateZ(fullRotation);

            // Assert
            assertEquals(point.getX(), rotatedX.getX(), DELTA);
            assertEquals(point.getY(), rotatedX.getY(), DELTA);
            assertEquals(point.getZ(), rotatedX.getZ(), DELTA);

            assertEquals(point.getX(), rotatedY.getX(), DELTA);
            assertEquals(point.getY(), rotatedY.getY(), DELTA);
            assertEquals(point.getZ(), rotatedY.getZ(), DELTA);

            assertEquals(point.getX(), rotatedZ.getX(), DELTA);
            assertEquals(point.getY(), rotatedZ.getY(), DELTA);
            assertEquals(point.getZ(), rotatedZ.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should not change origin when rotating")
        void testRotateOrigin() {
            // Arrange
            Point3D origin = Point3D.origin();

            // Act
            Point3D rotatedX = origin.rotateX(Math.PI / 2);
            Point3D rotatedY = origin.rotateY(Math.PI / 2);
            Point3D rotatedZ = origin.rotateZ(Math.PI / 2);

            // Assert
            assertTrue(rotatedX.equals(origin));
            assertTrue(rotatedY.equals(origin));
            assertTrue(rotatedZ.equals(origin));
        }
    }

    @Nested
    @DisplayName("Dot and Cross Product Operations")
    class ProductTests {

        @Test
        @DisplayName("Should calculate dot product correctly")
        void testDotProduct() {
            // Arrange
            Point3D p1 = new Point3D(1.0, 2.0, 3.0);
            Point3D p2 = new Point3D(4.0, 5.0, 6.0);

            // Act
            double dotProduct = p1.dot(p2);

            // Assert
            // 1*4 + 2*5 + 3*6 = 4 + 10 + 18 = 32
            assertEquals(32.0, dotProduct, DELTA);
        }

        @Test
        @DisplayName("Should return zero for perpendicular vectors")
        void testDotProductPerpendicular() {
            // Arrange
            Point3D p1 = new Point3D(1.0, 0.0, 0.0);
            Point3D p2 = new Point3D(0.0, 1.0, 0.0);

            // Act
            double dotProduct = p1.dot(p2);

            // Assert
            assertEquals(0.0, dotProduct, DELTA);
        }

        @Test
        @DisplayName("Should be commutative for dot product")
        void testDotProductCommutative() {
            // Arrange
            Point3D p1 = new Point3D(2.0, 3.0, 4.0);
            Point3D p2 = new Point3D(5.0, 6.0, 7.0);

            // Act
            double dot1 = p1.dot(p2);
            double dot2 = p2.dot(p1);

            // Assert
            assertEquals(dot1, dot2, DELTA);
        }

        @Test
        @DisplayName("Should calculate dot product with itself as magnitude squared")
        void testDotProductSelf() {
            // Arrange
            Point3D point = new Point3D(3.0, 4.0, 5.0);

            // Act
            double dotProduct = point.dot(point);
            double magnitudeSquared = point.magnitude() * point.magnitude();

            // Assert
            assertEquals(magnitudeSquared, dotProduct, DELTA);
        }

        @Test
        @DisplayName("Should throw exception for dot product with null")
        void testDotProductNull() {
            // Arrange
            Point3D point = new Point3D(1.0, 2.0, 3.0);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                point.dot(null);
            });
        }

        @Test
        @DisplayName("Should calculate cross product correctly")
        void testCrossProduct() {
            // Arrange
            Point3D p1 = new Point3D(1.0, 0.0, 0.0);
            Point3D p2 = new Point3D(0.0, 1.0, 0.0);

            // Act
            Point3D cross = p1.cross(p2);

            // Assert
            // i × j = k
            assertEquals(0.0, cross.getX(), DELTA);
            assertEquals(0.0, cross.getY(), DELTA);
            assertEquals(1.0, cross.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should produce zero vector for parallel vectors cross product")
        void testCrossProductParallel() {
            // Arrange
            Point3D p1 = new Point3D(2.0, 4.0, 6.0);
            Point3D p2 = new Point3D(1.0, 2.0, 3.0);

            // Act
            Point3D cross = p1.cross(p2);

            // Assert
            assertEquals(0.0, cross.getX(), DELTA);
            assertEquals(0.0, cross.getY(), DELTA);
            assertEquals(0.0, cross.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should be anti-commutative for cross product")
        void testCrossProductAntiCommutative() {
            // Arrange
            Point3D p1 = new Point3D(1.0, 2.0, 3.0);
            Point3D p2 = new Point3D(4.0, 5.0, 6.0);

            // Act
            Point3D cross1 = p1.cross(p2);
            Point3D cross2 = p2.cross(p1);

            // Assert
            assertEquals(cross1.getX(), -cross2.getX(), DELTA);
            assertEquals(cross1.getY(), -cross2.getY(), DELTA);
            assertEquals(cross1.getZ(), -cross2.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should produce perpendicular vector in cross product")
        void testCrossProductPerpendicularity() {
            // Arrange
            Point3D p1 = new Point3D(1.0, 2.0, 3.0);
            Point3D p2 = new Point3D(4.0, 5.0, 6.0);

            // Act
            Point3D cross = p1.cross(p2);

            // Assert - cross product should be perpendicular to both inputs
            assertEquals(0.0, cross.dot(p1), DELTA);
            assertEquals(0.0, cross.dot(p2), DELTA);
        }

        @Test
        @DisplayName("Should return zero for cross product with self")
        void testCrossProductSelf() {
            // Arrange
            Point3D point = new Point3D(3.0, 4.0, 5.0);

            // Act
            Point3D cross = point.cross(point);

            // Assert
            assertEquals(0.0, cross.getX(), DELTA);
            assertEquals(0.0, cross.getY(), DELTA);
            assertEquals(0.0, cross.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should throw exception for cross product with null")
        void testCrossProductNull() {
            // Arrange
            Point3D point = new Point3D(1.0, 2.0, 3.0);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                point.cross(null);
            });
        }
    }

    @Nested
    @DisplayName("Normalization Tests")
    class NormalizationTests {

        @Test
        @DisplayName("Should normalize vector to unit length")
        void testNormalize() {
            // Arrange
            Point3D point = new Point3D(3.0, 4.0, 0.0);

            // Act
            Point3D normalized = point.normalize();

            // Assert
            assertEquals(1.0, normalized.magnitude(), DELTA);
            assertEquals(0.6, normalized.getX(), DELTA);  // 3/5
            assertEquals(0.8, normalized.getY(), DELTA);  // 4/5
            assertEquals(0.0, normalized.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should preserve direction when normalizing")
        void testNormalizePreservesDirection() {
            // Arrange
            Point3D point = new Point3D(5.0, 10.0, 15.0);

            // Act
            Point3D normalized = point.normalize();

            // Assert
            // Normalized should be parallel to original (dot product = magnitude)
            double dotProduct = point.dot(normalized);
            double expectedDot = point.magnitude();
            assertEquals(expectedDot, dotProduct, DELTA);
        }

        @Test
        @DisplayName("Should handle normalizing already normalized vector")
        void testNormalizeAlreadyNormalized() {
            // Arrange
            Point3D point = new Point3D(1.0, 0.0, 0.0);

            // Act
            Point3D normalized = point.normalize();

            // Assert
            assertEquals(1.0, normalized.magnitude(), DELTA);
            assertEquals(1.0, normalized.getX(), DELTA);
            assertEquals(0.0, normalized.getY(), DELTA);
            assertEquals(0.0, normalized.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should return zero vector when normalizing zero vector")
        void testNormalizeZeroVector() {
            // Arrange
            Point3D zero = Point3D.origin();

            // Act
            Point3D normalized = zero.normalize();

            // Assert
            assertEquals(0.0, normalized.getX(), DELTA);
            assertEquals(0.0, normalized.getY(), DELTA);
            assertEquals(0.0, normalized.getZ(), DELTA);
        }

        @Test
        @DisplayName("Should normalize 3D vector correctly")
        void testNormalize3D() {
            // Arrange
            Point3D point = new Point3D(1.0, 2.0, 2.0);  // magnitude = 3

            // Act
            Point3D normalized = point.normalize();

            // Assert
            assertEquals(1.0, normalized.magnitude(), DELTA);
            assertEquals(1.0/3.0, normalized.getX(), DELTA);
            assertEquals(2.0/3.0, normalized.getY(), DELTA);
            assertEquals(2.0/3.0, normalized.getZ(), DELTA);
        }
    }

    @Nested
    @DisplayName("Equality and Hash Code Tests")
    class EqualityTests {

        @Test
        @DisplayName("Should be equal to itself")
        void testEqualsSelf() {
            // Arrange
            Point3D point = new Point3D(1.0, 2.0, 3.0);

            // Act & Assert
            assertTrue(point.equals(point));
        }

        @Test
        @DisplayName("Should be equal to point with same coordinates")
        void testEqualsSameCoordinates() {
            // Arrange
            Point3D p1 = new Point3D(1.0, 2.0, 3.0);
            Point3D p2 = new Point3D(1.0, 2.0, 3.0);

            // Act & Assert
            assertTrue(p1.equals(p2));
            assertTrue(p2.equals(p1));
        }

        @Test
        @DisplayName("Should not be equal to point with different coordinates")
        void testNotEqualsDifferentCoordinates() {
            // Arrange
            Point3D p1 = new Point3D(1.0, 2.0, 3.0);
            Point3D p2 = new Point3D(1.0, 2.0, 4.0);

            // Act & Assert
            assertFalse(p1.equals(p2));
        }

        @Test
        @DisplayName("Should not be equal to null")
        void testNotEqualsNull() {
            // Arrange
            Point3D point = new Point3D(1.0, 2.0, 3.0);

            // Act & Assert
            assertFalse(point.equals(null));
        }

        @Test
        @DisplayName("Should not be equal to different class")
        void testNotEqualsDifferentClass() {
            // Arrange
            Point3D point = new Point3D(1.0, 2.0, 3.0);
            String notAPoint = "Not a point";

            // Act & Assert
            assertFalse(point.equals(notAPoint));
        }

        @Test
        @DisplayName("Should have same hash code for equal points")
        void testHashCodeConsistency() {
            // Arrange
            Point3D p1 = new Point3D(1.0, 2.0, 3.0);
            Point3D p2 = new Point3D(1.0, 2.0, 3.0);

            // Act & Assert
            assertEquals(p1.hashCode(), p2.hashCode());
        }

        @Test
        @DisplayName("Should have same hash code when called multiple times")
        void testHashCodeStability() {
            // Arrange
            Point3D point = new Point3D(1.0, 2.0, 3.0);

            // Act
            int hash1 = point.hashCode();
            int hash2 = point.hashCode();

            // Assert
            assertEquals(hash1, hash2);
        }

        @Test
        @DisplayName("Should handle equality with epsilon tolerance")
        void testEqualsWithEpsilon() {
            // Arrange
            Point3D p1 = new Point3D(1.0, 2.0, 3.0);
            Point3D p2 = new Point3D(1.0 + 1e-11, 2.0, 3.0);

            // Act & Assert
            assertTrue(p1.equals(p2));  // Within epsilon tolerance
        }
    }

    @Nested
    @DisplayName("String Representation Tests")
    class StringTests {

        @Test
        @DisplayName("Should produce proper string representation")
        void testToString() {
            // Arrange
            Point3D point = new Point3D(1.5, 2.5, 3.5);

            // Act
            String str = point.toString();

            // Assert
            assertNotNull(str);
            assertTrue(str.contains("Point3D"));
            assertTrue(str.contains("1.50"));
            assertTrue(str.contains("2.50"));
            assertTrue(str.contains("3.50"));
        }

        @Test
        @DisplayName("Should handle zero coordinates in string")
        void testToStringZero() {
            // Arrange
            Point3D origin = Point3D.origin();

            // Act
            String str = origin.toString();

            // Assert
            assertNotNull(str);
            assertTrue(str.contains("0.00"));
        }

        @Test
        @DisplayName("Should handle negative coordinates in string")
        void testToStringNegative() {
            // Arrange
            Point3D point = new Point3D(-1.0, -2.0, -3.0);

            // Act
            String str = point.toString();

            // Assert
            assertNotNull(str);
            assertTrue(str.contains("-"));
        }
    }

    @Nested
    @DisplayName("Edge Cases and Special Values")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle very small differences in coordinates")
        void testVerySmallDifferences() {
            // Arrange
            Point3D p1 = new Point3D(1.0, 2.0, 3.0);
            Point3D p2 = new Point3D(1.0 + 1e-15, 2.0, 3.0);

            // Act
            double distance = p1.distanceTo(p2);

            // Assert
            assertTrue(distance < EPSILON);
        }

        @Test
        @DisplayName("Should handle maximum double values")
        void testMaxDoubleValues() {
            // Arrange
            double maxVal = Double.MAX_VALUE / 10;  // Avoid overflow
            Point3D point = new Point3D(maxVal, maxVal, maxVal);

            // Act & Assert
            assertNotNull(point);
            assertEquals(maxVal, point.getX(), DELTA);
        }

        @Test
        @DisplayName("Should handle minimum positive double values")
        void testMinPositiveDoubleValues() {
            // Arrange
            double minVal = Double.MIN_VALUE;
            Point3D point = new Point3D(minVal, minVal, minVal);

            // Act & Assert
            assertNotNull(point);
            assertEquals(minVal, point.getX());
        }

        @Test
        @DisplayName("Should handle operations resulting in very small values")
        void testOperationsProducingSmallValues() {
            // Arrange
            Point3D p1 = new Point3D(1.0, 2.0, 3.0);
            Point3D p2 = new Point3D(1.0 + 1e-14, 2.0, 3.0);

            // Act
            Point3D diff = p1.subtract(p2);

            // Assert
            assertTrue(Math.abs(diff.getX()) < 1e-13);
        }

        @Test
        @DisplayName("Should maintain precision through multiple operations")
        void testPrecisionThroughOperations() {
            // Arrange
            Point3D original = new Point3D(1.0, 2.0, 3.0);

            // Act - perform operations that should return to original
            Point3D result = original
                    .scale(2.0)
                    .scale(0.5)
                    .add(Point3D.of(1.0, 1.0, 1.0))
                    .subtract(Point3D.of(1.0, 1.0, 1.0));

            // Assert
            assertEquals(original.getX(), result.getX(), DELTA);
            assertEquals(original.getY(), result.getY(), DELTA);
            assertEquals(original.getZ(), result.getZ(), DELTA);
        }
    }
}
