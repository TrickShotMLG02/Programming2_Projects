package raytracer.shade;

import java.util.List;

import raytracer.core.Hit;
import raytracer.core.LightSource;
import raytracer.core.Shader;
import raytracer.core.Trace;
import raytracer.math.Color;
import raytracer.math.Constants;
import raytracer.math.Vec3;

public class Phong implements Shader {

    final Shader inner;
    final Color ambient;
    final float diffuse;
    final float specular;
    final float smoothness;

    public Phong(final Shader inner, final Color ambient, final float diffuse, final float specular,
            final float smoothness) {

        if (diffuse < -Constants.EPS || specular < -Constants.EPS || smoothness < -Constants.EPS)
            throw new IllegalArgumentException("invalid value -> may not be smaller 0");

        if (Float.isInfinite(diffuse) || Float.isInfinite(specular) || Float.isInfinite(smoothness))
            throw new IllegalArgumentException("invalid value -> may not be infinite");

        if (Float.isNaN(diffuse) || Float.isNaN(specular) || Float.isNaN(smoothness))
            throw new IllegalArgumentException("Not a Number (NaN)");

        if (ambient == null || inner == null)
            throw new IllegalArgumentException("instance null");

        this.inner = inner;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.smoothness = smoothness;
    }

    @Override
    public Color shade(Hit hit, Trace trace) {

        // get light sources
        List<LightSource> lights = trace.getScene().getLightSources().stream().toList();

        // initialize colors as Black
        Color diffusion = Color.BLACK;
        Color specularity = Color.BLACK;
        Color phong = Color.BLACK;

        Color baseColor = inner.shade(hit, trace);

        // iterate over all light sources
        for (int i = 0; i < lights.size(); i++) {
            LightSource light = lights.get(i);

            // calculate vector from hitpoint to light
            Vec3 lightVec = light.getLocation().sub(hit.getPoint()).normalized();
            float lightDistance = light.getLocation().sub(hit.getPoint()).norm();

            // calculate reflection vector of light on hitpoint from camera position
            Vec3 reflectionVector = trace.getRay().reflect(hit.getPoint(), hit.getNormal()).dir();

            // create trace which points to light source
            Trace shadowTrace = trace.spawn(hit.getPoint(), lightVec);

            // check that trace is not blocked by any object
            if (!shadowTrace.getHit().hits() || shadowTrace.getHit().getParameter() > lightDistance) {

                // grab color of light source
                Color light_baseColor = light.getColor().mul(baseColor);

                // calculate diffusion coefficient using scalar product n_v
                float diffusionCoefficient = Math.max(0, hit.getNormal().dot(lightVec));

                // add diffusion for current light source to diffusion color
                diffusion = diffusion.add(light_baseColor.scale(diffuse).scale(diffusionCoefficient));

                // calculate angle of reflection vector using scalar product
                float reflectionAngle = reflectionVector.dot(lightVec);

                // calculate specularity coefficient using the reflectionAngle
                float specularityCoefficient = (float) Math.pow(Math.max(0, reflectionAngle), smoothness);

                // calculate specularity for current light source
                specularity = specularity.add(light.getColor().scale(specular).scale(specularityCoefficient));
            }
        }

        // set phong color to ambient color
        phong = phong.add(ambient);

        // add diffusion to phong value
        phong = phong.add(diffusion);

        // add specularity to phong value
        phong = phong.add(specularity);

        // return phong color
        return phong;
    }

}
