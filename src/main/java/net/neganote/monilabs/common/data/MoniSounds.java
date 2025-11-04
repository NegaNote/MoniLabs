package net.neganote.monilabs.common.data;

import com.gregtechceu.gtceu.api.sound.SoundEntry;

import net.neganote.monilabs.MoniLabs;

import static com.gregtechceu.gtceu.common.registry.GTRegistration.REGISTRATE;

public class MoniSounds {

    public static final SoundEntry MICROVERSE = REGISTRATE.sound(MoniLabs.id("microverse")).build();

    public static void init() {}
}
