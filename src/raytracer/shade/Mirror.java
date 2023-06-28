package raytracer.shade;

import raytracer.core.Hit;
import raytracer.core.LightSource;
import raytracer.core.Shader;
import raytracer.core.Trace;
import raytracer.math.Color;
import raytracer.math.Vec3;

public class Mirror implements Shader {
    private float reflectivity;

    public Mirror(float reflectivity) {
        this.reflectivity = reflectivity;
    }

    @Override
    public Color shade(Hit hit, Trace trace) {
        Color finalColor = Color.BLACK;

        for (LightSource light : trace.getScene().getLightSources()) {

            Vec3 v = light.getLocation().sub(hit.getPoint()).normalized();

            // reflect
            v.reflect(hit.getNormal());

            // create reflected Trace
            Trace reflectedTrace = trace.spawn(hit.getPoint(), v);

            Color recursiveColor = reflectedTrace.getHit().get().shade(hit, trace);

            Color objColor = trace.shade();

            finalColor = finalColor.add(objColor.scale(1 - reflectivity).add(recursiveColor.scale(reflectivity)));
        }

        return finalColor;
    }

    public float getReflectionCoefficient() {
        return reflectivity;
    }
}
