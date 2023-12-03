package net.celsiuss.crystalhollowsmap;

import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.renderer.render.Renderer2d;
import me.x150.renderer.util.AlphaOverride;
import me.x150.renderer.util.BufferUtils;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.awt.*;

import static me.x150.renderer.util.RendererUtils.endRender;
import static me.x150.renderer.util.RendererUtils.setupRender;

public class CustomRenderer2d extends Renderer2d {
    public static void renderPolygon(MatrixStack matrices, Color color, Vector2f[] points) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float[] colorFloat = getColor(color);
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        buffer.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        for (Vector2f point : points) {
            buffer.vertex(matrix, point.x, point.y, 0f)
                    .color(colorFloat[0], colorFloat[1], colorFloat[2], colorFloat[3])
                    .next();
        }
        setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferUtils.draw(buffer);
        endRender();
    }

    static float[] getColor(Color c) {
        return new float[]{c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, transformColor(
                c.getAlpha() / 255f)};
    }
    static float transformColor(float f) {
        return AlphaOverride.compute(f);
    }


}
