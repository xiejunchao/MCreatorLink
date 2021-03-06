/*
 * Copyright 2019 Pylo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.mcreator.minecraft.link.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.mcreator.minecraft.link.MCreatorLink;
import net.mcreator.minecraft.link.devices.raspberrypi.RaspberryPi;
import net.mcreator.minecraft.link.devices.raspberrypi.RaspberryPiDetector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT) public class GuiDirectLink extends Screen {

	private final Screen lastScreen;
	private TextFieldWidget ipTextField;

	private Button connect;

	GuiDirectLink(Screen lastScreenIn) {
		super(new StringTextComponent("Minecraft Link direct connect"));
		this.lastScreen = lastScreenIn;
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	@Override public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);

		drawCenteredString(matrixStack, this.font, new TranslationTextComponent("link.direct.title"), this.width / 2,
				20, 16777215);
		drawString(matrixStack, this.font, new TranslationTextComponent("link.direct.field"), this.width / 2 - 100, 100,
				10526880);

		this.ipTextField.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
	 * window resizes, the buttonList is cleared beforehand.
	 */
	@Override public void init(Minecraft minecraft, int width, int height) {
		super.init(minecraft, width, height);
		minecraft.keyboardListener.enableRepeatEvents(true);
		this.addButton(connect = new Button(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20,
				new TranslationTextComponent("link.direct.connect"), e -> {
			String device = this.ipTextField.getText();
			RaspberryPi raspberryPi = RaspberryPiDetector.getRaspberryPiForIP(device);
			if (raspberryPi != null) {
				MCreatorLink.LINK.setConnectedDevice(raspberryPi);
				if (this.minecraft != null) {
					this.minecraft.displayGuiScreen(this.lastScreen);
				}
			} else {
				this.ipTextField.setTextColor(0xff5d4d);
			}
		}));
		this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20,
				new TranslationTextComponent("gui.cancel"), e -> {
			if (this.minecraft != null) {
				this.minecraft.displayGuiScreen(this.lastScreen);
			}
		}));

		this.ipTextField = new TextFieldWidget(this.font, this.width / 2 - 100, 116, 200, 20,
				new StringTextComponent(""));

		connect.active = !this.ipTextField.getText().isEmpty() && this.ipTextField.getText().split(":").length > 0;

		this.ipTextField.setMaxStringLength(128);
		this.children.add(this.ipTextField);
		this.ipTextField.setFocused2(true);
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat events
	 */
	@Override public void onClose() {
		super.onClose();
		if (minecraft != null) {
			minecraft.keyboardListener.enableRepeatEvents(false);
		}
	}

	/**
	 * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
	 */
	@Override public boolean keyPressed(int typedChar, int keyCode, int par) {
		if (keyCode == 28 || keyCode == 156) {
			connect.onPress();
		} else if (this.ipTextField.keyPressed(typedChar, keyCode, par)) {
			this.ipTextField.setTextColor(0xffffff);
			connect.active = !this.ipTextField.getText().isEmpty() && this.ipTextField.getText().split(":").length > 0;
			return true;
		}

		connect.active = !this.ipTextField.getText().isEmpty() && this.ipTextField.getText().split(":").length > 0;
		return super.keyPressed(typedChar, keyCode, par);
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	@Override public void tick() {
		this.ipTextField.tick();
	}

}
