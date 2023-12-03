package net.celsiuss.crystalhollowsmap;

import me.x150.renderer.event.RenderEvents;
import me.x150.renderer.render.MSAAFramebuffer;
import me.x150.renderer.render.Renderer2d;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.scoreboard.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class CrystalHollowsMap implements ClientModInitializer {

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("crystalhollowsmap");

	private static final KeyBinding SHOW_MAP = new KeyBinding("key.toggle_cystalhollows_map", GLFW.GLFW_KEY_M, "key.categories.misc");

	private Vector2f playerPos = new Vector2f(0, 0);
	private double angle = 0f;
	private boolean showHud = false;

	@Override
	public void onInitializeClient() {
		LOGGER.info("CrystalHollowsMap initialize");

        KeyBindingHelper.registerKeyBinding(SHOW_MAP);
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (SHOW_MAP.wasPressed()) {
				showHud = !showHud;
			}
		});
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			// Update player position and angle every tick
			if (showHud && client.player != null) {
				Vec3d pos = client.player.getPos();
				playerPos.x = (float)pos.x;
				playerPos.y = (float)pos.z;
				float yaw = client.player.getYaw();
				if (yaw < 0) {
					yaw += 360;
				}
				angle = Math.toRadians(yaw);
			}
		});

		RenderEvents.HUD.register(context -> {
			MSAAFramebuffer.use(8, () -> {
				if (!showHud) {
					return;
				}
				// Map size
				float width = 100f;
				float height = 100f;
				float halfW = width / 2;
				float halfH = height / 2;

				// Draw map
				Renderer2d.renderQuad(context.getMatrices(), Color.darkGray, 0f, 0f, width, height);
				Renderer2d.renderLine(context.getMatrices(), Color.black, 0, halfH, width, halfH);
				Renderer2d.renderLine(context.getMatrices(), Color.black, halfW, 0, halfW, height);
				Renderer2d.renderCircle(context.getMatrices(), new Color(200, 200, 255), halfW, halfH, 10, 50);

				// Define map boundaries and calculate relative player position
				float xStart = 200f;
				float yStart = 200f;
				float xEnd = 825f;
				float yEnd = 825f;
				float xPos = (playerPos.x - xStart) / (xEnd - xStart) * width;
				float yPos = (playerPos.y - yStart) / (yEnd - yStart) * height;

				// Define player arrow shape
				float scale = 5f;
				Vector2f pos = new Vector2f(xPos, yPos);
				Matrix2d rotationMatrix = new Matrix2d().rotation(angle);
				Vector2f line0 = new Vector2f(0f, 0.5f).mul(rotationMatrix).mul(scale).add(pos);
				Vector2f line1 = new Vector2f(0.4f, -0.5f).mul(rotationMatrix).mul(scale).add(pos);
				Vector2f line2 = new Vector2f(0f, -0.25f).mul(rotationMatrix).mul(scale).add(pos);
				Vector2f line3 = new Vector2f(-0.4f, -0.5f).mul(rotationMatrix).mul(scale).add(pos);

				// Draw player arrow
				Renderer2d.renderLine(context.getMatrices(), Color.black, line0.x ,line0.y, line1.x, line1.y);
				Renderer2d.renderLine(context.getMatrices(), Color.black, line1.x ,line1.y, line2.x, line2.y);
				Renderer2d.renderLine(context.getMatrices(), Color.black, line2.x ,line2.y, line3.x, line3.y);
				Renderer2d.renderLine(context.getMatrices(), Color.black, line3.x ,line3.y, line0.x, line0.y);
				CustomRenderer2d.renderPolygon(context.getMatrices(), Color.white, new Vector2f[] {line0, line1, line2, line3});
			});
		});
	}
}
