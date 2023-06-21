package raytracer.shade;

import java.util.Collection;

import raytracer.core.Hit;
import raytracer.core.LightSource;
import raytracer.core.Shader;
import raytracer.core.Trace;
import raytracer.math.Color;

public class Phong implements Shader {

    final Shader inner;
    final Color ambient;
    final float diffuse;
    final float specular;
    final float smoothness;

    public Phong(final Shader inner, final Color ambient, final float diffuse, final float specular,
            final float smoothness) {

        if (diffuse < 0 || specular < 0 || smoothness < 0) {
            throw new IllegalArgumentException("invalid value");
        }

        if (ambient == null || inner == null) {
            throw new IllegalArgumentException("instance null");
        }

        this.inner = inner;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.smoothness = smoothness;

    }

    @Override
    public Color shade(Hit hit, Trace trace) {

        // set phong color to ambient color
        Color phong = ambient;

        // get light sources
        Collection<LightSource> lights = trace.getScene().getLightSources();

        // set base color as black
        Color diffusion = Color.BLACK;

        // iterate over all light sources
        for (int i = 0; i < lights.size(); i++) {
            LightSource light = (LightSource) lights.toArray()[i];

            // add diffusion for current light to diffusion color
            diffusion = diffusion.add(light.getColor().mul(inner.shade(hit, trace)).scale(diffuse)
                    .scale(Math.max(0, hit.getNormal().dot(trace.getRay().dir()))));
        }

        // add diffusion to phong value
        phong = phong.add(diffusion);

        // set base color as black
        Color specularity = Color.BLACK;

        // iterate over all light sources
        for (int i = 0; i < lights.size(); i++) {
            LightSource light = (LightSource) lights.toArray()[i];

            // add specularity for current light to diffusion color
            specularity = specularity.add(light.getColor().scale(specular)
                    .scale((float) Math.pow(Math.max(0,
                            trace.getRay().reflect(hit.getPoint(), hit.getNormal()).dir()
                                    .dot(trace.getRay().dir())),
                            smoothness)));
        }

        // add specularity to phong value
        phong = phong.add(specularity);

        // return phong color
        return phong;
    }

}
