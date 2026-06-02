package kr.pyke.deceased_croa.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import kr.pyke.deceased_croa.data.MailboxData;
import kr.pyke.deceased_croa.registry.component.ModComponents;
import kr.pyke.deceased_croa.util.MailboxOpener;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class MailboxCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal("우편함").executes(MailboxCommand::openMailbox));
        dispatcher.register(Commands.literal("우편보내기")
            .requires(source -> source.hasPermission(2))
            .executes(MailboxCommand::openSendMailbox)
        );
        dispatcher.register(Commands.literal("우편테스트")
            .requires(source -> source.hasPermission(2))
            .executes(MailboxCommand::debug)
        );
    }

    public static int openMailbox(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        MailboxOpener.open(player);

        return 1;
    }

    public static int debug(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        MailboxData mailboxData = MailboxData.create("테스트", "시스템", "테스트용 메일입니다.", List.of(new ItemStack(Items.STONE, 64), new ItemStack(Items.STONE, 64), new ItemStack(Items.STONE, 64), new ItemStack(Items.STONE, 64), new ItemStack(Items.STONE, 64), new ItemStack(Items.STONE, 64), new ItemStack(Items.STONE, 64)));
        ModComponents.MAILBOX.get(player).addMail(mailboxData);

        return 1;
    }

    public static int openSendMailbox(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        MailboxOpener.openSend(player);

        return 1;
    }
}
