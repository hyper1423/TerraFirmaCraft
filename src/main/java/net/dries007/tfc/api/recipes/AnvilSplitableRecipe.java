/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.api.capability.forge.CapabilityForgeable;
import net.dries007.tfc.api.capability.forge.IForgeable;
import net.dries007.tfc.api.capability.forge.IForgeableMeasurable;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.util.forge.ForgeRule;

/*
 * Use this AnvilRecipe implementation if your ItemStack has metal amount to be split into smaller ItemStacks of itself
 */
public class AnvilSplitableRecipe extends AnvilMeasurableRecipe
{

    protected int splitAmount;

    public AnvilSplitableRecipe(ResourceLocation name, IIngredient<ItemStack> input, int splitAmount, Metal.Tier minTier, ForgeRule... rules) throws IllegalArgumentException
    {
        super(name, input, ItemStack.EMPTY, minTier, rules);
        this.splitAmount = splitAmount;
    }

    @Override
    public boolean matches(ItemStack input)
    {
        if (!super.matches(input)) return false;
        //Splitable if the output is at least two(don't change this or you will have duplicates)
        IForgeable cap = input.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
        if (cap instanceof IForgeableMeasurable)
            return splitAmount < ((IForgeableMeasurable) cap).getMetalAmount();
        return false;
    }


    @Override
    @Nonnull
    public NonNullList<ItemStack> getOutput(ItemStack input)
    {
        if (!matches(input)) return NonNullList.withSize(1, ItemStack.EMPTY);
        IForgeable inCap = input.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
        int metalAmount = ((IForgeableMeasurable) inCap).getMetalAmount();
        int surplus = metalAmount % splitAmount;
        int outCount = metalAmount / splitAmount;
        NonNullList<ItemStack> output = NonNullList.create();
        for (int i = 0; i < outCount; i++)
        {
            ItemStack dump = input.copy();
            IForgeable cap = dump.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
            cap.setWork(0); //Reset work without resetting temp
            cap.setRecipe((ResourceLocation) null);
            ((IForgeableMeasurable) cap).setMetalAmount(splitAmount);
            output.add(dump);
        }
        if (surplus > 0)
        {
            ItemStack dumpSurplus = input.copy();
            IForgeable cap = dumpSurplus.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
            cap.setWork(0); //Reset work without resetting temp
            cap.setRecipe((ResourceLocation) null);
            ((IForgeableMeasurable) cap).setMetalAmount(surplus);
            output.add(dumpSurplus);
        }
        return output;
    }
}