/*
 * Copyright (C) 2017 LEIDOS.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package gov.dot.fhwa.saxton.carma.geometry.cartesian;

import java.util.List;

/**
 * An object in n-dimensional cartesian space defined by a point cloud.
 * The bounds of the object are calculated on construction and can be used for intersection checking
 */
public class CartesianObject implements CartesianElement {

  protected final int MIN_BOUND_IDX = 0;
  protected final int MAX_BOUND_IDX = 1;
  protected double[][] bounds; // 2 cols as every dim has a min and max value
  protected Point centroidOfBounds;
  protected Point centroidOfCloud;
  protected int numDimensions;
  protected List<? extends Point> pointCloud;

  /**
   * Constructor which defines a cartesian object by the provided point cloud
   * All provided points must be of the same dimension
   * @param pointCloud The point cloud which defines this object
   * @throws IllegalArgumentException Thrown if the provided point cloud has points of varying dimensions
   */
  public CartesianObject(List<? extends Point> pointCloud) throws IllegalArgumentException {
    this.validateInput(pointCloud);
    this.numDimensions = pointCloud.get(0).getNumDimensions();
    this.calculateBounds(pointCloud);
    this.calculateCentroidOfBounds();
    this.pointCloud = pointCloud;
    this.calculateCentroidOfCloud();
  }

  /**
   * Helper function to validate the input to the constuctor
   * @param points The point cloud to validate
   * @throws IllegalArgumentException Thrown if the provided point cloud has points of varying dimensions
   */
  protected void validateInput(List<? extends Point> points) throws IllegalArgumentException {
    if (points.size() <= 0) {
      throw new IllegalArgumentException("Empty list of points provided to CartesianObject constructor");
    }
    int expectedDimensions = 0;
    boolean firstPoint = true;
    for (Point p : points) {
      if (firstPoint) {
        expectedDimensions = p.getNumDimensions();
        firstPoint = false;
      } else if (p.getNumDimensions() != expectedDimensions) {
        throw new IllegalArgumentException("Inconsistent dimensions in list of points provided to CartesianObject constructor");
      }
    }
  }

  /**
   * Calculates the centroid of this object's point cloud
   * Called in constructor
   * Assumes that validateInput() has already been called
   */
  protected void calculateCentroidOfCloud() {
    Vector centroidValues = new Vector(new Point(new double[numDimensions]));
    for (int i = 0; i < pointCloud.size(); i++) {
      for (int j = 0; j < numDimensions; j++) {
        centroidValues.setDim(j, centroidValues.getDim(j) + pointCloud.get(i).getDim(j));
      }
    }
    centroidOfCloud = centroidValues.scalarMultiply(1.0 / pointCloud.size()).toPoint();
  }

  /**
   * Calculates the centroid of this object's bounds
   * Called in constructor
   * Assumes calculateBounds() has already been called
   */
  protected void calculateCentroidOfBounds() {
    double[] centroidValues = new double[numDimensions];
    for (int i = 0; i < numDimensions; i++) {
      centroidValues[i] = (bounds[i][MIN_BOUND_IDX] + bounds[i][MAX_BOUND_IDX]) / 2;
    }
    centroidOfBounds = new Point(centroidValues);
  }

  /**
   * Calculates the bounds of the provided point cloud
   * Called in the constructor
   * Assumes that validateInput() has already been called
   * @param points A list of points assumed to be of the same dimension
   */
  protected void calculateBounds(List<? extends Point> points) {
    int dims = this.getNumDimensions();
    bounds = new double[dims][2];

    boolean firstPoint = true;
    for (Point p : points) {
      for (int i = 0; i < dims; i++) {
        if (firstPoint) {
          bounds[i][MIN_BOUND_IDX] = p.getDim(i);
          bounds[i][MAX_BOUND_IDX] = p.getDim(i);
        } else if (p.getDim(i) < bounds[i][MIN_BOUND_IDX]) {
          bounds[i][MIN_BOUND_IDX] = p.getDim(i);
        } else if (p.getDim(i) > bounds[i][MAX_BOUND_IDX]) {
          bounds[i][MAX_BOUND_IDX] = p.getDim(i);
        }
      }
      firstPoint = false;
    }
  }

  /**
   * Get's the set of points originally used to define this object
   * @return list of points of the same dimension
   */
  public List<? extends Point> getPointCloud() {
    return pointCloud;
  }

  /**
   * Gets the index used for minimum bounds in the 2d bounds array
   * @return index
   */
  public int getMinBoundIndx() {
    return MIN_BOUND_IDX;
  }

  /**
   * Gets the index used for maximum bounds in the 2d bounds array
   * @return index
   */
  public int getMaxBoundIndx() {
    return MAX_BOUND_IDX;
  }

  /**
   * Gets the bounds of this object
   * @return A 2d array where the rows are the dimension and the columns are the min/max values
   */
  public double[][] getBounds() {
    return bounds;
  }

  /**
   * Gets the centroid of this object's bounds
   * @return the bounds centroid
   */
  public Point getCentroidOfBounds() {
    return centroidOfBounds;
  }

  /**
   * Gets the centroid of this object's original point cloud
   * @return the point cloud centroid
   */
  public Point getCentroidOfCloud() {
    return centroidOfCloud;
  }

  @Override public int getNumDimensions() {
    return numDimensions;
  }
}