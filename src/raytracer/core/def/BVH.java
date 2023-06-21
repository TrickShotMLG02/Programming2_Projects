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
        // TODO Implement this method

        Point maxPoint = calculateMaxOfMinPoints();

        Point minPoint;

        if (childBVHs.size() == 0 && childObjects.size() == 0) {
            return 0;
        } else if (childBVHs.size() == 0) {
            // there are only child objects but no child boxes
            minPoint = childObjects.get(0).bbox().getMin();
        } else {
            minPoint = childBVHs.get(0).boundingBox.getMin();
            for (int i = 0; i < childBVHs.size(); i++) {
                minPoint = childBVHs.get(i).boundingBox.getMin().max(minPoint);
            }
        }

        // calculate minPoint from extent and maxPoint
        minPoint = maxPoint.sub(extent);

        // minPoint now contains minimum point of all objects

        // get largest distance between min and max

        // return largest distance
        return (int) maxPoint.sub(minPoint).norm();
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
