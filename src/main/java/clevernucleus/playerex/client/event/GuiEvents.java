package clevernucleus.playerex.client.event;

import java.util.Map;

import clevernucleus.playerex.client.gui.OverlayScreen;
import clevernucleus.playerex.client.gui.TexturedButton;
import clevernucleus.playerex.common.PlayerEx;
import clevernucleus.playerex.common.init.Registry;
import clevernucleus.playerex.common.init.element.IElement;
import clevernucleus.playerex.common.init.item.RelicItem;
import clevernucleus.playerex.common.network.SwitchScreens;
import clevernucleus.playerex.common.rarity.Rareness;
import clevernucleus.playerex.common.util.ConfigSetting;
import clevernucleus.playerex.common.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.client.gui.CuriosScreen;

/**
 * Events holder on the FORGE bus for client side hooks.
 */
@Mod.EventBusSubscriber(modid = PlayerEx.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class GuiEvents {
	
	/** Custom overlay object. */
	private static OverlayScreen overlay = new OverlayScreen(() -> Minecraft.getInstance());
	
	/** Tests to see if LShift or RShift is being pressed. */
	public static boolean isShiftDown() {
		return InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 340) || InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 344);
	}
	
	/** Tests to see if LAlt or RAlt is being pressed. */
	public static boolean isAltDown() {
		return InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 342) || InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 346);
	}
	
	/**
	 * Event drawing item tooltips.
	 * @param par0
	 */
	@SubscribeEvent
	public static void onDrawTooltip(final ItemTooltipEvent par0) {
		ItemStack var0 = par0.getItemStack();
		
		if(var0.getItem() instanceof RelicItem) {
			if(!var0.hasTag() || !var0.getTag().contains("Elements") || !var0.getTag().contains("Rareness")) {
				par0.getToolTip().clear();
				par0.getToolTip().add(new TranslationTextComponent("tooltip.relic"));
			} else {
				Rareness var2 = Rareness.read(var0.getTag());
				
				par0.getToolTip().add(new StringTextComponent(""));
				par0.getToolTip().add(var2.getDisplayText());
				
				for(Map.Entry<IElement, Float> var : Util.attributeMap(var0.getTag()).entrySet()) {
					par0.getToolTip().add(new StringTextComponent(TextFormatting.GRAY + var.getKey().getTooltip(var.getValue()).getFormattedText()));
				}
			}
		}
	}
	
	/**
	 * Event for adding components to a container.
	 * @param par0
	 */
	@SubscribeEvent
	public static void onGuiInit(final GuiScreenEvent.InitGuiEvent.Post par0) {
		Screen var0 = par0.getGui();
		
		if(var0 instanceof InventoryScreen || var0 instanceof CuriosScreen) {
			ContainerScreen<?> var1 = (ContainerScreen<?>)var0;
			
			if(par0.getWidgetList() != null) {
				par0.addWidget(new TexturedButton(var1, 155, 7, 14, 13, 176, 0, 0, (var2, var3) -> {
					if(var2 instanceof InventoryScreen || var2 instanceof CuriosScreen) {
						Registry.NETWORK.sendToServer(new SwitchScreens(false));
					}
				}));
			}
		}
	}
	
	/**
	 * Event cancelling the vanilla gui.
	 * @param par0
	 */
	@SubscribeEvent
	public static void onHUDRender(final RenderGameOverlayEvent.Pre par0) {
		if(!ConfigSetting.CLIENT.enableGui.get().booleanValue() || overlay == null) return;
		
		ElementType var0 = par0.getType();
		
		if(var0 == ElementType.HEALTH || var0 == ElementType.ARMOR || var0 == ElementType.FOOD || var0 == ElementType.EXPERIENCE || var0 == ElementType.HEALTHMOUNT || var0 == ElementType.JUMPBAR || var0 == ElementType.AIR) {
			par0.setCanceled(true);
		}
		
		if(var0 != ElementType.HOTBAR) return;
		if(Minecraft.getInstance().player.isCreative()) return;
		
		overlay.draw(true);
	}
	
	/**
	 * Event rendering the font over the custom overlay.
	 * @param par0
	 */
	@SubscribeEvent
	public static final void onRenderIntercept(final RenderGameOverlayEvent.Post par0) {
		if(!ConfigSetting.CLIENT.enableGui.get().booleanValue() || overlay == null) return;
		if(par0.getType() != ElementType.HOTBAR) return;
		if(Minecraft.getInstance().player.isCreative()) return;
		
		overlay.draw(false);
	}
}