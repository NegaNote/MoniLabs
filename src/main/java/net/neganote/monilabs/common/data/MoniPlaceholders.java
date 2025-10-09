package net.neganote.monilabs.common.data;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.placeholder.*;
import com.gregtechceu.gtceu.api.placeholder.exceptions.NotSupportedException;
import com.gregtechceu.gtceu.api.placeholder.exceptions.PlaceholderException;

import net.minecraft.network.chat.Component;
import net.neganote.monilabs.common.machine.multiblock.MicroverseProjectorMachine;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;
import net.neganote.monilabs.common.machine.multiblock.SculkVatMachine;

import java.util.List;

import static net.neganote.monilabs.common.machine.multiblock.SculkVatMachine.XP_BUFFER_MAX;

public class MoniPlaceholders {

    public static void init() {
        PlaceholderHandler.addPlaceholder(new Placeholder("prismacColor") {

            @Override
            public MultiLineComponent apply(PlaceholderContext ctx,
                                            List<MultiLineComponent> args) throws PlaceholderException {
                PlaceholderUtils.checkArgs(args, 0);
                MetaMachine machine = MetaMachine.getMachine(ctx.level(), ctx.pos());
                if (!(machine instanceof PrismaticCrucibleMachine pcm)) {
                    throw new NotSupportedException();
                }
                var colorKey = pcm.getColorState().nameKey;
                return MultiLineComponent.of(Component.translatable("monilabs.prismatic.current_color",
                        Component.translatable(colorKey)));
            }
        });
        PlaceholderHandler.addPlaceholder(new Placeholder("microverseType") {

            @Override
            public MultiLineComponent apply(PlaceholderContext ctx,
                                            List<MultiLineComponent> args) throws PlaceholderException {
                PlaceholderUtils.checkArgs(args, 0);
                MetaMachine machine = MetaMachine.getMachine(ctx.level(), ctx.pos());
                if (!(machine instanceof MicroverseProjectorMachine projector)) {
                    throw new NotSupportedException();
                }
                var type = projector.getMicroverse();
                return MultiLineComponent.of(Component.translatable("microverse.monilabs.current_microverse",
                        Component.translatable(type.langKey)));
            }
        });
        PlaceholderHandler.addPlaceholder(new Placeholder("microverseStability") {

            @Override
            public MultiLineComponent apply(PlaceholderContext ctx,
                                            List<MultiLineComponent> args) throws PlaceholderException {
                PlaceholderUtils.checkArgs(args, 0);
                MetaMachine machine = MetaMachine.getMachine(ctx.level(), ctx.pos());
                if (!(machine instanceof MicroverseProjectorMachine projector)) {
                    throw new NotSupportedException();
                }
                var integrity = projector.getMicroverseIntegrity();
                return MultiLineComponent.of(Component.translatable("microverse.monilabs.integrity",
                        (float) integrity / MicroverseProjectorMachine.FLUX_REPAIR_AMOUNT));
            }
        });
        PlaceholderHandler.addPlaceholder(new Placeholder("sculkVatXPBuffer") {

            @Override
            public MultiLineComponent apply(PlaceholderContext ctx,
                                            List<MultiLineComponent> args) throws PlaceholderException {
                PlaceholderUtils.checkArgs(args, 0);
                MetaMachine machine = MetaMachine.getMachine(ctx.level(), ctx.pos());
                if (!(machine instanceof SculkVatMachine sculkVat)) {
                    throw new NotSupportedException();
                }
                var xpBuffer = sculkVat.getXpBuffer();
                return MultiLineComponent
                        .of(Component.translatable("sculk_vat.monilabs.current_xp_buffer", xpBuffer, XP_BUFFER_MAX));
            }
        });
    }
}
