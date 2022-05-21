package com.github.clevernucleus.playerex.client.gui;

import java.util.List;
import java.util.function.BiFunction;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.PoseStack;

import com.github.clevernucleus.playerex.api.ExAPI;
import com.github.clevernucleus.playerex.api.attribute.IPlayerAttributes;
import net.minecraft.client.gui.Font;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class DynamicTextComponent {
	private final float scale = 0.7F;
	private int posX, posY;
	private BiFunction<Player, IPlayerAttributes, String> titleText;
	private BiFunction<Player, IPlayerAttributes, List<Component>> hoverText;
	
	public DynamicTextComponent(int par0, int par1, BiFunction<Player, IPlayerAttributes, String> par2, BiFunction<Player, IPlayerAttributes, List<Component>> par3) {
		this.posX = par0;
		this.posY = par1;
		this.titleText = par2;
		this.hoverText = par3;
	}
	
	private boolean isHovered(int par0, int par1, int par2, int par3, int par4, int par5) {
		return (par0 >= par2 && par1 >= par3 && par0 < par2 + par4 && par1 < par3 + par5);
	}
	
	public void draw(PoseStack par0, Font par1, Player par2) {
		ExAPI.playerAttributes(par2).ifPresent(var -> {
			GL11.glPushMatrix();
			GL11.glScalef(this.scale, this.scale, this.scale);
			
			par1.draw(par0, this.titleText.apply(par2, var), this.posX, this.posY, 4210752);
			
			GL11.glPopMatrix();
		});
	}
	
	public void drawAlt(PoseStack par0, Font par1, Player par2, int par3, int par4, int par5, int par6) {
		int var0 = (int)((float)(par3 - 176) / 2.0F);
		int var1 = (int)((float)(par4 - 166) / 2.0F);
		
		ExAPI.playerAttributes(par2).ifPresent(var -> {
			if(isHovered(par5, par6, var0 + (int)(this.posX * this.scale), var1 + (int)(this.posY * this.scale), (int)(par1.width(this.titleText.apply(par2, var)) * this.scale), 7)) {
				GuiUtils.drawHoveringText(par0, this.hoverText.apply(par2, var), par5, par6, par3, par4, -1, par1);
			}
		});
	}
}
