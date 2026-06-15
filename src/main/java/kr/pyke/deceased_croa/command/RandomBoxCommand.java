package kr.pyke.deceased_croa.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import kr.pyke.PykeLib;
import kr.pyke.deceased_croa.manager.RandomBoxManager;
import kr.pyke.deceased_croa.network.pakcet.s2c.S2C_SyncRandomBoxPacket;
import kr.pyke.util.constants.COLOR;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;

public class RandomBoxCommand {
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_BOX_ID = (context, builder) -> SharedSuggestionProvider.suggest(RandomBoxManager.getDefinitions().keySet(), builder);

    private RandomBoxCommand() { }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal("randombox")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("reload").executes(RandomBoxCommand::reload))
            .then(Commands.literal("create")
                .then(Commands.argument("box_id", StringArgumentType.word()).suggests(SUGGEST_BOX_ID)
                    .then(Commands.argument("display_name", StringArgumentType.greedyString())
                        .executes(RandomBoxCommand::create)
                    )
                )
            )
            .then(Commands.literal("extract")
                .then(Commands.argument("box_id", StringArgumentType.word()).suggests(SUGGEST_BOX_ID)
                    .executes(RandomBoxCommand::extractRandomBox)
                )
            )
        );
    }

    private static int reload(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        int count = RandomBoxManager.reload();

        syncAll(context.getSource().getServer());

        ServerPlayer player = context.getSource().getPlayerOrException();

        PykeLib.sendSystemMessage(player, COLOR.LIME.getColor(), String.format("랜덤 상자 %s개를 불러왔습니다.", count));

        return 1;
    }

    private static int create(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String boxID = StringArgumentType.getString(context, "box_id");
        String displayName = StringArgumentType.getString(context, "display_name");

        List<ItemStack> contents = lookingAtContainerContents(player);
        if (contents == null) {
            PykeLib.sendSystemMessage(player, COLOR.RED.getColor(), "바라보고 있는 상자가 없습니다.");
            return 0;
        }

        RandomBoxManager.CreateResult result = RandomBoxManager.createFromContainer(boxID, displayName, contents);

        switch (result) {
            case ALREADY_EXISTS: {
                PykeLib.sendSystemMessage(player, COLOR.RED.getColor(), String.format("이미 존재하는 id 입니다: %s", boxID));
                return 0;
            }
            case EMPTY: {
                PykeLib.sendSystemMessage(player, COLOR.RED.getColor(), "상자가 비어 있습니다.");
                return 0;
            }
            case ERROR: {
                PykeLib.sendSystemMessage(player, COLOR.RED.getColor(), "파일을 생성하지 못했습니다.");
                return 0;
            }
            default: {
                break;
            }
        }

        int count = RandomBoxManager.reload();
        syncAll(context.getSource().getServer());

        PykeLib.sendSystemMessage(player, COLOR.LIME.getColor(), String.format("랜덤 상자 '%s' 를 생성했습니다. (총 %d개)", boxID, count));

        return 1;
    }

    private static int extractRandomBox(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String boxID = StringArgumentType.getString(context, "box_id");

        HitResult hitResult = player.pick(5.0, 1.f, false);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            PykeLib.sendSystemMessage(player, COLOR.RED.getColor(), "바라보고 있는 블록이 없습니다.");
            return 0;
        }

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        Container container = getFullContainer(player, blockHit.getBlockPos());

        if (container == null) {
            PykeLib.sendSystemMessage(player, COLOR.RED.getColor(), "바라보고 있는 블록이 상자가 아닙니다.");
            return 0;
        }

        RandomBoxManager.ExtractResult result = RandomBoxManager.extractToContainer(boxID, container);

        switch (result) {
            case NOT_FOUND: {
                PykeLib.sendSystemMessage(player, COLOR.RED.getColor(), String.format("존재하지 않는 상자 id 입니다: %s", boxID));
                return 0;
            }
            case NOT_ENOUGH_SPACE: {
                PykeLib.sendSystemMessage(player, COLOR.RED.getColor(), "상자의 공간이 부족하여 추출할 수 없습니다.");
                return 0;
            }
            case SUCCESS: {
                PykeLib.sendSystemMessage(player, COLOR.LIME.getColor(), String.format("랜덤 상자 '%s' 의 내용물을 성공적으로 꺼냈습니다.", boxID));
                break;
            }
        }

        return 1;
    }

    private static List<ItemStack> lookingAtContainerContents(ServerPlayer player) {
        HitResult hitResult = player.pick(5.0, 1.f, false);
        if (hitResult.getType() != HitResult.Type.BLOCK) { return null; }

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        Container container = getFullContainer(player, blockHit.getBlockPos());
        if (container == null) { return null; }

        List<ItemStack> contents = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                contents.add(stack.copy());
            }
        }

        return contents;
    }

    private static Container getFullContainer(ServerPlayer player, BlockPos pos) {
        BlockState state = player.level().getBlockState(pos);

        if (state.getBlock() instanceof ChestBlock chestBlock) {
            Container container = ChestBlock.getContainer(chestBlock, state, player.level(), pos, true);
            if (container != null) { return container; }
        }

        BlockEntity blockEntity = player.level().getBlockEntity(pos);
        return blockEntity instanceof Container c ? c : null;
    }

    private static void syncAll(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            S2C_SyncRandomBoxPacket.send(player);
        }
    }
}