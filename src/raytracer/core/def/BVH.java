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

    /*
     * Missing/Incomplete function implementations
     * 
     * BuildBVH needs to recursively restructure BVHs ect
     * 
     * hit missing
     */

    public BVH() {
        // create empty lists for child objects and child BVHs
        childObjects = new ArrayList<Obj>();
        childBVHs = new ArrayList<BVH>();

        // create emtpy bounding box, since there is nothing in it yet
        boundingBox = BBox.EMPTY;
    }

    @Override
    public BBox bbox() {

        Point min;
        Point max;

        // check if there are any childObjects
        if (childObjects.size() > 0) {
            min = childObjects.get(0).bbox().getMin();
        } else {
            min = BBox.EMPTY.getMin();
        }

        // set max to min, since tere is a larger point than min
        max = min;

        for (Obj obj : childObjects) {
            // calculate min point from all childObjects
            min = min.min(obj.bbox().getMin());
        }

        for (Obj obj : childObjects) {
            // calculate max point from all childObjects
            max = max.max(obj.bbox().getMax());
        }

        // create new Box with points min and max
        return BBox.create(min, max);
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

            // add it to the elments of the current bounding box
            childObjects.add(prim);

        } else if (prim instanceof BVH) {
            // add boundingbox to children bounding boxes
            childBVHs.add((BVH) prim);
        }

        // recalculate this bounding box
        this.boundingBox = bbox();
    }

    /**
     * Builds the actual bounding volume hierarchy
     */
    @Override
    public void buildBVH() {

        // In the method buildBVH() the sub-boxes are built recursively.

        // distribute Objects in a only if there are more than 4 objects in it

        /*
         * if (a.getObjects().size() > THRESHOLD) {
         * 
         * // create two BVHs for distribution
         * BVH bvh1 = new BVH();
         * BVH bvh2 = new BVH();
         * 
         * Point minPoint = boundingBox.getMin();
         * 
         * // get maxMinPoint to extract splitDimension
         * Point maxMinPoint = calculateMaxOfMinPoints();
         * 
         * // convert maxMinPoint to a vector
         * Vec3 v = maxMinPoint.sub(Point.ORIGIN);
         * 
         * // calculate splitDimension int
         * int splitDimension = calculateSplitDimension(v);
         * 
         * // calculate splitPosition float
         * 
         * // TODO:
         * // restructure everything
         * 
         * }
         */

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
        for (int i = 0; i < childObjects.size(); i++) {
            maxPoint = childObjects.get(i).bbox().getMin().max(maxPoint);
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

        // get min point of current bvh
        Point minBvh = bbox().getMin();

        // create vector3 with splitpos at splitDim and everything else 0,
        Vec3 splitPoint;
        switch (splitDim) {
            case 0:
                splitPoint = new Vec3(splitPos, 0, 0);
                break;

            case 1:
                splitPoint = new Vec3(0, splitPos, 0);
                break;

            case 2:
                splitPoint = new Vec3(0, 0, splitPos);
                break;
            default:
                splitPoint = new Vec3(0, 0, 0);
        }

        // add vector to min point
        Point splitDimensionPoint = minBvh.add(splitPoint);

        // iterate over all objects in current BVH and check if objects min point is in
        // the BVHBase a. If so, add it to a else to b
        for (Obj obj : childObjects) {

            // compare minPoint of all objets at coordinate splitDim with
            // splitDimensionPoint

            switch (splitDim) {
                case 0:
                    if (obj.bbox().getMin().x() <= splitDimensionPoint.x()) {
                        a.add(obj);
                    } else {
                        b.add(obj);
                    }
                    break;

                case 1:
                    if (obj.bbox().getMin().y() <= splitDimensionPoint.y()) {
                        a.add(obj);
                    } else {
                        b.add(obj);
                    }
                    break;

                case 2:
                    if (obj.bbox().getMin().z() <= splitDimensionPoint.z()) {
                        a.add(obj);
                    } else {
                        b.add(obj);
                    }
                    break;

                default:
                    break;
            }
        }

        childBVHs.add((BVH) a);
        childBVHs.add((BVH) b);

        // remove objects from childObject list which where added to sub BVHs
        childObjects.removeAll(a.getObjects());
        childObjects.removeAll(b.getObjects());
    }

    @Override
    public Hit hit(final Ray ray, final Obj obj, final float tMin, final float tMax) {

        // get hit of ray

        // check if ray hit the current bbox

        // check if the ray also hit a sub box

        // iteratively check if ray hittet bounding box of child object of that box

        // TODO Implement this method
        throw new UnsupportedOperationException("This method has not yet been implemented.");
    }

    @Override
    public List<Obj> getObjects() {
        // return only child objects of current box
        return childObjects;
    }
}
