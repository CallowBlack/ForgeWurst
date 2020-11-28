/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import java.util.*;

import com.google.common.collect.BiMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.GameData;
import net.wurstclient.fmlevents.WGetAmbientOcclusionLightValueEvent;
import net.wurstclient.fmlevents.WRenderBlockModelEvent;
import net.wurstclient.fmlevents.WRenderTileEntityEvent;
import net.wurstclient.fmlevents.WSetOpaqueCubeEvent;
import net.wurstclient.fmlevents.WShouldSideBeRenderedEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.BlockListSetting;
import net.wurstclient.forge.utils.BlockUtils;

public final class XRayHack extends Hack
{
	private final BlockListSetting blocks = new BlockListSetting("Blocks",
		Blocks.COAL_ORE, Blocks.COAL_BLOCK, Blocks.IRON_ORE, Blocks.IRON_BLOCK,
		Blocks.GOLD_ORE, Blocks.GOLD_BLOCK, Blocks.LAPIS_ORE,
		Blocks.LAPIS_BLOCK, Blocks.REDSTONE_ORE, Blocks.LIT_REDSTONE_ORE,
		Blocks.REDSTONE_BLOCK, Blocks.DIAMOND_ORE, Blocks.DIAMOND_BLOCK,
		Blocks.EMERALD_ORE, Blocks.EMERALD_BLOCK, Blocks.QUARTZ_ORE,
		Blocks.LAVA, Blocks.MOB_SPAWNER, Blocks.PORTAL, Blocks.END_PORTAL,
		Blocks.END_PORTAL_FRAME);
	
	private ArrayList<String> blockNames;
	
	public XRayHack()
	{
		super("X-Ray", "Allows you to see ores through walls.");
		setCategory(Category.RENDER);
		addSetting(blocks);
	}
	
	@Override
	public String getRenderName()
	{
		return "X-Wurst";
	}
	
	@Override
	protected void onEnable()
	{
		blockNames = new ArrayList<>(blocks.getBlockNames());
		
		MinecraftForge.EVENT_BUS.register(this);
		mc.renderGlobal.loadRenderers();
	}
	
	@Override
	protected void onDisable()
	{
		MinecraftForge.EVENT_BUS.unregister(this);
		mc.renderGlobal.loadRenderers();
		
		if(!wurst.getHax().fullbrightHack.isEnabled())
			mc.gameSettings.gammaSetting = 0.5F;
	}
	
	@SubscribeEvent
	public void onUpdate(WUpdateEvent event)
	{
		mc.gameSettings.gammaSetting = 16;
	}
	
	@SubscribeEvent
	public void onSetOpaqueCube(WSetOpaqueCubeEvent event)
	{
		event.setCanceled(true);
	}
	
	@SubscribeEvent
	public void onGetAmbientOcclusionLightValue(
		WGetAmbientOcclusionLightValueEvent event)
	{
		event.setLightValue(1);
	}
	
	@SubscribeEvent
	public void onShouldSideBeRendered(WShouldSideBeRenderedEvent event)
	{
		event.setRendered(isVisible(event.getState()));
	}
	
	@SubscribeEvent
	public void onRenderBlockModel(WRenderBlockModelEvent event)
	{
		if(!isVisible(event.getState()))
			event.setCanceled(true);
	}
	
	@SubscribeEvent
	public void onRenderTileEntity(WRenderTileEntityEvent event)
	{
		if(!isVisible(event.getTileEntity().getBlockType().getStateFromMeta(event.getTileEntity().getBlockMetadata())))
			event.setCanceled(true);

	}
	
	private boolean isVisible(IBlockState blockState)
	{
		int indexMain = Collections.binarySearch(blockNames, BlockUtils.getMainName(blockState.getBlock()));
		int indexSubType = Collections.binarySearch(blockNames, BlockUtils.getSubName(blockState));
		return indexMain >= 0 || indexSubType >= 0;
	}
}
