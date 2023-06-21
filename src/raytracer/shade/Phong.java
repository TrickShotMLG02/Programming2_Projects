package raytracer.shade;

import java.util.List;

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
        List<LightSource> lights = trace.getScene().getLightSources().stream().toList();

        // set base color as black
        Color diffusion = Color.BLACK;

        // iterate over all light sources
        for (int i = 0; i < lights.size(); i++) {
            LightSource light = lights.get(i);

            // check if shadow ray hits something on way from hit point to light source
            // send new ray back
            Trace shadowTrace = trace.spawn(hit.getPoint(), trace.getRay().invDir().normalized());
            if (shadowTrace.getHit().hits()) {

                // add diffusion for current light to diffusion color
                diffusion = diffusion.add(light.getColor().mul(inner.shade(hit, trace)).scale(diffuse)
                        .scale(Math.max(0, hit.getNormal().dot(trace.getRay().dir().inv()))));

            }
        }

        // add diffusion to phong value
        phong = phong.add(diffusion);

        // set base color as black
        Color specularity = Color.BLACK;

        // iterate over all light sources
        for (int i = 0; i < lights.size(); i++) {
            LightSource light = lights.get(i);

            // add specularity for current light to diffusion color
            specularity = specularity.add(light.getColor().scale(specular)
                    .scale((float) Math.pow(Math.max(0,
                            trace.getRay().reflect(hit.getPoint(), hit.getNormal()).dir()
                                    .dot(trace.getRay().dir().inv())),
                            smoothness)));
        }

        // add specularity to phong value
        phong = phong.add(specularity);

        // return phong color
        return phong;
    }

}
