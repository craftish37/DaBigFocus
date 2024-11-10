package net.craftish37.dabigfocus.fabric;

import net.craftish37.dabigfocus.DaBigFocus;
import net.fabricmc.api.ClientModInitializer;

public class DaBigFocusFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        DaBigFocus.init();
    }

}
