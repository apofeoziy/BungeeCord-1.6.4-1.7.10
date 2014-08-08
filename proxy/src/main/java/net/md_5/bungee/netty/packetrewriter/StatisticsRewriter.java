package net.md_5.bungee.netty.packetrewriter;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.netty.PacketMapping;
import net.md_5.bungee.netty.Var;

public class StatisticsRewriter extends PacketRewriter
{

    @Override
    public void rewriteClientToServer(ByteBuf in, ByteBuf out)
    {
        unsupported( true );
    }

    @Override
    public void rewriteServerToClient(ByteBuf in, ByteBuf out)
    {
        int id = in.readInt();
        int amount = in.readInt();
        Var.writeVarInt(1, out);
        Var.writeString( PacketMapping.statistics[ id ], out, true );
        Var.writeVarInt( amount, out);
    }
}
