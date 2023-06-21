package raytracer.core.def;

import java.util.ArrayList;
import java.util.List;

import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.geom.BBox;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec3;

/**
 * Represents a bounding volume hierarchy acceleration structure
 */
public class BVH extends BVHBase {

    /*
     * Done:
     * calculateMaxOfMinPoints -> calculates the maximum of all minimum points
     * (elementwise)
     * 
     * Done:
     * CalculateSplitDimension: get difference as vector between min and max point
     * Afterwards get the largest coordinate from x,y,z and return it
     * 
     * 
     * Get middle of the vector between min and max point's largest value
     * Distribute objects on left side to one box and objects on right side to
     * another box
     * Do that until there are 4 or less objects within each box
     * Distribute by the minimum point. So if the minimum point lies within a box,
     * the object is added to that particular box
     * 
     */

    // list that contains all child objects of the current bounding box
    private List<Obj> childObjects;
    // list of all BVHs that are children of current BVH
    private List<BVH> childBVHs;

    private BBox boundingBox;

    public BVH() {
        // create empty lists for child objects and child BVHs
        childObjects = new ArrayList<Obj>();
        childBVHs = new ArrayList<BVH>();

        // create emtpy bounding box, since there is nothing in it yet
        boundingBox = BBox.EMPTY;

        // throw new UnsupportedOperationException("This method has not yet been
        // implemented.");
    }

    @Override
    public BBox bbox() {
        BBox newBoundingBox = BBox.EMPTY;
        // TODO: compute bounding Box of the current objects

        Point min;
        Point max;

        return newBoundingBox;
    }

    /**
     * Adds an object to the acceleration structure
     *
     * @param prim
     *             The object to add
     */
    @Override
    public void add(final Obj prim) {

        // check if the oject we want to add is a standard object
        if (prim instanceof StandardObj) {
            // check if current bounding box has reached the threshold of objects
            if (childObjects.size() >= THRESHOLD) {

                // TODO:
                // restructure everything

            } else {
                // add it to the elments of the current bounding box
                childObjects.add(prim);

            }
        } else if (prim instanceof BVH) {
            // add boundingbox to children bounding boxes
            childBVHs.add((BVH) prim);

        }

        // recalculate this bounding box
        this.boundingBox = bbox();

        // TODO Implement this method
        throw new UnsupportedOperationException("This method has not yet been implemented.");
    }

    /**
     * Builds the actual bounding volume hierarchy
     */
    @Override
    public void buildBVH() {
        // TODO Implement this method
        // this function is recursive
        throw new UnsupportedOperationException("This method has not yet been implemented.");
    }

    @Override
    public Point calculateMaxOfMinPoints() {

        if (childBVHs.size() == 0 && childObjects.size() == 0) {
            return Point.ORIGIN;
        }

        // initialize with minimum point of first object
        Point maxPoint = childObjects.get(0).bbox().getMin();

        // iterate over all objects and get element wise maximum of current maxPoint and
        // getMin()
        for (int i = 0; i < childBVHs.size(); i++) {
            maxPoint = childBVHs.get(i).boundingBox.getMin().max(maxPoint);
        }

        return maxPoint;
    }

    @Override
    public int calculateSplitDimension(final Vec3 extent) {
        // if x is largest coordinate -> 0
        // if y is largest coordinate -> 1
        // if z is largest coordinate -> 2

        float maxCoord = Math.max(extent.x(), Math.max(extent.y(), extent.z()));

        if (maxCoord == extent.x())
            return 0;
        else if (maxCoord == extent.y())
            return 1;
        else
            return 2;
    }

    @Override
    public void distributeObjects(final BVHBase a, final BVHBase b,
            final int splitDim, final float splitPos) {

        // TODO Implement this method
        throw new UnsupportedOperationException("This method has not yet been implemented.");
    }

    @Override
    public Hit hit(final Ray ray, final Obj obj, final float tMin, final float tMax) {
        // TODO Implement this method
        throw new UnsupportedOperationException("This method has not yet been implemented.");
    }

    @Override
    public List<Obj> getObjects() {
        // create list that contains child objects of current box
        List<Obj> allObjects = childObjects;
        // add all other child objects from child boxes
        for (int i = 0; i < childBVHs.size(); i++) {
            // add all objects from child box at index
            allObjects.addAll(childBVHs.get(i).getObjects());
        }

        // TODO:
        // check if it should return only the objects in the current box or in all sub
        // boxes

        // return all child objects iteratively
        // return allObjects;

        // return only child objects of current box
        return childObjects;
    }
}
