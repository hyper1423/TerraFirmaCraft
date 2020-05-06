/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila.providers;

import java.util.List;
import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.api.capability.forge.CapabilityForgeable;
import net.dries007.tfc.api.capability.forge.IForgeable;
import net.dries007.tfc.api.capability.forge.IForgeableMeasurableMetal;
import net.dries007.tfc.api.recipes.BloomeryRecipe;
import net.dries007.tfc.compat.waila.interfaces.IWailaBlock;
import net.dries007.tfc.objects.blocks.devices.BlockBloomery;
import net.dries007.tfc.objects.blocks.property.ILightableBlock;
import net.dries007.tfc.objects.te.TEBloom;
import net.dries007.tfc.objects.te.TEBloomery;

public class BloomeryProvider implements IWailaBlock
{
    @Nonnull
    @Override
    public List<String> getBodyTooltip(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull List<String> currentTooltip, @Nonnull NBTTagCompound nbt)
    {
        IBlockState state = world.getBlockState(pos);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TEBloomery)
        {
            TEBloomery bloomery = (TEBloomery)tileEntity;
            if (state.getValue(ILightableBlock.LIT))
            {
                List<ItemStack> oreStacks = bloomery.getOreStacks();
                BloomeryRecipe recipe = oreStacks.size() > 0 ? BloomeryRecipe.get(oreStacks.get(0)) : null;
                long remainingMinutes = Math.round(bloomery.getBurnTicksLeft() / 1200.0f);
                currentTooltip.add(new TextComponentTranslation("waila.tfc.devices.remaining", remainingMinutes).getFormattedText());
                if (recipe != null)
                {
                    ItemStack output = recipe.getOutput(oreStacks);
                    IForgeable cap = output.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
                    if (cap instanceof IForgeableMeasurableMetal)
                    {
                        IForgeableMeasurableMetal forgeCap = ((IForgeableMeasurableMetal) cap);
                        currentTooltip.add(new TextComponentTranslation("waila.tfc.bloomery.output", forgeCap.getMetalAmount(), new TextComponentTranslation(forgeCap.getMetal().getTranslationKey()).getFormattedText()).getFormattedText());
                    }
                }
            }
            else
            {
                int ores = bloomery.getOreStacks().size();
                int fuel = bloomery.getFuelStacks().size();
                int max = BlockBloomery.getChimneyLevels(world, bloomery.getInternalBlock()) * 8;
                currentTooltip.add(new TextComponentTranslation("waila.tfc.bloomery.ores", ores, max).getFormattedText());
                currentTooltip.add(new TextComponentTranslation("waila.tfc.bloomery.fuel", fuel, max).getFormattedText());
            }
        }
        else if (tileEntity instanceof TEBloom)
        {
            TEBloom bloom = (TEBloom)tileEntity;
            IItemHandler cap = bloom.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if (cap != null)
            {
                ItemStack bloomStack = cap.getStackInSlot(0);
                IForgeable forgeCap = bloomStack.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
                if (forgeCap instanceof IForgeableMeasurableMetal)
                {
                    IForgeableMeasurableMetal bloomCap = ((IForgeableMeasurableMetal) forgeCap);
                    currentTooltip.add(new TextComponentTranslation("waila.tfc.metal.output", bloomCap.getMetalAmount(), new TextComponentTranslation(bloomCap.getMetal().getTranslationKey()).getFormattedText()).getFormattedText());
                }
            }
        }
        return currentTooltip;
    }

    @Nonnull
    @Override
    public List<Class<?>> getBodyClassList()
    {
        return ImmutableList.of(TEBloom.class, TEBloomery.class);
    }

    @Nonnull
    @Override
    public List<Class<?>> getNBTClassList()
    {
        return ImmutableList.of(TEBloom.class, TEBloomery.class);
    }
}