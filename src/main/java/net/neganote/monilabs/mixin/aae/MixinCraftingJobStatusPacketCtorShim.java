package net.neganote.monilabs.mixin.aae;

import appeng.core.sync.packets.CraftingJobStatusPacket;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CraftingJobStatusPacket.class)
public abstract class MixinCraftingJobStatusPacketCtorShim {}
