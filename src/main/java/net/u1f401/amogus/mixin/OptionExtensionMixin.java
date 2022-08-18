package net.u1f401.amogus.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.u1f401.amogus.event.KeyInputHandler;

@Mixin(SimpleOption.class)
public class OptionExtensionMixin<T>{
	@Shadow
	private T value;
	@Shadow @Final
	Text text;

	@SuppressWarnings("unchecked")
	@Inject(at = @At("HEAD"), method = "setValue", cancellable = true)
	public void setValue(T value, CallbackInfo info){
		if(KeyInputHandler.gammaboost && text.getString().equals(I18n.translate("options.gamma")))
			this.value = (T) Double.valueOf(Math.floor(100 * ((Double)value).doubleValue()) / 20);
		else this.value = value;
		info.cancel();
	}
}
