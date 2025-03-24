package net.neganote.monilabs.data;

import net.neganote.monilabs.data.lang.MoniLangHandler;

import com.tterrag.registrate.providers.ProviderType;

import static net.neganote.monilabs.MoniLabs.REGISTRATE;

public class MoniDataGen {

    public static void init() {
        REGISTRATE.addDataGenerator(ProviderType.LANG, MoniLangHandler::init);
    }
}
