package kr.pyke.deceased_croa.mixin.server;

import kr.pyke.deceased_croa.DeceasedCroa;
import kr.pyke.deceased_croa.data.DisplayNameData;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntitySelectorParser.class)
public class EntitySelectorParserMixin {
    @ModifyVariable(method = "parseNameOrUUID", at = @At("STORE"))
    private String replaceDisplayNameWithRealName(String name) {
        if (null == name || name.isEmpty()) { return ""; }
        if (DeceasedCroa.SERVER_INSTANCE == null) { return name; }

        return DisplayNameData.getServerState(DeceasedCroa.SERVER_INSTANCE).getRealName(name);
    }
}