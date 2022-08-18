package net.u1f401.amogus;

import net.fabricmc.api.ClientModInitializer;
import net.u1f401.amogus.event.KeyInputHandler;

public class ModClient implements ClientModInitializer{
	@Override
	public void onInitializeClient(){
		KeyInputHandler.register();
	}
}