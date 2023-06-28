package raytracer.Scenes;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import raytracer.core.Camera;
import raytracer.core.LightSource;
import raytracer.core.Obj;
import raytracer.core.PerspectiveCamera;
import raytracer.core.Renderer;
import raytracer.core.Scene;
import raytracer.core.Shader;
import raytracer.core.def.Accelerator;
import raytracer.core.def.BVH;
import raytracer.core.def.PointLightSource;
import raytracer.core.def.SimpleAccelerator;
import raytracer.core.def.StandardObj;
import raytracer.core.def.StandardScene;
import raytracer.geom.GeomFactory;
import raytracer.geom.Primitive;
import raytracer.math.Color;
import raytracer.math.Point;
import raytracer.math.Vec3;
import raytracer.shade.ShaderFactory;
import raytracer.shade.SingleColor;

public class LightSpheres {

    private static class MyPanel extends JPanel {

        private final BufferedImage img;

        public MyPanel(final int w, final int h) {
            img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(img.getWidth(), img.getHeight());
        }

        public void drawPacket(final int x, final int y, final int w, final int h, final int[] data) {
            img.setRGB(x, y, w, h, data, 0, w);
        }

        @Override
        public void paintComponent(final Graphics g) {
            super.paintComponent(g);
            g.drawImage(img, 0, 0, null);
        }
    }

    static Scene getScene(float t) {

        final Color ambient = Color.WHITE.scale(0.05f);

        final Camera cam = new PerspectiveCamera(
                new Point(0, 0, -7),
                new Point(0, 0, 0),
                new Vec3(0, 5, 0),
                5, 10, 10);
        final List<LightSource> lights = new ArrayList<LightSource>();
        final Accelerator accel = new SimpleAccelerator();
        final BVH bvh = new BVH();

        // Distance of the lights to the center
        float lightRadius = 2.3f;
        // how many separators between the lights
        int separatorCount = 5;
        // Size of the spheres
        float size = 1.0f;
        // Distance between the spheres
        float distance = 2.0f;
        // Number of spheres in each direction
        int[] count = new int[] { 6, 6, 2 };

        {
            final Shader color_shader = new SingleColor(Color.WHITE);
            final Shader shader = ShaderFactory.createPhong(color_shader, ambient, 0.8f, 1.0f, 15);
            for (int ix = 0; ix < count[0]; ix++) {
                for (int iy = 0; iy < count[1]; iy++) {
                    for (int iz = 0; iz < count[2]; iz++) {
                        float x = -distance * count[0] / 2 + ix * distance;
                        float y = -distance * count[1] / 2 + iy * distance;
                        float z = -distance * count[2] / 2 + iz * distance;
                        Point p = new Point(x, y, z);
                        if (Math.abs(p.get(0)) + Math.abs(p.get(1)) < 1.5 * lightRadius) {
                            continue;
                        }

                        final Primitive sphere = GeomFactory.createSphere(p, size / 2);
                        final Obj obj = new StandardObj(sphere, shader);
                        bvh.add(obj);
                    }
                }
            }

        }

        {

            Color[] colors = new Color[] { Color.YELLOW, Color.MAGENTA, Color.CYAN };
            // create a circle of lights (in xy-plane)
            // rotate by 10 degrees per second
            float angleOffset = (float) ((10 * t) * Math.PI / 180);
            for (int i = 0; i < colors.length; i++) {
                float angle = (float) (2 * Math.PI * i / colors.length) + angleOffset;
                float x = (float) (lightRadius * Math.cos(angle));
                float y = (float) (lightRadius * Math.sin(angle));
                Point p = new Point(x, y, 0);
                LightSource ls = new PointLightSource(p, colors[i].scale(1.0f));
                lights.add(ls);

                // add sphere behind light for visualization
                Point p2 = new Point(x, y, 10);
                final Shader color_shader = new SingleColor(colors[i]);
                final Shader shader = ShaderFactory.createPhong(color_shader, ambient, 1.0f,
                        0.2f, 5);
                final Primitive sphere = GeomFactory.createSphere(p2, 0.5f);
                final Obj obj = new StandardObj(sphere, shader);
                bvh.add(obj);
            }

            float sphereRadius = lightRadius / separatorCount / 2;
            // sphere columns between them
            for (int i = 0; i < colors.length; i++) {
                float angle = (float) (2 * Math.PI * i / colors.length) + angleOffset
                        + (float) (Math.PI / colors.length);
                final Shader color_shader = new SingleColor(colors[i]);
                final Shader shader = ShaderFactory.createPhong(color_shader, ambient, 1.0f,
                        0.2f, 5);

                for (int j = 0; j < separatorCount; j++) {
                    float d = j * 2 * sphereRadius;
                    float x = (float) (d * Math.cos(angle));
                    float y = (float) (d * Math.sin(angle));
                    Point p = new Point(x, y, 0);
                    final Primitive sphere = GeomFactory.createSphere(p, sphereRadius);
                    final Obj obj = new StandardObj(sphere, shader);
                    bvh.add(obj);
                }
            }
        }

        bvh.buildBVH();
        accel.add(bvh);

        final Scene scene = new StandardScene(cam, lights, accel);
        return scene;
    }

    public static void main(final String[] args) {
        final int xRes = 640, yRes = 480, packet = 16;
        final MyPanel panel = new MyPanel(xRes, yRes);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final JFrame f = new JFrame("Prog2 Raytracer");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.add(panel);
                f.pack();
                f.setVisible(true);
            }
        });

        int frameCount = 0;
        long startTime = System.currentTimeMillis();
        while (true) {
            frameCount++;
            float t = (float) ((System.currentTimeMillis() - startTime) / 1000.0);
            final Scene scene = getScene(t);
            final Renderer r = new Renderer(scene, xRes, yRes, 1);

            final Executor exe = Executors.newFixedThreadPool(12);
            final CompletionService<Renderer.Work> ecs = new ExecutorCompletionService<Renderer.Work>(exe);
            int num = 0;
            for (int x = 0; x < xRes; x += packet) {
                for (int y = 0; y < yRes; y += packet) {
                    ecs.submit(r.render(x, y, packet, packet));
                    num = num + 1;
                }
            }

            for (int i = 0; i < num; i++) {
                try {
                    final Renderer.Work w = ecs.take().get();
                    panel.drawPacket(w.x, w.y, packet, packet, w.pixels);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                } catch (final ExecutionException e) {
                    e.printStackTrace();
                }
            }

            panel.repaint();
            float fps = frameCount / t;
            System.out.println("Finished frame " + frameCount + " (" + String.format("%.2f", fps) + " fps)");
        }
    }

}