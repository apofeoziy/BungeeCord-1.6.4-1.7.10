package net.md_5.bungee.netty.packetrewriter;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.netty.Var;

public class ScoreboardObjectiveRewriter extends PacketRewriter
{

    @Override
    public void rewriteClientToServer(ByteBuf in, ByteBuf out)
    {
        unsupported( true );
    }

    @Override
    public void rewriteServerToClient(ByteBuf in, ByteBuf out)
    {
        String name = Var.readString( in, false );
        String value = Var.readString( in, false );
        byte createRemove = in.readByte();
        Var.writeString( name, out, true );
        Var.writeString( value, out, true );
        out.writeByte( createRemove );
    }

}
