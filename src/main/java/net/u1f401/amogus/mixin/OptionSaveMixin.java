package net.u1f401.amogus.mixin;

import java.util.function.Function;
import java.util.function.IntSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.minecraft.client.option.SimpleOption.MaxSuppliableIntCallbacks;

@Mixin(MaxSuppliableIntCallbacks.class)
public class OptionSaveMixin{
	@Shadow
	int minInclusive;
	
	@Shadow
	IntSupplier maxSupplier;
	
	@Inject(at = @At("RETURN"), method = "codec", cancellable = true, remap = true)
    public Codec<Integer> codec(CallbackInfoReturnable<Codec<Integer>> info){
		System.out.println(info.getReturnValue());
		System.out.println("Hello your photons have viruses!");
        Function<Integer, DataResult<Integer>> correctfunction = value -> {
    		System.out.println("Hello your photons have nested viruses!");
            return DataResult.success(value);
        };
        info.setReturnValue(Codec.INT.flatXmap(correctfunction, correctfunction));
        info.cancel();
        return Codec.INT.flatXmap(correctfunction, correctfunction);
    }
}