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

    // current bounding box
    private BBox boundingBox;

    public BVH() {
        // create empty lists for child objects and child BVHs
        childObjects = new ArrayList<Obj>();
        childBVHs = new ArrayList<BVH>();

        // create emtpy bounding box, since there is nothing in it yet
        boundingBox = BBox.EMPTY;
    }

    @Override
    public BBox bbox() {

        BBox box = BBox.EMPTY;

        // surround current box and object
        for (Obj obj : childObjects)
            box = BBox.surround(box, obj.bbox());

        // surround current box and child box
        for (BVH bvh : childBVHs)
            box = BBox.surround(box, bvh.boundingBox);

        return box;
    }

    @Override
    public void add(final Obj prim) {

        // check if the oject we want to add is a standard object
        if (prim instanceof BVH)
            // add boundingbox to children bounding boxes
            childBVHs.add((BVH) prim);
        else if (prim instanceof StandardObj)
            // add it to the elments of the current bounding box
            childObjects.add(prim);

        // recalculate this bounding box
        this.boundingBox = bbox();
    }

    @Override
    public void buildBVH() {

        BVH a;
        BVH b;

        if (childBVHs.size() > 0 && childObjects.size() > 0) {
            // grab both existing BVHs
            a = childBVHs.get(0);
            b = childBVHs.get(1);
        } else if (childBVHs.size() == 0 && childObjects.size() > 0) {
            // there are no childBVHs -> check if there are more than THRESHOLD objects
            if (childObjects.size() > THRESHOLD) {
                // create new BVHs for distribution since there are none
                a = new BVH();
                b = new BVH();
            } else
                return;

        } else if (childBVHs.size() > 0 && childObjects.size() == 0) {

            // there are no free objects to distribute into child BVHs, thus distribute
            // recursively on sub boxes
            for (int i = 0; i < childBVHs.size(); i++) {
                childBVHs.get(i).buildBVH();
            }
            return;
        } else
            return;

        // grab smallest point (which is the minPoint of the BBox)
        Point minPoint = bbox().getMin();

        // get maxMinPoint to extract splitDimension
        Point maxMinPoint = calculateMaxOfMinPoints();

        // convert maxMinPoint to a vector
        Vec3 v = maxMinPoint.sub(minPoint);

        // calculate splitDimension int
        int splitDimension = calculateSplitDimension(v);

        // create Zero vector where only splitDimension is changed
        Vec3 splitVec = Vec3.INF;

        if (splitDimension == 0)
            splitVec = new Vec3(v.x(), 0, 0);
        if (splitDimension == 1)
            splitVec = new Vec3(0, v.y(), 0);
        if (splitDimension == 2)
            splitVec = new Vec3(0, 0, v.z());

        // scale splitVec by 0.5f to get half of the vector
        splitVec = splitVec.scale(0.5f);

        // calculate splitPosition float by adding the vector to the minPoint
        float splitPosition = minPoint.add(splitVec).get(splitDimension);

        // distribute objects into a and b
        distributeObjects(a, b, splitDimension, splitPosition);

        // build newly distributed bvhs
        a.buildBVH();
        b.buildBVH();
    }

    @Override
    public Point calculateMaxOfMinPoints() {

        // return min point of bounding box since nothing in it
        if (childBVHs.size() == 0 && childObjects.size() == 0)
            return boundingBox.getMin();

        // initialize with minimum point of first object
        Point maxPoint = childObjects.get(0).bbox().getMin();

        // iterate over all objects and get maximum of current maxPoint and getMin()
        for (int i = 0; i < childObjects.size(); i++)
            maxPoint = childObjects.get(i).bbox().getMin().max(maxPoint);

        return maxPoint;
    }

    @Override
    public int calculateSplitDimension(final Vec3 extent) {

        // extract max value from extent vector
        float maxCoord = Math.max(Math.abs(extent.x()), Math.max(Math.abs(extent.y()), Math.abs(extent.z())));

        // grab index of largest coordinate
        for (int i = 0; i < 3; i++) {
            if (maxCoord == extent.get(i))
                return i;
        }
        return -1;
    }

    @Override
    public void distributeObjects(final BVHBase a, final BVHBase b,
            final int splitDim, final float splitPos) {

        // iterate over all objects in current BVH and check if objects min point is in
        // the BVHBase a. If so, add it to a else to b
        for (Obj obj : childObjects) {

            // compare minPoint of curr objets at coordinate splitDim with splitpos
            BVHBase bvh = obj.bbox().getMin().get(splitDim) < splitPos ? a : b;

            // add obj to the specific box
            bvh.add(obj);
        }

        // add BVHs to childBVHs
        childBVHs.add((BVH) a);
        childBVHs.add((BVH) b);

        // remove objects from childObject list which where added to sub BVHs
        childObjects.clear();
    }

    @Override
    public Hit hit(final Ray ray, final Obj obj, final float tMin, final float tMax) {

        // get hit of ray with main BVH
        Hit parentHit = boundingBox.hit(ray, tMin, tMax);

        // check if ray hit the current bbox
        if (parentHit.hits()) {

            // there are up to 4 childObjects
            if (childObjects.size() != 0) {

                float hD = Float.POSITIVE_INFINITY;
                Hit closestHit = Hit.No.get();

                // iterate over all child objects
                for (Obj childObj : childObjects) {

                    // check if child object is hit and hit is valid and there is no closer object
                    Hit childObj_Hit = childObj.hit(ray, obj, tMin, tMax);
                    if (childObj_Hit.hits())
                        if (childObj_Hit.getParameter() >= tMin && childObj_Hit.getParameter() <= tMax)
                            if (hD == Float.POSITIVE_INFINITY || hD > childObj_Hit.getParameter()) {
                                hD = childObj_Hit.getParameter();
                                closestHit = childObj_Hit;
                            }
                }

                return closestHit;
            } else {
                // grab both BVHs
                BVH bvh1 = childBVHs.get(0);
                BVH bvh2 = childBVHs.get(1);

                // test for hit on first BVH
                Hit bvh1Hit = bvh1.hit(ray, obj, tMin, tMax);

                // set hit distance to Infinity
                float h1D = Float.POSITIVE_INFINITY;
                if (bvh1Hit.hits())
                    if (bvh1Hit.getParameter() >= tMin && bvh1Hit.getParameter() <= tMax)
                        h1D = bvh1Hit.getParameter();

                // test for hit on second BVH
                Hit bvh2Hit = bvh2.hit(ray, obj, tMin, tMax);

                // set hit distance to Infinity
                float h2D = Float.POSITIVE_INFINITY;
                if (bvh2Hit.hits())
                    if (bvh2Hit.getParameter() >= tMin && bvh2Hit.getParameter() <= tMax)
                        h2D = bvh2Hit.getParameter();

                // check if only first bvh was hit
                if (h1D != Float.POSITIVE_INFINITY && h2D == Float.POSITIVE_INFINITY)
                    return bvh1Hit;

                // check if only second bvh was hit
                if (h1D == Float.POSITIVE_INFINITY && h2D != Float.POSITIVE_INFINITY)
                    return bvh2Hit;

                // both BVHs were hit -> check for closer one (first hit) as stated here
                // https://forum.prog2.sic.saarland/t/rabbit-has-black-areas/1559/3
                return h1D < h2D ? bvh1Hit : bvh2Hit;
            }
        }
        // return default Hit No since nothing was hit
        return Hit.No.get();
    }

    @Override
    public List<Obj> getObjects() {
        return childObjects;
    }
}
